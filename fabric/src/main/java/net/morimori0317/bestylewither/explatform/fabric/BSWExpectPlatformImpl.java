package net.morimori0317.bestylewither.explatform.fabric;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.morimori0317.bestylewither.config.BESConfig;
import net.morimori0317.bestylewither.fabric.BEStyleWitherFabric;
import net.morimori0317.bestylewither.networking.BSWPackets;

public class BSWExpectPlatformImpl {

    public static void sendWhitherChargePacket(ServerLevel level, ChunkPos chunkPos, int entityId) {
        BSWPackets.WitherChargeMessage msg = new BSWPackets.WitherChargeMessage(entityId);

        level.getChunkSource().chunkMap.getPlayers(chunkPos, false)
                .forEach(player -> ServerPlayNetworking.send(player, msg));
    }

    public static BESConfig getConfig() {
        return BEStyleWitherFabric.getConfig();
    }

}
