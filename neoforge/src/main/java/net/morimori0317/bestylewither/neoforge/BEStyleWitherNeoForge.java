package net.morimori0317.bestylewither.neoforge;

import net.morimori0317.bestylewither.BEStyleWither;
import net.morimori0317.bestylewither.neoforge.networking.BSWPacketsNeoForge;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(BEStyleWither.MODID)
public final class BEStyleWitherNeoForge {
    public static final BESConfigNeoForge CONFIG = new BESConfigNeoForge();

    public BEStyleWitherNeoForge(ModContainer container) {
        BESConfigNeoForge.init(container);
        BEStyleWither.init();
        BSWPacketsNeoForge.init();
    }
}
