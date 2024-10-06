package net.morimori0317.bestylewither.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.morimori0317.bestylewither.BEStyleWither;
import net.morimori0317.bestylewither.entity.BEWitherBoss;
import net.morimori0317.bestylewither.entity.WitherBossInstance;
import net.morimori0317.bestylewither.entity.goal.WitherChargeAttackGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WitherBoss.class)
public abstract class WitherBossMixin implements BEWitherBoss {

    @Shadow
    public abstract boolean isPowered();

    @Shadow
    public abstract int getInvulnerableTicks();

    @Shadow
    @Final
    private float[] yRotHeads;

    @Unique
    private static final EntityDataAccessor<Boolean> beStyleWither$DATA_ID_ONCE_WEAKENED = SynchedEntityData.defineId(WitherBoss.class, EntityDataSerializers.BOOLEAN);

    @Unique
    private final WitherBossInstance beStyleWither$witherBossInstance = new WitherBossInstance((WitherBoss) (Object) (this), beStyleWither$DATA_ID_ONCE_WEAKENED);


    @Override
    public WitherBossInstance beStyleWither$getInstance() {
        return beStyleWither$witherBossInstance;
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineSynchedDataInject(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(beStyleWither$DATA_ID_ONCE_WEAKENED, false);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addAdditionalSaveDataInject(CompoundTag compoundTag, CallbackInfo ci) {
        CompoundTag tag = new CompoundTag();
        beStyleWither$witherBossInstance.saveToTag(tag);
        compoundTag.put("BEStyleWitherData", tag);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAdditionalSaveDataInject(CompoundTag compoundTag, CallbackInfo ci) {
        CompoundTag tag = compoundTag.getCompound("BEStyleWitherData");
        beStyleWither$witherBossInstance.loadFromTag(tag);
    }

    @Inject(method = "customServerAiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Monster;customServerAiStep()V", shift = At.Shift.AFTER))
    private void customServerAiStepInject(CallbackInfo ci) {
        if (isPowered() && !beStyleWither$witherBossInstance.isOnceWeakened()) {
            beStyleWither$witherBossInstance.setOnceWeakened(true);
        }
    }

    // DoubleHealth

    @WrapOperation(
            method = "createAttributes",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;add(Lnet/minecraft/core/Holder;D)Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;", ordinal = 0)
    )
    private static AttributeSupplier.Builder createAttributesAddWarp(AttributeSupplier.Builder instance, Holder<Attribute> holder, double d, Operation<AttributeSupplier.Builder> original) {
        double health = d;

        if (BEStyleWither.getConfig().isEnableDoubleHealth()) {
            health *= 2;
        }

        return original.call(instance, holder, health);
    }

    @WrapOperation(
            method = "customServerAiStep",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/boss/wither/WitherBoss;heal(F)V", ordinal = 0)
    )
    private void healWarp(WitherBoss instance, float v, Operation<Void> original) {
        float healAmount = v;

        if (BEStyleWither.getConfig().isEnableDoubleHealth()) {
            healAmount *= 2;
        }

        original.call(instance, healAmount);
    }

    // SpinAndWhiteSummon

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void aiStepInject(CallbackInfo ci) {
        if (!BEStyleWither.getConfig().isEnableSpinAndWhiteSummon()) {
            return;
        }

        int it = getInvulnerableTicks();
        if (it > 0) {
            WitherBoss ths = (WitherBoss) (Object) this;
            float par = 1f - ((float) it / 220f);
            float angle = (60f * par) + 5f;

            ths.setYBodyRot(ths.yBodyRot + angle);
            ths.setYHeadRot(ths.getYHeadRot() + angle);
            for (int i = 0; i < yRotHeads.length; i++) {
                yRotHeads[i] = yRotHeads[i] + angle;
            }
        }
    }

    // ShootMoreBlueWitherSkull

    @WrapOperation(
            method = "performRangedAttack(ILnet/minecraft/world/entity/LivingEntity;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/boss/wither/WitherBoss;performRangedAttack(IDDDZ)V", ordinal = 0)
    )
    private void performRangedAttackWarp(WitherBoss instance, int i, double d, double e, double f, boolean bl, Operation<Void> original) {
        boolean blueSkull = bl;

        if (BEStyleWither.getConfig().isEnableShootMoreBlueWitherSkull() && i == 0) {
            blueSkull = true;
        }

        original.call(instance, i, d, e, f, blueSkull);
    }

    // MaintainWeakenedState

    @Inject(method = "isPowered", at = @At("RETURN"), cancellable = true)
    private void isPoweredInject(CallbackInfoReturnable<Boolean> cir) {
        if (BEStyleWither.getConfig().isEnableMaintainWeakenedState() && beStyleWither$witherBossInstance.isOnceWeakened()) {
            cir.setReturnValue(true);
        }
    }

    // ExplodeByHalfHealth

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/boss/wither/WitherBoss;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V", ordinal = 0, shift = At.Shift.AFTER))
    private void aiStepInject2(CallbackInfo ci) {
        if (!BEStyleWither.getConfig().isEnableExplodeByHalfHealth()) {
            return;
        }

        if (beStyleWither$witherBossInstance.getHalfHealthExplodeFallTick() > 0) {
            WitherBoss ths = (WitherBoss) (Object) this;
            ths.setDeltaMovement(ths.getDeltaMovement().add(0, -0.7f, 0));
        }
    }

    @Inject(method = "customServerAiStep", at = @At("TAIL"))
    private void customServerAiStepInject2(CallbackInfo ci) {
        if (!BEStyleWither.getConfig().isEnableExplodeByHalfHealth() || beStyleWither$witherBossInstance.isHalfHealthExploded()) {
            beStyleWither$witherBossInstance.setHalfHealthExplodeFallTick(0);
            beStyleWither$witherBossInstance.setHalfHealthExplodeElapsedTick(0);
            return;
        }

        if (getInvulnerableTicks() <= 0 && isPowered()) {
            beStyleWither$witherBossInstance.setHalfHealthExplodeFallTick(WitherBossInstance.HALF_HEALTH_EXPLODE_RETENTION_TICK);
        } else if (beStyleWither$witherBossInstance.getHalfHealthExplodeFallTick() > 0) {
            beStyleWither$witherBossInstance.setHalfHealthExplodeFallTick(beStyleWither$witherBossInstance.getHalfHealthExplodeFallTick() - 1);
        }

        if (beStyleWither$witherBossInstance.getHalfHealthExplodeFallTick() > 0) {
            WitherBoss ths = (WitherBoss) (Object) this;
            BlockHitResult hitResult = ths.level().clip(new ClipContext(ths.position(), ths.position().add(0, -30, 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, ths));

            boolean explodeFlg = false;
            boolean spawnSkeletonFlg = false;

            if (ths.isInWall() || beStyleWither$witherBossInstance.getHalfHealthExplodeElapsedTick() >= WitherBossInstance.HALF_HEALTH_EXPLODE_MAX_ELAPSED_TICK) {
                explodeFlg = true;
            } else if ((hitResult.getType() != HitResult.Type.MISS && Math.sqrt(hitResult.distanceTo(ths)) <= 1) || ths.onGround() || ths.isInLiquid()) {
                explodeFlg = true;
                spawnSkeletonFlg = true;
            }

            if (explodeFlg) {
                beStyleWither$witherBossInstance.setHalfHealthExploded(true);
                ths.level().explode(ths, ths.getX(), ths.getEyeY(), ths.getZ(), 5.0F, false, Level.ExplosionInteraction.MOB);

                if (!ths.isSilent()) {
                    ths.level().globalLevelEvent(LevelEvent.SOUND_WITHER_BLOCK_BREAK, ths.blockPosition(), 0);
                }

                if (spawnSkeletonFlg && (ths.level().getDifficulty() == Difficulty.NORMAL || ths.level().getDifficulty() == Difficulty.HARD)) {
                    RandomSource randomSource = ths.getRandom();
                    int numOfSpawn = randomSource.nextBoolean() ? 4 : 3;

                    for (int i = 0; i < numOfSpawn; i++) {
                        WitherSkeleton ws = new WitherSkeleton(EntityType.WITHER_SKELETON, ths.level());
                        ws.moveTo(ths.getX(), ths.getY(), ths.getZ(), randomSource.nextFloat() * 360.0F, 0.0F);
                        ws.finalizeSpawn((ServerLevelAccessor) ths.level(), ths.level().getCurrentDifficultyAt(ths.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
                        ths.level().addFreshEntity(ws);
                    }
                }
            }

            beStyleWither$witherBossInstance.setHalfHealthExplodeElapsedTick(beStyleWither$witherBossInstance.getHalfHealthExplodeElapsedTick() + 1);
        } else {
            beStyleWither$witherBossInstance.setHalfHealthExplodeElapsedTick(0);
        }
    }

    // EnableExplodeByDie

    @Inject(method = "aiStep", at = @At("HEAD"), cancellable = true)
    private void aiStepPre(CallbackInfo ci) {
        if (!BEStyleWither.getConfig().isEnableExplodeByDie()) {
            return;
        }

        WitherBoss ths = (WitherBoss) (Object) this;
        if (ths.isDeadOrDying()) {
            ci.cancel();
        }
    }

    // ChargeAttack

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        ((MobAccessor) this).getGoalSelector().addGoal(1, new WitherChargeAttackGoal((WitherBoss) (Object) this));
    }

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void aiStepPost(CallbackInfo ci) {
        WitherBoss ths = (WitherBoss) (Object) this;
        WitherBossInstance instance = beStyleWither$witherBossInstance;

        instance.setChargeCoolDown(Math.max(0, instance.getChargeCoolDown() - 1));
        if (ths.level().isClientSide()) {
            instance.setClientChargeTickOld(instance.getClientChargeTick());
            instance.setClientChargeTick(Math.max(0, instance.getClientChargeTick() - 1));
        }
    }

}