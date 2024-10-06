package net.morimori0317.bestylewither.fabric.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.morimori0317.bestylewither.networking.BSWPackets;

public class BSWPacketsFabric {

    public static void init() {
        PayloadTypeRegistry.playS2C().register(BSWPackets.WHITHER_CHARGE_TYPE, BSWPackets.WITHER_CHARGE_CODEC);
    }

    public static void clientInit() {
        ClientPlayNetworking.registerGlobalReceiver(BSWPackets.WHITHER_CHARGE_TYPE, (payload, context) -> BSWPackets.onWhitherChargePacket(payload.entityId()));
    }

}
