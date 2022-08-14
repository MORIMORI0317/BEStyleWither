package net.morimori0317.bestylewither.explatform.fabric;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.morimori0317.bestylewither.fabric.networking.BSWPacketsFabric;

public class BSWExpectPlatformImpl {
    public static void sendWhitherChargePacket(LevelChunk chunk, int entityId) {
        ((ServerChunkCache) chunk.getLevel().getChunkSource()).chunkMap.getPlayers(chunk.getPos(), false).forEach(player -> {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeInt(entityId);
            ServerPlayNetworking.send(player, BSWPacketsFabric.WHITHER_CHARGE, buf);
        });
    }

    public static void sendWhitherSkullBouncePacket(LevelChunk chunk, int entityId, Vec3 vec) {
        ((ServerChunkCache) chunk.getLevel().getChunkSource()).chunkMap.getPlayers(chunk.getPos(), false).forEach(player -> {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeInt(entityId);
            buf.writeDouble(vec.x());
            buf.writeDouble(vec.y());
            buf.writeDouble(vec.z());
            ServerPlayNetworking.send(player, BSWPacketsFabric.WHITHER_SKULL_BOUNCE, buf);
        });
    }
}
