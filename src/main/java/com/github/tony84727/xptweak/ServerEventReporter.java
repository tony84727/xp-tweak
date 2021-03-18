package com.github.tony84727.xptweak;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ServerEventReporter {
    private final MinecraftServer server;

    public ServerEventReporter(MinecraftServer server) {
        this.server = server;
    }

    public void sendChat(MessageCreateEvent event) {
        broadcastMessage(formatMessageEvent(event));
    }

    private void broadcastMessage(ITextComponent message) {
        this.server.getPlayerList().getPlayers().forEach(player -> player.sendMessage(message, player.getUniqueID()));
    }

    private static ITextComponent formatMessageEvent(MessageCreateEvent event) {
        return new StringTextComponent(String.format("[Discord] %s 說： %s", event.getMember().map(Member::getDisplayName).orElse("不知道是誰"), event.getMessage().getContent()));
    }
}
