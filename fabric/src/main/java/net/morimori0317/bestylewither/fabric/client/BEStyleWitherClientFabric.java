package net.morimori0317.bestylewither.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.morimori0317.bestylewither.client.BEStyleWitherClient;
import net.morimori0317.bestylewither.fabric.networking.BSWPacketsFabric;

public class BEStyleWitherClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BEStyleWitherClient.init();
        BSWPacketsFabric.clientInit();
    }
}
