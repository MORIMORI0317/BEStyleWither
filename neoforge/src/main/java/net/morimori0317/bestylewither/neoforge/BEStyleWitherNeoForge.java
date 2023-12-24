package net.morimori0317.bestylewither.neoforge;

import net.morimori0317.bestylewither.BEStyleWither;
import net.morimori0317.bestylewither.client.BEStyleWitherClient;
import net.morimori0317.bestylewither.neoforge.networking.BSWPacketsNeoForge;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BEStyleWither.MODID)
public class BEStyleWitherNeoForge {
    public static final BESConfigNeoForge CONFIG = new BESConfigNeoForge();

    public BEStyleWitherNeoForge() {
        BESConfigNeoForge.init();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        BEStyleWither.init();
        BSWPacketsNeoForge.init();
    }

    private void doClientStuff(FMLClientSetupEvent event) {
        BEStyleWitherClient.init();
    }
}
