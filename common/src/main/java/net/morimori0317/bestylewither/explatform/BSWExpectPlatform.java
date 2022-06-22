package net.morimori0317.bestylewither.explatform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;

public class BSWExpectPlatform {
    @ExpectPlatform
    public static void sendWhitherSkullBouncePacket(LevelChunk chunk, int entityId, Vec3 vec) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendWhitherChargePacket(LevelChunk chunk, int entityId) {
        throw new AssertionError();
    }
}
