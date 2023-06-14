package net.morimori0317.bestylewither.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.phys.HitResult;
import net.morimori0317.bestylewither.BEStyleWither;
import net.morimori0317.bestylewither.entity.BEWitherBoss;
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
public abstract class WitherBossMixin extends Monster implements BEWitherBoss {
    @Unique
    private static final int MAX_WITHER_DEATH_TIME = 20 * 10;
    @Unique
    private int chargeTickCoolDown;
    @Unique
    private int clientChargeTick;
    @Unique
    private int clientChargeTickOld;
    @Unique
    private DamageSource lastDeathDamageSource;
    @Unique
    private boolean dropLootSkip;

    @Shadow
    public abstract int getInvulnerableTicks();

    @Shadow
    @Final
    private float[] yRotHeads;

    @Shadow
    public abstract boolean isPowered();

    @Shadow
    private int destroyBlocksTick;

    @Shadow
    protected abstract void performRangedAttack(int i, double d, double e, double f, boolean bl);

    @Shadow
    @Final
    private ServerBossEvent bossEvent;
    @Unique
    private static final EntityDataAccessor<Boolean> DATA_ID_FORCED_POWER = SynchedEntityData.defineId(WitherBoss.class, EntityDataSerializers.BOOLEAN);

    @Inject(method = "createAttributes", at = @At("RETURN"))
    private static void createAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        if (BEStyleWither.getConfig().isEnableDoubleHealth()) {
            var preBuilder = ((AttributeSupplierBuilderAccessor) cir.getReturnValue()).getBuilder();
            var preMH = preBuilder.get(Attributes.MAX_HEALTH);
            double preVal = preMH != null ? preMH.getBaseValue() : 300;
            cir.getReturnValue().add(Attributes.MAX_HEALTH, preVal * 2d);
        }
    }

    protected WitherBossMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "performRangedAttack(ILnet/minecraft/world/entity/LivingEntity;)V", at = @At("HEAD"), cancellable = true)
    private void performRangedAttack(int i, LivingEntity livingEntity, CallbackInfo ci) {
        if (!BEStyleWither.getConfig().isEnableShootMoreBlueWitherSkull())
            return;

        if (i == 0) {
            this.performRangedAttack(i, livingEntity.getX(), livingEntity.getY() + (double) livingEntity.getEyeHeight() * 0.5, livingEntity.getZ(), true);
            ci.cancel();
        }
    }

    @Inject(method = "isPowered", at = @At("RETURN"), cancellable = true)
    private void isPowered(CallbackInfoReturnable<Boolean> cir) {
        if ((BEStyleWither.getConfig().isEnableMaintainWeakenedState() && isForcedPowered()) || (BEStyleWither.getConfig().isEnableExplodeByDie() && deathTime > 0))
            cir.setReturnValue(true);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        this.goalSelector.addGoal(1, new WitherChargeAttackGoal((WitherBoss) (Object) this));
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineSynchedData(CallbackInfo ci) {
        this.entityData.define(DATA_ID_FORCED_POWER, false);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        compoundTag.putBoolean("FPower", isForcedPowered());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        setForcedPowered(compoundTag.getBoolean("FPower"));
    }

    @Inject(method = "aiStep", at = @At("HEAD"), cancellable = true)
    private void aiStepPre(CallbackInfo ci) {
        if (BEStyleWither.getConfig().isEnableExplodeByDie() && isDeadOrDying())
            ci.cancel();
    }

    @Inject(method = "getDeathSound", at = @At("RETURN"), cancellable = true)
    private void getDeathSound(CallbackInfoReturnable<SoundEvent> cir) {
        if (!BEStyleWither.getConfig().isEnableExplodeByDie())
            return;

        if (deathTime <= 0)
            cir.setReturnValue(null);
    }

    @Override
    public void playAmbientSound() {
        if (!BEStyleWither.getConfig().isEnableExplodeByDie()) {
            super.playAmbientSound();
            return;
        }

        if (isAlive())
            super.playAmbientSound();
    }

    @Override
    public void tick() {
        super.tick();

        if (BEStyleWither.getConfig().isEnableExplodeByHalfHealth() && getInvulnerableTicks() <= 0 && isPowered() && !isForcedPowered())
            setDeltaMovement(getDeltaMovement().add(0, -0.7f, 0));
    }

    @Override
    public void die(DamageSource damageSource) {
        if (!BEStyleWither.getConfig().isEnableExplodeByDie()) {
            super.die(damageSource);
            return;
        }

        dropLootSkip = true;
        super.die(damageSource);
        dropLootSkip = false;

        if (this.isDeadOrDying())
            lastDeathDamageSource = damageSource;
    }

    @Override
    protected void dropAllDeathLoot(DamageSource damageSource) {
        if (!dropLootSkip)
            super.dropAllDeathLoot(damageSource);
    }

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void aiStepPost(CallbackInfo ci) {
        if (BEStyleWither.getConfig().isEnableSpinAndWhiteSummon()) {
            int it = getInvulnerableTicks();
            if (it > 0) {
                float par = 1f - ((float) it / 220f);
                float angle = (60f * par) + 5f;
                setYBodyRot(yBodyRot + angle);
                setYHeadRot(getYHeadRot() + angle);
                for (int i = 0; i < yRotHeads.length; i++) {
                    yRotHeads[i] = yRotHeads[i] + angle;
                }
            }
        }

        setChargeCoolDown(Math.max(0, getChargeCoolDown() - 1));
        if (level().isClientSide()) {
            clientChargeTickOld = clientChargeTick;
            clientChargeTick = Math.max(0, clientChargeTick - 1);
        }
    }

    @Inject(method = "customServerAiStep", at = @At("TAIL"))
    private void customServerAiStep(CallbackInfo ci) {
        if (getInvulnerableTicks() <= 0 && isPowered() && !isForcedPowered()) {
            if (BEStyleWither.getConfig().isEnableExplodeByHalfHealth()) {
                var clip = level().clip(new ClipContext(position(), position().add(0, -30, 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));

                boolean flg = true;
                boolean exFlg = false;

                if (clip.getType() != HitResult.Type.MISS) {
                    flg = Math.sqrt(clip.distanceTo(this)) <= 1 || onGround() || isInWater() || isInWall();
                    exFlg = true;
                }

                if (flg) {
                    setForcedPowered(true);
                    this.level().explode(this, this.getX(), this.getEyeY(), this.getZ(), 5.0F, false, Level.ExplosionInteraction.MOB);

                    if (!this.isSilent())
                        this.level().globalLevelEvent(LevelEvent.SOUND_WITHER_BLOCK_BREAK, this.blockPosition(), 0);

                    if (exFlg && (this.level().getDifficulty() == Difficulty.NORMAL || this.level().getDifficulty() == Difficulty.HARD)) {
                        int wc = 3;
                        if (random.nextInt(8) == 0) wc = 4;

                        for (int i = 0; i < wc; i++) {
                            WitherSkeleton ws = new WitherSkeleton(EntityType.WITHER_SKELETON, level());
                            ws.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                            ws.finalizeSpawn((ServerLevelAccessor) level(), level().getCurrentDifficultyAt(blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                            level().addFreshEntity(ws);
                        }
                    }
                }

            } else {
                setForcedPowered(true);
            }
        }
    }

    private void setForcedPowered(boolean powered) {
        this.entityData.set(DATA_ID_FORCED_POWER, powered);
    }

    private boolean isForcedPowered() {
        return this.entityData.get(DATA_ID_FORCED_POWER);
    }

    @Override
    protected void tickDeath() {
        if (!BEStyleWither.getConfig().isEnableExplodeByDie()) {
            super.tickDeath();
            return;
        }

        deathTime++;
        bossEvent.setProgress(this.getHealth() / this.getMaxHealth());

        if (this.deathTime % 4 == 0)
            setForcedPowered(random.nextInt((int) Math.max(5 - ((float) this.deathTime / (20f * 10f) * 5f), 1)) == 0);

        if (!this.level().isClientSide()) {
            if (this.deathTime == MAX_WITHER_DEATH_TIME - 1) {
                this.level().explode(this, this.getX(), this.getEyeY(), this.getZ(), 8f, false, Level.ExplosionInteraction.MOB);
                if (!this.isSilent())
                    this.level().globalLevelEvent(LevelEvent.SOUND_WITHER_BLOCK_BREAK, this.blockPosition(), 0);

                SoundEvent soundevent = this.getDeathSound();
                if (soundevent != null)
                    this.playSound(soundevent, this.getSoundVolume() * 1.5f, this.getVoicePitch());
            } else if (this.deathTime == MAX_WITHER_DEATH_TIME) {
                this.lastHurtByPlayerTime = Math.max(lastHurtByPlayerTime, 1);
                var dmg = lastDeathDamageSource == null ? level().damageSources().fellOutOfWorld() : lastDeathDamageSource;
                dropAllDeathLoot(dmg);

                this.level().broadcastEntityEvent(this, (byte) 60);
                this.remove(RemovalReason.KILLED);
            }
        }
    }

    @Override
    public int getWitherDeathTime() {
        return deathTime;
    }

    @Override
    public float getWitherDeathTime(float delta) {
        return (deathTime + delta - 1.0F) / (float) (30 - 2);
    }

    @Override
    public int getDestroyBlocksTick() {
        return this.destroyBlocksTick;
    }

    @Override
    public void setDestroyBlocksTick(int tick) {
        this.destroyBlocksTick = tick;
    }

    @Override
    public int getChargeCoolDown() {
        return chargeTickCoolDown;
    }

    @Override
    public void setChargeCoolDown(int tick) {
        this.chargeTickCoolDown = tick;
    }

    @Override
    public void setClientCharge(int charge) {
        this.clientChargeTick = charge;
    }

    @Override
    public int getClientCharge() {
        return this.clientChargeTick;
    }

    @Override
    public float getClientCharge(float delta) {
        return Mth.lerp(delta, clientChargeTickOld, clientChargeTick);
    }
}