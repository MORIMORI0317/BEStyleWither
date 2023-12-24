package net.morimori0317.bestylewither.neoforge.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.morimori0317.bestylewither.BEStyleWither;
import net.morimori0317.bestylewither.networking.BSWPackets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.simple.SimpleChannel;

public class BSWPacketsNeoForge {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(BEStyleWither.MODID, "main_channel"))
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();

    private static int index = 0;

    public static void init() {
        INSTANCE.messageBuilder(WhitherSkullBounceMessage.class, index++)
                .encoder(WhitherSkullBounceMessage::encode)
                .decoder(WhitherSkullBounceMessage::decode)
                .consumerNetworkThread((msg, ctx) -> {
                    ctx.enqueueWork(() -> {
                        if (FMLEnvironment.dist == Dist.CLIENT) {
                            BSWPackets.onWhitherSkullBouncePacket(msg.entityId, msg.vec);
                        }
                    });
                    ctx.setPacketHandled(true);
                }).add();

        INSTANCE.messageBuilder(WhitherChargeMessage.class, index++)
                .encoder(WhitherChargeMessage::encode)
                .decoder(WhitherChargeMessage::decode)
                .consumerNetworkThread((msg, ctx) -> {
                    ctx.enqueueWork(() -> {
                        if (FMLEnvironment.dist == Dist.CLIENT) {
                            BSWPackets.onWhitherChargePacket(msg.entityId);
                        }
                    });
                    ctx.setPacketHandled(true);
                }).add();
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
