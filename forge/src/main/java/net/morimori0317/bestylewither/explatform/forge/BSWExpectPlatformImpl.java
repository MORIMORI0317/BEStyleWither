package net.morimori0317.bestylewither.explatform.forge;

import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import net.morimori0317.bestylewither.config.BESConfig;
import net.morimori0317.bestylewither.forge.BEStyleWitherForge;
import net.morimori0317.bestylewither.forge.networking.BSWPacketsForge;

public class BSWExpectPlatformImpl {
    public static void sendWhitherSkullBouncePacket(LevelChunk chunk, int entityId, Vec3 vec) {
        BSWPacketsForge.INSTANCE.send(new BSWPacketsForge.WhitherSkullBounceMessage(entityId, vec), PacketDistributor.TRACKING_CHUNK.with(chunk));
    }

    public static void sendWhitherChargePacket(LevelChunk chunk, int entityId) {
        BSWPacketsForge.INSTANCE.send(new BSWPacketsForge.WhitherChargeMessage(entityId), PacketDistributor.TRACKING_CHUNK.with(chunk));
    }

    public static BESConfig getConfig() {
        return BEStyleWitherForge.CONFIG;
    }
}
