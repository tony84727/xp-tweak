package com.github.tony84727.xptweak;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;

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
}
