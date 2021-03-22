package com.github.tony84727.xptweak;

import discord4j.core.DiscordClient;
import net.minecraftforge.common.ForgeConfigSpec;

public class XpTweakConfig {
    private final ForgeConfigSpec.ConfigValue<String> botToken;
    public final ForgeConfigSpec.ConfigValue<Long> channelID;

    public XpTweakConfig(ForgeConfigSpec.Builder builder) {
        builder.push("discord");
        botToken = builder.comment("Discord bot token. It's secret, do not share with others")
            .define("botToken", "yourDiscordBotTokenHere");
        channelID = builder.comment("Discord bot channel").define("channelID",  0L);
        builder.pop();
    }

    public DiscordClient getDiscordClient() {
        return DiscordClient.create(this.botToken.get());
    }
}
