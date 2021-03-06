package com.github.tony84727.xptweak;

import com.github.tony84727.xptweak.integration.BackupFinishedEvent;
import com.github.tony84727.xptweak.integration.BackupStartedEvent;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.discordjson.json.EmbedData;
import discord4j.discordjson.json.MessageData;
import discord4j.rest.entity.RestChannel;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.commons.lang3.tuple.Pair;
import reactor.core.Disposable;
import reactor.core.Disposables;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

public class DiscordRelay {
    public static Scheduler discordScheduler = Schedulers.newElastic("xptweakdiscord");

    public static Disposable start(
            Flux<GatewayDiscordClient> gatewayFlux,
            Flux<Snowflake> targetChannelFlux,
            Mono<ServerEventListeners> listenerFlux) {
        final Flux<RestChannel> restChannel = Flux.combineLatest(gatewayFlux, targetChannelFlux, (client, channelID) -> client.rest().getChannelById(channelID));
        final Flux<DiscordMessage> channelMessage = Flux.combineLatest(
                gatewayFlux.flatMap(client -> client.on(MessageCreateEvent.class)),
                targetChannelFlux,
                Pair::of)
                .filter((pair) -> {
                    final MessageCreateEvent event = pair.getLeft();
                    final Snowflake channelID = pair.getRight();
                    return event.getMessage().getChannelId().equals(channelID) && event.getMember().map(member -> !member.isBot()).orElse(false);
                })
                .map(Pair::getLeft)
                .map(event -> new DiscordMessage(event.getMember().map(Member::getDisplayName).orElse("???????????????"), event.getMessage().getContent()));
        final Flux<ServerChatEvent> chatEvent = listenerFlux.flatMapMany(ServerEventListeners::getServerChantEventProcessor);

        final Flux<FMLServerStartedEvent> startedEventFlux = listenerFlux.flatMapMany(ServerEventListeners::getServerStartedEventProcessor);
        final Flux<FMLServerStartingEvent> startingEventFlux = listenerFlux.flatMapMany(ServerEventListeners::getServerStartingEventProcessor);
        final Flux<FMLServerStoppedEvent> stoppedEventFlux = listenerFlux.flatMapMany(ServerEventListeners::getServerStoppedEventProcessor);

        final Flux<PlayerEvent.PlayerLoggedInEvent> playerLoggedInFlux = listenerFlux.flatMapMany(ServerEventListeners::getPlayerLoggedInEventProcessor);
        final Flux<PlayerEvent.PlayerLoggedOutEvent> playerLoggedOutFlux = listenerFlux.flatMapMany(ServerEventListeners::getPlayerLoggedOutEventProcessor);
        final Flux<LivingDeathEvent> playerDeathFlux = listenerFlux.flatMapMany(ServerEventListeners::getLivingDeathEventProcessor).filter(livingDeathEvent -> {
            final Entity entity = livingDeathEvent.getEntity();
            if (entity == null) {
                return false;
            }
            return entity.getType().equals(EntityType.PLAYER);
        });
        final Flux<AdvancementEvent> advancementEventFlux = listenerFlux.flatMapMany(ServerEventListeners::getAdvancementEventProcessor);

        final UUID discordSenderID = UUID.randomUUID();
        final Flux<String> discordTextMessage = Flux.merge(
                chatEvent.map(DiscordMessageDisplayFormatter::formatChat),
                startingEventFlux.map(fmlServerStartingEvent -> "??????????????????..."),
                startedEventFlux.map(fmlServerStartedEvent -> "?????????????????????"),
                stoppedEventFlux.map(fmlServerStoppedEvent -> "?????????????????????"),
                playerLoggedInFlux.map(DiscordMessageDisplayFormatter::formatLoggedIn),
                playerLoggedOutFlux.map(DiscordMessageDisplayFormatter::formatLoggedOut),
                playerDeathFlux.map(livingDeathEvent -> livingDeathEvent.getSource().getDeathMessage(livingDeathEvent.getEntityLiving()).getString()),
                advancementEventFlux.filter(event -> {
                    final DisplayInfo displayInfo = event.getAdvancement().getDisplay();
                    if (displayInfo == null) {
                        return false;
                    }
                    return displayInfo.shouldAnnounceToChat();
                }).map(DiscordMessageDisplayFormatter::formatAdvancement));
        final Flux<BackupStartedEvent> backupStartedEventFlux = listenerFlux.flatMapMany(ServerEventListeners::getBackupStartedEventFlux);
        final Flux<BackupFinishedEvent> backupFinishedEventFlux = listenerFlux.flatMapMany(ServerEventListeners::getBackupFinishedEventFlux);
        final Flux<EmbedData> backupEventMessageFlux = Flux.merge(
                backupStartedEventFlux.map(DiscordMessageDisplayFormatter::formatBackupStarted),
                backupFinishedEventFlux.map(DiscordMessageDisplayFormatter::formatBackupFinished)
        );
        final Flux<MessageData> embedMessageFlux = backupEventMessageFlux.withLatestFrom(restChannel, ((embedData, channel) ->
                channel.createMessage(embedData)
        )).flatMap((messageDataMono -> messageDataMono));
        final Flux<MessageData> discordMessageFlux = discordTextMessage.withLatestFrom(restChannel, ((message, channel) ->
                channel.createMessage(message)
        )).flatMap((messageDataMono -> messageDataMono));
        return Disposables.composite(
                Flux.merge(
                        embedMessageFlux,
                        discordMessageFlux
                ).subscribeOn(discordScheduler).subscribe(),
                channelMessage.subscribe(discordMessage -> {
                    final MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                    if (server == null) {
                        return;
                    }
                    server.getPlayerList().getPlayers().forEach(player -> player.sendMessage(ServerMessageDisplayFormatter.format(discordMessage), discordSenderID));
                }));
    }
}
