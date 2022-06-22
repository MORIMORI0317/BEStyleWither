package net.morimori0317.bestylewither.forge.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.morimori0317.bestylewither.BEStyleWither;
import net.morimori0317.bestylewither.networking.BSWPackets;

public class BSWPacketsForge {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(BEStyleWither.MODID, "main_channel"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public static void init() {
        INSTANCE.registerMessage(0, WhitherSkullBounceMessage.class, WhitherSkullBounceMessage::encode, WhitherSkullBounceMessage::decode, (msg, ctx) -> {
            ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> BSWPackets.onWhitherSkullBouncePacket(msg.entityId, msg.vec)));
            ctx.get().setPacketHandled(true);
        });

        INSTANCE.registerMessage(1, WhitherChargeMessage.class, WhitherChargeMessage::encode, WhitherChargeMessage::decode, (msg, ctx) -> {
            ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> BSWPackets.onWhitherChargePacket(msg.entityId)));
            ctx.get().setPacketHandled(true);
        });
    }

    public static class WhitherSkullBounceMessage {
        public int entityId;
        public Vec3 vec;

        public WhitherSkullBounceMessage(int entityId, Vec3 vec) {
            this.entityId = entityId;
            this.vec = vec;
        }

        public void encode(FriendlyByteBuf buf) {
            buf.writeInt(entityId);
            buf.writeDouble(vec.x());
            buf.writeDouble(vec.y());
            buf.writeDouble(vec.z());
        }

        public static WhitherSkullBounceMessage decode(FriendlyByteBuf buf) {
            int id = buf.readInt();
            var vec = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
            return new WhitherSkullBounceMessage(id, vec);
        }
    }

    public static class WhitherChargeMessage {
        public int entityId;

        public WhitherChargeMessage(int entityId) {
            this.entityId = entityId;
        }

        public void encode(FriendlyByteBuf buf) {
            buf.writeInt(entityId);
        }

        public static WhitherChargeMessage decode(FriendlyByteBuf buf) {
            int id = buf.readInt();
            return new WhitherChargeMessage(id);
        }
    }
}
