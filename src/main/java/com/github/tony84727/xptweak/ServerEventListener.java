package com.github.tony84727.xptweak;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import reactor.core.publisher.Flux;

public class ServerEventListener {

    private final DiscordEventReporter discordReporter;
    private final Flux<MessageCreateEvent> messages;

    public ServerEventListener(DiscordEventReporter reporter, Flux<MessageCreateEvent> messages) {
        this.discordReporter = reporter;
        this.messages = messages;
    }

    @SubscribeEvent
    public void onMessage(final ServerChatEvent event) {
        this.discordReporter.sendMessageChat(event);
    }

    @SubscribeEvent
    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        this.discordReporter.annouceEvent("伺服器正在啟動....");
    }

    @SubscribeEvent
    public void onServerStarted(FMLServerStartedEvent event) {
        this.discordReporter.annouceEvent("伺服器已啟動！");
        final ServerEventReporter serverReporter = new ServerEventReporter(event.getServer());
        this.messages.filter(event1 -> event1.getMember().map(member -> !member.isBot()).orElse(false)
        ).subscribe(serverReporter::sendChat);
    }

    @SubscribeEvent
    public void onServerStopped(FMLServerStoppedEvent event) {
        this.discordReporter.annouceEvent("伺服器已停機！");
    }
}
