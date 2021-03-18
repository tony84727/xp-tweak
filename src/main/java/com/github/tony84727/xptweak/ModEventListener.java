package com.github.tony84727.xptweak;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.Channel;
import discord4j.rest.entity.RestChannel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.github.tony84727.xptweak.XpTweak.CONFIG;
import static com.github.tony84727.xptweak.XpTweak.CONFIG_SPEC;

@Mod.EventBusSubscriber(modid = XpTweak.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private static ServerEventListener listener;

    @SubscribeEvent
    public static void onModConfig(final ModConfig.ModConfigEvent event) {
        if (event.getConfig().getSpec() != CONFIG_SPEC) {
            return;
        }
        LOGGER.info("mod configured");
        if (listener != null) {
            MinecraftForge.EVENT_BUS.unregister(listener);
            listener = null;
        }
        final GatewayDiscordClient gateway = CONFIG.getDiscordClient().gateway().login().block();
        if (gateway == null) {
            LOGGER.warn("fail to login, please check if the bot token is valid");
            return;
        }
        final Channel channel = gateway.getChannelById(Snowflake.of(CONFIG.channelID.get())).block();
        if (channel == null) {
            LOGGER.warn("fail to retrieve given channel: {}", CONFIG.channelID.get());
            return;
        }
        final RestChannel restChannel = channel.getRestChannel();
        if (restChannel == null) {
            LOGGER.warn("unable to retrieve channel: {}", CONFIG.channelID.get());
            return;
        }
        DiscordEventReporter discordEventReporter = new DiscordEventReporter(restChannel);
        listener = new ServerEventListener(discordEventReporter, gateway.on(MessageCreateEvent.class));
        MinecraftForge.EVENT_BUS.register(listener);
    }
}
