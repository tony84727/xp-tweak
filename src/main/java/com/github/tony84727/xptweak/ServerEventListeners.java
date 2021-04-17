package com.github.tony84727.xptweak;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import reactor.extra.processor.TopicProcessor;

public class ServerEventListeners {
    private final TopicProcessor<PlayerEvent.PlayerLoggedInEvent> playerLoggedInEventProcessor = TopicProcessor.create();
    private final TopicProcessor<PlayerEvent.PlayerLoggedOutEvent> playerLoggedOutEventProcessor = TopicProcessor.create();
    private final TopicProcessor<LivingDeathEvent> livingDeathEventProcessor = TopicProcessor.create();
    private final TopicProcessor<AdvancementEvent> advancementEventProcessor = TopicProcessor.create();
    private final TopicProcessor<ServerChatEvent> serverChantEventProcessor = TopicProcessor.create();
    private final TopicProcessor<FMLServerStartedEvent> serverStartedEventProcessor = TopicProcessor.create();
    private final TopicProcessor<FMLServerStartingEvent> serverStartingEventProcessor = TopicProcessor.create();
    private final TopicProcessor<FMLServerStoppedEvent> serverStoppedEventProcessor = TopicProcessor.create();

    public TopicProcessor<PlayerEvent.PlayerLoggedInEvent> getPlayerLoggedInEventProcessor() {
        return playerLoggedInEventProcessor;
    }

    public TopicProcessor<PlayerEvent.PlayerLoggedOutEvent> getPlayerLoggedOutEventProcessor() {
        return playerLoggedOutEventProcessor;
    }

    public TopicProcessor<LivingDeathEvent> getLivingDeathEventProcessor() {
        return livingDeathEventProcessor;
    }

    public TopicProcessor<AdvancementEvent> getAdvancementEventProcessor() {
        return advancementEventProcessor;
    }

    public TopicProcessor<ServerChatEvent> getServerChantEventProcessor() {
        return serverChantEventProcessor;
    }

    public TopicProcessor<FMLServerStartedEvent> getServerStartedEventProcessor() {
        return serverStartedEventProcessor;
    }

    public TopicProcessor<FMLServerStartingEvent> getServerStartingEventProcessor() {
        return serverStartingEventProcessor;
    }

    public TopicProcessor<FMLServerStoppedEvent> getServerStoppedEventProcessor() {
        return serverStoppedEventProcessor;
    }

    public void attach() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SuppressWarnings("unused")
    public void detach() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
        playerLoggedInEventProcessor.onNext(event);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerLoggedOut(final PlayerEvent.PlayerLoggedOutEvent event) {
        playerLoggedOutEventProcessor.onNext(event);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onLivingDeath(final LivingDeathEvent event) {
        livingDeathEventProcessor.onNext(event);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onAdvancement(final AdvancementEvent event) {
        advancementEventProcessor.onNext(event);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onChatEvent(final ServerChatEvent event) {
        serverChantEventProcessor.onNext(event);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onServerStarting(final FMLServerStartingEvent event) {
        serverStartingEventProcessor.onNext(event);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onServerStarted(final FMLServerStartedEvent event) {
        serverStartedEventProcessor.onNext(event);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onServerStopped(final FMLServerStoppedEvent event) {
        serverStoppedEventProcessor.onNext(event);
    }
}
