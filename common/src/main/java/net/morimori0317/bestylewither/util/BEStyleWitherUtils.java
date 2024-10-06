package net.morimori0317.bestylewither.util;

import net.minecraft.world.entity.boss.wither.WitherBoss;

public class BEStyleWitherUtils {


    public static float getWitherDeltaDeathTime(WitherBoss witherBoss, float delta) {
        return (witherBoss.deathTime + delta - 1.0F) / 28f;
    }
}
