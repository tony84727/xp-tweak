package com.github.tony84727.xptweak;

import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DiscordMessageDisplayFormatterTest {
    @Test
    public void testDateFormatter() {
        final SimpleDateFormat dateFormat = DiscordMessageDisplayFormatter.getSimpleDataFormat();
        final Date date = Date.from(Instant.ofEpochMilli(1618721931000L));
        assertEquals("2021/04/18 12:58:51", dateFormat.format(date));
    }
}
