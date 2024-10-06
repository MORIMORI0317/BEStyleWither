package net.morimori0317.bestylewither.entity;

import net.minecraft.world.entity.projectile.WitherSkull;
import net.morimori0317.bestylewither.BEStyleWither;

public class WitherSkullModification {

    public static boolean isBounce(WitherSkull witherSkull) {
        if (!BEStyleWither.getConfig().isEnableBounceBlueWitherSkull()) {
            return false;
        }

        return witherSkull.isDangerous();
    }

    public static boolean isMoreInertial(WitherSkull witherSkull) {
        if (!BEStyleWither.getConfig().isEnableMoreInertialBlueWitherSkull()) {
            return false;
        }

        return witherSkull.isDangerous();
    }

}
