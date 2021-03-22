package com.github.tony84727.xptweak;

import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class DiscordMessageDisplayFormatter {
    public static String formatChat(ServerChatEvent event) {
        return String.format("%s說：%s", event.getPlayer().getDisplayName().getString(), event.getMessage());
    }

    public static String formatLoggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
        return String.format("%s 加入了遊戲！", event.getPlayer().getDisplayName().getString());
    }

    public static String formatLoggedOut(final PlayerEvent.PlayerLoggedOutEvent event) {
        return String.format("%s 離開了遊戲！", event.getPlayer().getDisplayName().getString());
    }
}
