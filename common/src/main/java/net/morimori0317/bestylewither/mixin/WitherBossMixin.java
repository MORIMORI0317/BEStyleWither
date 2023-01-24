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
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.phys.HitResult;
import net.morimori0317.bestylewither.entity.BEWitherBoss;
import net.morimori0317.bestylewither.entity.goal.WitherChargeAttackGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WitherBoss.class)
public abstract class WitherBossMixin extends Monster implements BEWitherBoss {
    private static final int MAX_WITHER_DEATH_TIME = 20 * 10;
    private int witherDeathTime;
    private int witherDeathTimeOld;
    private int chargeTickCoolDown;
    private int clientChargeTick;
    private int clientChargeTickOld;
    private DamageSource lastDeathDamageSource;

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
    @Final
    private ServerBossEvent bossEvent;


    @Shadow
    public abstract void performRangedAttack(int i, double d, double e, double f, boolean bl);

    private static final EntityDataAccessor<Boolean> DATA_ID_FORCED_POWER = SynchedEntityData.defineId(WitherBoss.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_ID_DEATH = SynchedEntityData.defineId(WitherBoss.class, EntityDataSerializers.BOOLEAN);

    protected WitherBossMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    /*  @Redirect(method = "performRangedAttack(ILnet/minecraft/world/entity/LivingEntity;)V", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextFloat()F", remap = false, ordinal = 0))
      private float injected(Random instance) {
          return 0f;
      }
  */
    /*@ModifyConstant(method = "performRangedAttack(ILnet/minecraft/world/entity/LivingEntity;)V", constant = @Constant(floatValue = 0.001f))
    private float performRangedAttack(float value) {
        return 1f;
    }*/

    @Inject(method = "performRangedAttack(ILnet/minecraft/world/entity/LivingEntity;)V", at = @At("HEAD"), cancellable = true)
    private void performRangedAttack(int i, LivingEntity livingEntity, CallbackInfo ci) {
        if (i == 0) {
            this.performRangedAttack(i, livingEntity.getX(), livingEntity.getY() + (double) livingEntity.getEyeHeight() * 0.5, livingEntity.getZ(), true);
            ci.cancel();
        }
    }

    @Inject(method = "isPowered", at = @At("RETURN"), cancellable = true)
    private void isPowered(CallbackInfoReturnable<Boolean> cir) {
        if (isForcedPowered() || witherDeathTime > 0) cir.setReturnValue(isForcedPowered());
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        this.goalSelector.addGoal(1, new WitherChargeAttackGoal((WitherBoss) (Object) this));
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineSynchedData(CallbackInfo ci) {
        this.entityData.define(DATA_ID_FORCED_POWER, false);
        this.entityData.define(DATA_ID_DEATH, false);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        compoundTag.putBoolean("FPower", isForcedPowered());
        compoundTag.putShort("WitherDeathTime", (short) this.witherDeathTime);
        compoundTag.putBoolean("WitherDeath", isDeath());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        setForcedPowered(compoundTag.getBoolean("FPower"));
        this.witherDeathTime = compoundTag.getShort("WitherDeathTime");
        setDeath(compoundTag.getBoolean("WitherDeath"));
    }

    @Override
    public void baseTick() {
        if (isDeath()) {
            if (this.lastHurtByPlayerTime > 0) this.lastHurtByPlayerTime++;
        }
        super.baseTick();
    }

    @Inject(method = "aiStep", at = @At("HEAD"), cancellable = true)
    private void aiStepPre(CallbackInfo ci) {
        if (isDeath()) {
            if (this.witherDeathTime >= MAX_WITHER_DEATH_TIME)
                return;

            this.setHealth(1.0F);

            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
            this.witherDeathTime++;

            if (!this.level.isClientSide()) {
                if (this.witherDeathTime % 4 == 0)
                    setForcedPowered(random.nextInt((int) Math.max(5 - ((float) this.witherDeathTime / (20f * 10f) * 5f), 1)) == 0);

                if (!isDeadOrDying()) {
                    if (this.witherDeathTime == MAX_WITHER_DEATH_TIME - 1) {
                        var interaction = this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
                        this.level.explode(this, this.getX(), this.getEyeY(), this.getZ(), 8f, false, interaction);
                        if (!this.isSilent())
                            this.level.globalLevelEvent(LevelEvent.SOUND_WITHER_BLOCK_BREAK, this.blockPosition(), 0);

                        SoundEvent soundevent = this.getDeathSound();
                        if (soundevent != null)
                            this.playSound(soundevent, this.getSoundVolume() * 1.5f, this.getVoicePitch());

                    } else if (this.witherDeathTime == MAX_WITHER_DEATH_TIME) {

                        var dmg = lastDeathDamageSource == null ? DamageSource.OUT_OF_WORLD : lastDeathDamageSource;
                        hurt(dmg, Float.MAX_VALUE);
                        if (!isDeadOrDying()) {
                            setHealth(0);
                            die(dmg);
                        }

                    }

                }
            }
            ci.cancel();
        }
    }

    @Override
    public boolean isAlive() {
        return super.isAlive() && !isDeath();
    }

    @Inject(method = "getDeathSound", at = @At("RETURN"), cancellable = true)
    private void getDeathSound(CallbackInfoReturnable<SoundEvent> cir) {
        if (!isDeath()) cir.setReturnValue(null);
    }

    @Override
    public void playAmbientSound() {
        if (!isDeath()) super.playAmbientSound();
    }

    @Override
    public void tick() {
        super.tick();
        this.witherDeathTimeOld = witherDeathTime;

        if (getInvulnerableTicks() <= 0 && isPowered() && !isForcedPowered())
            setDeltaMovement(getDeltaMovement().add(0, -0.7f, 0));
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void hurt(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir) {
        if (isDeath() && this.witherDeathTime < MAX_WITHER_DEATH_TIME)
            cir.setReturnValue(false);
    }

    @Override
    public void die(DamageSource damageSource) {
        if (isDeath()) {
            super.die(damageSource);
        } else {
            lastDeathDamageSource = damageSource;
            this.setHealth(1.0F);
            setDeath(true);
        }
    }

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void aiStepPost(CallbackInfo ci) {
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

        setChargeCoolDown(Math.max(0, getChargeCoolDown() - 1));
        if (level.isClientSide()) {
            clientChargeTickOld = clientChargeTick;
            clientChargeTick = Math.max(0, clientChargeTick - 1);
        }
    }

    @Inject(method = "customServerAiStep", at = @At("TAIL"))
    private void customServerAiStep(CallbackInfo ci) {
        if (getInvulnerableTicks() <= 0 && isPowered() && !isForcedPowered()) {
            var clip = level.clip(new ClipContext(position(), position().add(0, -30, 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));

            boolean flg = true;
            boolean exFlg = false;

            if (clip.getType() != HitResult.Type.MISS) {
                flg = Math.sqrt(clip.distanceTo(this)) <= 1 || isOnGround() || isInWater() || isInWall();
                exFlg = true;
            }

            if (flg) {
                setForcedPowered(true);
                var interaction = this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
                this.level.explode(this, this.getX(), this.getEyeY(), this.getZ(), 5.0F, false, interaction);
                if (!this.isSilent())
                    this.level.globalLevelEvent(LevelEvent.SOUND_WITHER_BLOCK_BREAK, this.blockPosition(), 0);

                if (exFlg && (this.level.getDifficulty() == Difficulty.NORMAL || this.level.getDifficulty() == Difficulty.HARD)) {
                    int wc = 3;
                    if (random.nextInt(8) == 0) wc = 4;

                    for (int i = 0; i < wc; i++) {
                        WitherSkeleton ws = new WitherSkeleton(EntityType.WITHER_SKELETON, level);
                        ws.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                        ws.finalizeSpawn((ServerLevelAccessor) level, level.getCurrentDifficultyAt(blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                        level.addFreshEntity(ws);
                    }
                }
            }
        }
    }

    private void setForcedPowered(boolean powered) {
        this.entityData.set(DATA_ID_FORCED_POWER, powered);
    }

    private boolean isForcedPowered() {
        return this.entityData.get(DATA_ID_FORCED_POWER);
    }

    private void setDeath(boolean death) {
        this.entityData.set(DATA_ID_DEATH, death);
    }

    private boolean isDeath() {
        return this.entityData.get(DATA_ID_DEATH);
    }

    @Override
    protected void tickDeath() {
        if (!this.level.isClientSide()) {
            this.level.broadcastEntityEvent(this, (byte) 60);
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public int getWitherDeathTime() {
        return witherDeathTime;
    }

    @Override
    public float getWitherDeathTime(float delta) {
        return Mth.lerp(delta, (float) this.witherDeathTimeOld, (float) this.witherDeathTime) / (float) (30 - 2);
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
