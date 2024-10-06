package net.morimori0317.bestylewither.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.morimori0317.bestylewither.fabric.networking.BSWPacketsFabric;

public final class BEStyleWitherFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BSWPacketsFabric.clientInit();
    }
}
