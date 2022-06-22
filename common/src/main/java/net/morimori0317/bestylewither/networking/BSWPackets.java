package net.morimori0317.bestylewither.networking;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.phys.Vec3;
import net.morimori0317.bestylewither.entity.BEWitherBoss;
import net.morimori0317.bestylewither.entity.goal.WitherChargeAttackGoal;

public class BSWPackets {

    public static void onWhitherSkullBouncePacket(int entityId, Vec3 vec) {
        var level = Minecraft.getInstance().level;
        if (level != null) {
            var entity = level.getEntity(entityId);
            if (entity instanceof WitherSkull witherSkull) {
                witherSkull.setDeltaMovement(vec);
                witherSkull.xPower = vec.x * 0.1;
                witherSkull.yPower = vec.y * 0.1;
                witherSkull.zPower = vec.z * 0.1;
            }
        }
    }

    public static void onWhitherChargePacket(int entityId) {
        var level = Minecraft.getInstance().level;
        if (level != null) {
            var entity = level.getEntity(entityId);
            if (entity instanceof BEWitherBoss wither)
                wither.setClientCharge(WitherChargeAttackGoal.chargeTime);
        }
    }
}
