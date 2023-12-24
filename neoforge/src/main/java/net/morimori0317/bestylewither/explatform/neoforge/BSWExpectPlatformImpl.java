package net.morimori0317.bestylewither.explatform.neoforge;

import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.morimori0317.bestylewither.config.BESConfig;
import net.morimori0317.bestylewither.neoforge.BEStyleWitherNeoForge;
import net.morimori0317.bestylewither.neoforge.networking.BSWPacketsNeoForge;
import net.neoforged.neoforge.network.PacketDistributor;

public class BSWExpectPlatformImpl {
    public static void sendWhitherSkullBouncePacket(LevelChunk chunk, int entityId, Vec3 vec) {
        BSWPacketsNeoForge.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), new BSWPacketsNeoForge.WhitherSkullBounceMessage(entityId, vec));
    }

    public static void sendWhitherChargePacket(LevelChunk chunk, int entityId) {
        BSWPacketsNeoForge.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), new BSWPacketsNeoForge.WhitherChargeMessage(entityId));
    }

    public static BESConfig getConfig() {
        return BEStyleWitherNeoForge.CONFIG;
    }
}
