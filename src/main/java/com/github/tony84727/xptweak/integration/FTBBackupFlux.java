package com.github.tony84727.xptweak.integration;

import com.feed_the_beast.mods.ftbbackups.Backup;
import com.feed_the_beast.mods.ftbbackups.BackupEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.Date;
import java.util.function.Consumer;

public class FTBBackupFlux {
    public static final Flux<BackupStartedEvent> preBackupFlux = Flux.create((emitter) -> {
        final Consumer<BackupEvent.Pre> listener = (event) -> emitter.next(new BackupStartedEvent());
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, listener);
    });
    public static final Flux<BackupFinishedEvent> postBackupFlux = Flux.create((emitter) -> {
        final Consumer<BackupEvent.Post> listener = (event) -> {
            final Backup backup = event.getBackup();
            final Date createdAt = Date.from(Instant.ofEpochMilli(backup.time));
            emitter.next(new BackupFinishedEvent(createdAt, backup.size));
        };
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, listener);
    });
}
