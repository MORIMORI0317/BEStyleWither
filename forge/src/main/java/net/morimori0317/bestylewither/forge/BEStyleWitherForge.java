package net.morimori0317.bestylewither.forge;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.morimori0317.bestylewither.BEStyleWither;
import net.morimori0317.bestylewither.client.BEStyleWitherClient;
import net.morimori0317.bestylewither.forge.networking.BSWPacketsForge;

@Mod(BEStyleWither.MODID)
public class BEStyleWitherForge {
    public static final BESConfigForge CONFIG = new BESConfigForge();

    public BEStyleWitherForge() {
        BESConfigForge.init();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        BEStyleWither.init();
        BSWPacketsForge.init();
    }

    private void doClientStuff(FMLClientSetupEvent event) {
        BEStyleWitherClient.init();
    }
}
