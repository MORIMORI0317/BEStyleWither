package net.morimori0317.bestylewither.entity.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.morimori0317.bestylewither.BEStyleWither;
import net.morimori0317.bestylewither.entity.BEWitherBoss;
import net.morimori0317.bestylewither.explatform.BSWExpectPlatform;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class WitherChargeAttackGoal extends Goal {
    public static final int chargeTime = 75;
    public static final int chargeHoldTime = 50;
    private final WitherBoss mob;
    @Nullable
    private LivingEntity target;
    private int chargeTick;
    private Vec3 lookAt;
    private float bodyRot;

    public WitherChargeAttackGoal(WitherBoss mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return canUse(true);
    }

    public boolean canUse(boolean randed) {
        if (!BEStyleWither.getConfig().isEnableChargeAttack())
            return false;

        if (randed && mob.getRandom().nextInt(75) != 0)
            return false;

        if (mob.getInvulnerableTicks() > 0 || !mob.isPowered() || ((BEWitherBoss) mob).getChargeCoolDown() > 0)
            return false;
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity != null && livingEntity.isAlive()) {
            this.target = livingEntity;
            return true;
        }
        return false;
    }

    @Override
    public void start() {

        if (!mob.level().isClientSide()) {
            LevelChunk lch = (LevelChunk) mob.level().getChunk(mob.blockPosition());
            BSWExpectPlatform.sendWhitherChargePacket(lch, mob.getId());
        }

        this.chargeTick = this.adjustedTickDelay(chargeTime);
        this.mob.getNavigation().stop();
    }

    @Override
    public void stop() {
        this.chargeTick = 0;
        this.target = null;
        ((BEWitherBoss) mob).setChargeCoolDown(200);
        lookAt = null;
        bodyRot = 0f;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse(false) && this.chargeTick > 0 && target != null;
    }

    @Override
    public void tick() {
        this.chargeTick = Math.max(0, this.chargeTick - 1);
        if (this.chargeTick > this.adjustedTickDelay(chargeTime - chargeHoldTime)) {
            if (target != null) {
                this.mob.getLookControl().setLookAt(target);
                lookAt = mob.getLookAngle();
                bodyRot = mob.yBodyRot;
                mob.setYBodyRot(bodyRot);
                mob.setDeltaMovement(Vec3.ZERO);
            }
        } else {
            ((BEWitherBoss) mob).setDestroyBlocksTick(1);

            if (lookAt != null) {
                this.mob.getLookControl().setLookAt(lookAt);
                mob.setDeltaMovement(lookAt.scale(1.3));
                mob.setYBodyRot(bodyRot);
            }
        }
    }
}
