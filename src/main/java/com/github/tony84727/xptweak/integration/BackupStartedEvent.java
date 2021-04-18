package com.github.tony84727.xptweak.integration;

import java.util.Date;

public class BackupStartedEvent {
    public final Date startedAt;

    public BackupStartedEvent(final Date startedAt) {
        this.startedAt = startedAt;
    }

    public BackupStartedEvent() {
        this(new Date());
    }
}
