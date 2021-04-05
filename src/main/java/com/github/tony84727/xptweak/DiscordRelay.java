package com.github.tony84727.xptweak;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
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

import java.util.UUID;

public class DiscordRelay {
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
                .map(event -> new DiscordMessage(event.getMember().map(Member::getDisplayName).orElse("不知道是誰"), event.getMessage().getContent()));
        final Flux<ServerChatEvent> chatEvent = listenerFlux.flatMapMany(ServerEventListeners::getServerChantEventProcessor);

        final Flux<FMLServerStartedEvent> startedEventFlux = listenerFlux.flatMapMany(ServerEventListeners::getServerStartedEventProcessor);
        final Flux<FMLServerStartingEvent> startingEventFlux = listenerFlux.flatMapMany(ServerEventListeners::getServerStartingEventEmitterProcessor);
        final Flux<FMLServerStoppedEvent> stoppedEventFlux = listenerFlux.flatMapMany(ServerEventListeners::getServerStoppedEventEmitterProcessor);

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
                startingEventFlux.map(fmlServerStartingEvent -> "伺服器啟動中..."),
                startedEventFlux.map(fmlServerStartedEvent -> "伺服器已啟動！"),
                stoppedEventFlux.map(fmlServerStoppedEvent -> "伺服器已停機！"),
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
        final Flux<MessageData> discordMessageFlux = discordTextMessage.withLatestFrom(restChannel, Pair::of).flatMap(
                (pair) -> {
                    final String message = pair.getLeft();
                    final RestChannel channel = pair.getRight();
                    return channel.createMessage(message);
                });
        return Disposables.composite(
                discordMessageFlux.subscribe(),
                channelMessage.subscribe(discordMessage -> {
                    final MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                    if (server == null) {
                        return;
                    }
                    server.getPlayerList().getPlayers().forEach(player -> player.sendMessage(ServerMessageDisplayFormatter.format(discordMessage), discordSenderID));
                }));
    }
}
