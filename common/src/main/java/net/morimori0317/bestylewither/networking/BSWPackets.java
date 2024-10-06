package net.morimori0317.bestylewither.networking;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.morimori0317.bestylewither.BEStyleWither;
import net.morimori0317.bestylewither.entity.BEWitherBoss;
import net.morimori0317.bestylewither.entity.goal.WitherChargeAttackGoal;
import org.jetbrains.annotations.NotNull;

public class BSWPackets {
    public static final CustomPacketPayload.Type<WitherChargeMessage> WHITHER_CHARGE_TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(BEStyleWither.MODID, "whither_charge"));

    public static final StreamCodec<RegistryFriendlyByteBuf, WitherChargeMessage> WITHER_CHARGE_CODEC = new StreamCodec<>() {
        @Override
        public void encode(RegistryFriendlyByteBuf buf, WitherChargeMessage msg) {
            buf.writeInt(msg.entityId());
        }

        @Override
        public @NotNull WitherChargeMessage decode(RegistryFriendlyByteBuf buf) {
            return new WitherChargeMessage(buf.readInt());
        }
    };

    public static void onWhitherChargePacket(int entityId) {
        var level = Minecraft.getInstance().level;
        if (level != null) {
            var entity = level.getEntity(entityId);
            if (entity instanceof BEWitherBoss wither)
                wither.beStyleWither$getInstance().setClientChargeTick(WitherChargeAttackGoal.CHARGE_TIME);
        }

    }

    public record WitherChargeMessage(int entityId) implements CustomPacketPayload {
        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return WHITHER_CHARGE_TYPE;
        }
    }
}
