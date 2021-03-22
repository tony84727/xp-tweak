package com.github.tony84727.xptweak;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ReplayProcessor;

import static com.github.tony84727.xptweak.XpTweak.CONFIG;
import static com.github.tony84727.xptweak.XpTweak.CONFIG_SPEC;

@Mod.EventBusSubscriber(modid = XpTweak.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ReplayProcessor<DiscordClient> discordClientFlux = ReplayProcessor.create(1);
    private static final Flux<GatewayDiscordClient> gatewayFlux = discordClientFlux.switchMap(discordClient -> discordClient.gateway().login());
    private static final ReplayProcessor<Snowflake> targetChannelFlux = ReplayProcessor.create(1);
    private static final Mono<ServerEventListeners> listenerMono = Mono.defer(() -> Mono.create(serverEventListenersMonoSink -> {
        final ServerEventListeners listeners = new ServerEventListeners();
        listeners.attach();
        serverEventListenersMonoSink.success(listeners);
    }));

    static {
        DiscordRelay.start(gatewayFlux, targetChannelFlux.distinct(), listenerMono);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public static void onModConfig(final ModConfig.ModConfigEvent event) {
        if (event.getConfig().getSpec() != CONFIG_SPEC) {
            return;
        }
        LOGGER.info("bot configured");
        targetChannelFlux.onNext(Snowflake.of(CONFIG.channelID.get()));
        discordClientFlux.onNext(CONFIG.getDiscordClient());
    }
}
