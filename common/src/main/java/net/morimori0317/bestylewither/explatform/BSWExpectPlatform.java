package net.morimori0317.bestylewither.explatform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.morimori0317.bestylewither.config.BESConfig;

public class BSWExpectPlatform {
    @ExpectPlatform
    public static void sendWhitherChargePacket(ServerLevel level, ChunkPos chunkPos, int entityId) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static BESConfig getConfig() {
        throw new AssertionError();
    }
}
