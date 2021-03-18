package com.github.tony84727.xptweak;

import discord4j.core.object.entity.channel.Channel;
import discord4j.rest.entity.RestChannel;
import net.minecraftforge.event.ServerChatEvent;

// Report game events to Discord
public class DiscordEventReporter {
    private final RestChannel channel;

    public DiscordEventReporter(RestChannel channel) {
        this.channel = channel;
    }

    public void annouceEvent(String event) {
        sendTextMessage(event);
    }

    public void sendMessageChat(ServerChatEvent event) {
        sendTextMessage(formatMinecraftChat(event));
    }

    private void sendTextMessage(String message) {
        this.channel.createMessage(message).subscribe();
    }

    private static String formatMinecraftChat(ServerChatEvent event) {
        return String.format("%s 說： %s", event.getUsername(), event.getMessage());
    }
}
