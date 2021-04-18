package com.github.tony84727.xptweak;

import com.github.tony84727.xptweak.integration.BackupFinishedEvent;
import com.github.tony84727.xptweak.integration.BackupStartedEvent;
import discord4j.discordjson.json.EmbedData;
import discord4j.discordjson.json.ImmutableEmbedData;
import discord4j.discordjson.json.ImmutableEmbedFieldData;
import discord4j.rest.util.Color;
import net.minecraft.advancements.DisplayInfo;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.apache.commons.io.FileUtils;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

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

    public static String formatAdvancement(final AdvancementEvent event) {
        final DisplayInfo displayInfo = event.getAdvancement().getDisplay();
        String description = "";
        if (displayInfo != null) {
            description = "[" + displayInfo.getDescription().getString() + "]";
        }
        return String.format("%s 達成 %s %s", event.getPlayer().getDisplayName().getString(), event.getAdvancement().getDisplayText().getString(), description);
    }

    public static SimpleDateFormat getSimpleDataFormat() {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
        return dateFormat;
    }

    public static EmbedData formatBackupStarted(final BackupStartedEvent event) {
        final SimpleDateFormat dateFormat = getSimpleDataFormat();
        return ImmutableEmbedData.builder()
                .type("rich")
                .title("伺服器開始備份")
                .description("伺服器備份中，可能會LAG")
                .color(Color.YELLOW.getRGB())
                .addField(
                        ImmutableEmbedFieldData.builder().name("時間點").value(dateFormat.format(event.startedAt)).build()
                )
                .build();
    }

    public static EmbedData formatBackupFinished(final BackupFinishedEvent event) {
        final SimpleDateFormat dateFormat = getSimpleDataFormat();
        return ImmutableEmbedData.builder()
                .type("rich")
                .title("伺服器已完成備份")
                .description("伺服器已備份完畢，請玩家安心遊玩")
                .color(Color.GREEN.getRGB())
                .addField(
                        ImmutableEmbedFieldData.builder().name("時間點").value(dateFormat.format(event.createdAt)).build()
                )
                .addField(ImmutableEmbedFieldData.builder().name("大小").value(FileUtils.byteCountToDisplaySize(event.size)).build())
                .build();
    }
}
