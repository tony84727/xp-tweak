package com.github.tony84727.xptweak.integration;

import java.util.Date;

public class BackupFinishedEvent {
    public final Date createdAt;
    public final long size;

    public BackupFinishedEvent(Date createdAt, long size) {
        this.createdAt = createdAt;
        this.size = size;
    }
}
