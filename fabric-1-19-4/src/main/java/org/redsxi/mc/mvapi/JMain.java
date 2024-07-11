package org.redsxi.mc.mvapi;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;

public class JMain implements ModInitializer, ClientModInitializer, DedicatedServerModInitializer {
    private final Main main = new Main();

    public void onInitializeClient() {
        main.initializeClient();
    }

    public void onInitializeServer() {
        main.initializeServer();
    }

    public void onInitialize() {
        main.initialize();
    }
}
