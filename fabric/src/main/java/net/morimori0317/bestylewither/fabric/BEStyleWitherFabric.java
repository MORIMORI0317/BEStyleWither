package net.morimori0317.bestylewither.fabric;

import net.fabricmc.api.ModInitializer;
import net.morimori0317.bestylewither.BEStyleWither;

public class BEStyleWitherFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        BEStyleWither.init();
    }
}
