package net.morimori0317.bestylewither.fabric.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.morimori0317.bestylewither.BEStyleWither;
import net.morimori0317.bestylewither.networking.BSWPackets;

public class BSWPacketsFabric {
    public static final ResourceLocation WHITHER_SKULL_BOUNCE = new ResourceLocation(BEStyleWither.MODID, "whither_skull_bounce");
    public static final ResourceLocation WHITHER_CHARGE = new ResourceLocation(BEStyleWither.MODID, "whither_charge");

    public static void clientInit() {
        ClientPlayNetworking.registerGlobalReceiver(WHITHER_SKULL_BOUNCE, (client, handler, buf, responseSender) -> {
            int id = buf.readInt();
            var vec = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
            client.execute(() -> BSWPackets.onWhitherSkullBouncePacket(id, vec));
        });

        ClientPlayNetworking.registerGlobalReceiver(WHITHER_CHARGE, (client, handler, buf, responseSender) -> {
            int id = buf.readInt();
            client.execute(() -> BSWPackets.onWhitherChargePacket(id));
        });
    }
}
