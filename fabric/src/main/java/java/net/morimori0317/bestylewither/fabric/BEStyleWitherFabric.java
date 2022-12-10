package net.morimori0317.bestylewither.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.morimori0317.bestylewither.BEStyleWither;
import net.morimori0317.bestylewither.config.BESConfig;

public class BEStyleWitherFabric implements ModInitializer {
    private static net.morimori0317.bestylewither.fabric.BESConfigFabric CONFIG;

    @Override
    public void onInitialize() {
        BEStyleWither.init();
    }

    public static BESConfig getConfig() {
        if (CONFIG == null && FabricLoader.getInstance().isModLoaded("cloth-config"))
            CONFIG = net.morimori0317.bestylewither.fabric.BESConfigFabric.createConfig();

        if (CONFIG != null)
            return CONFIG;
        return BESConfig.DEFAULT;
    }
}
