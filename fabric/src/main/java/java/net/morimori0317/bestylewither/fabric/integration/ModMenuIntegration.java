package net.morimori0317.bestylewither.fabric.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.morimori0317.bestylewither.BEStyleWither;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (BEStyleWither.getConfig() instanceof net.morimori0317.bestylewither.fabric.BESConfigFabric)
            return net.morimori0317.bestylewither.fabric.BESConfigFabric::createConfigScreen;
        return ModMenuApi.super.getModConfigScreenFactory();
    }
}