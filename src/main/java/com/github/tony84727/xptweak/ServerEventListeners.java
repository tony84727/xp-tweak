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
import reactor.core.publisher.EmitterProcessor;

public class ServerEventListeners {
    private final EmitterProcessor<PlayerEvent.PlayerLoggedInEvent> playerLoggedInEventProcessor = EmitterProcessor.create();
    private final EmitterProcessor<PlayerEvent.PlayerLoggedOutEvent> playerLoggedOutEventProcessor = EmitterProcessor.create();
    private final EmitterProcessor<LivingDeathEvent> livingDeathEventProcessor = EmitterProcessor.create();
    private final EmitterProcessor<AdvancementEvent> advancementEventProcessor = EmitterProcessor.create();
    private final EmitterProcessor<ServerChatEvent> serverChantEventProcessor = EmitterProcessor.create();
    private final EmitterProcessor<FMLServerStartedEvent> serverStartedEventProcessor = EmitterProcessor.create();
    private final EmitterProcessor<FMLServerStartingEvent> serverStartingEventEmitterProcessor = EmitterProcessor.create();
    private final EmitterProcessor<FMLServerStoppedEvent> serverStoppedEventEmitterProcessor = EmitterProcessor.create();

    public EmitterProcessor<PlayerEvent.PlayerLoggedInEvent> getPlayerLoggedInEventProcessor() {
        return playerLoggedInEventProcessor;
    }

    public EmitterProcessor<PlayerEvent.PlayerLoggedOutEvent> getPlayerLoggedOutEventProcessor() {
        return playerLoggedOutEventProcessor;
    }

    public EmitterProcessor<LivingDeathEvent> getLivingDeathEventProcessor() {
        return livingDeathEventProcessor;
    }

    public EmitterProcessor<AdvancementEvent> getAdvancementEventProcessor() {
        return advancementEventProcessor;
    }

    public EmitterProcessor<ServerChatEvent> getServerChantEventProcessor() {
        return serverChantEventProcessor;
    }

    public EmitterProcessor<FMLServerStartedEvent> getServerStartedEventProcessor() {
        return serverStartedEventProcessor;
    }

    public EmitterProcessor<FMLServerStartingEvent> getServerStartingEventEmitterProcessor() {
        return serverStartingEventEmitterProcessor;
    }

    public EmitterProcessor<FMLServerStoppedEvent> getServerStoppedEventEmitterProcessor() {
        return serverStoppedEventEmitterProcessor;
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
        serverStartingEventEmitterProcessor.onNext(event);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onServerStarted(final FMLServerStartedEvent event) {
        serverStartedEventProcessor.onNext(event);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onServerStopped(final FMLServerStoppedEvent event) {
        serverStoppedEventEmitterProcessor.onNext(event);
    }
}
