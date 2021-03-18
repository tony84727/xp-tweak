package com.github.tony84727.xptweak;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Status;
import discord4j.discordjson.json.gateway.ImmutableStatusUpdate;
import discord4j.gateway.GatewayClient;
import discord4j.rest.entity.RestChannel;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Mono;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(XpTweak.MOD_ID)
public class XpTweak {
    public static final String MOD_ID = "xptweak";
    // Directly reference a log4j logger.
    public static final XpTweakConfig CONFIG;
    public static final ForgeConfigSpec CONFIG_SPEC;

    static {
        final Pair<XpTweakConfig, ForgeConfigSpec> configPair = new ForgeConfigSpec.Builder().configure(XpTweakConfig::new);
        CONFIG = configPair.getLeft();
        CONFIG_SPEC = configPair.getRight();
    }

    public XpTweak() {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (s, b) -> true));
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, CONFIG_SPEC);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

//    public void onChecking(ModL)
}
