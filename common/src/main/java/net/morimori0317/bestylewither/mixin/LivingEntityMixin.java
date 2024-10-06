package net.morimori0317.bestylewither.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.morimori0317.bestylewither.BEStyleWither;
import net.morimori0317.bestylewither.entity.BEWitherBoss;
import net.morimori0317.bestylewither.entity.ExplodeByDieConstants;
import net.morimori0317.bestylewither.entity.WitherBossInstance;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    public int deathTime;

    // EnableExplodeByDie

    @Shadow
    @Nullable
    protected abstract SoundEvent getDeathSound();

    @Shadow
    protected abstract float getSoundVolume();

    @Shadow
    public abstract float getVoicePitch();

    @Shadow
    protected int lastHurtByPlayerTime;

    @Shadow
    protected abstract void dropAllDeathLoot(ServerLevel serverLevel, DamageSource damageSource);

    @WrapWithCondition(
            method = "hurt",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;makeSound(Lnet/minecraft/sounds/SoundEvent;)V", ordinal = 0)
    )
    private boolean hurtWrapCondition(LivingEntity instance, SoundEvent soundEvent) {
        if (BEStyleWither.getConfig().isEnableExplodeByDie()) {
            return !(instance instanceof WitherBoss);
        }
        return true;
    }

    @WrapWithCondition(
            method = "handleEntityEvent",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V", ordinal = 0)
    )
    private boolean handleEntityEventWrapCondition(LivingEntity instance, SoundEvent soundEvent, float v1, float v2) {
        if (BEStyleWither.getConfig().isEnableExplodeByDie()) {
            return !(instance instanceof WitherBoss);
        }
        return true;
    }

    @WrapWithCondition(
            method = "die",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;dropAllDeathLoot(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;)V", ordinal = 0)
    )
    private boolean dieWrapCondition(LivingEntity instance, ServerLevel serverLevel, DamageSource damageSource) {

        if (BEStyleWither.getConfig().isEnableExplodeByDie() && instance instanceof WitherBoss witherBoss) {
            ((BEWitherBoss) witherBoss).beStyleWither$getInstance().setSkipLootDrop(true);
            return false;
        }

        return true;
    }

    @Inject(method = "die",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;broadcastEntityEvent(Lnet/minecraft/world/entity/Entity;B)V", ordinal = 0))
    private void dieInject(DamageSource damageSource, CallbackInfo ci) {
        LivingEntity ths = (LivingEntity) (Object) this;
        if (BEStyleWither.getConfig().isEnableExplodeByDie() && ths instanceof WitherBoss witherBoss) {
            ((BEWitherBoss) witherBoss).beStyleWither$getInstance().setExplodeByDieDeathDamageSource(damageSource);
        }
    }

    @WrapMethod(method = "tickDeath")
    private void tickDeathWrap(Operation<Void> original) {
        LivingEntity ths = (LivingEntity) (Object) this;

        if (!(ths instanceof WitherBoss witherBoss) || !BEStyleWither.getConfig().isEnableExplodeByDie()) {
            original.call();
            return;
        }

        deathTime++;
        ((WitherBossAccessor) witherBoss).getBossEvent().setProgress(ths.getHealth() / ths.getMaxHealth());

        WitherBossInstance witherBossInstance = ((BEWitherBoss) witherBoss).beStyleWither$getInstance();
        Level level = ths.level();

        if (!level.isClientSide()) {
            if (this.deathTime == ExplodeByDieConstants.EXPLODE_TICK - 1) {
                level.explode(ths, ths.getX(), ths.getEyeY(), ths.getZ(), 8f, false, Level.ExplosionInteraction.MOB);
                if (!ths.isSilent()) {
                    level.globalLevelEvent(LevelEvent.SOUND_WITHER_BLOCK_BREAK, ths.blockPosition(), 0);
                }

                SoundEvent soundevent = getDeathSound();
                if (soundevent != null) {
                    ths.playSound(soundevent, getSoundVolume() * 1.5f, getVoicePitch());
                }
            } else if (this.deathTime >= ExplodeByDieConstants.EXPLODE_TICK && !ths.isRemoved()) {
                lastHurtByPlayerTime = Math.max(lastHurtByPlayerTime, 1);

                DamageSource deathDamage = witherBossInstance.getExplodeByDieDeathDamageSource();
                if (deathDamage == null) {
                    deathDamage = level.damageSources().fellOutOfWorld();
                }

                if (level instanceof ServerLevel serverLevel) {
                    witherBossInstance.setSkipLootDrop(false);
                    dropAllDeathLoot(serverLevel, deathDamage);
                }

                if (!ths.isRemoved()) {
                    level.broadcastEntityEvent(ths, EntityEvent.POOF);
                    ths.remove(Entity.RemovalReason.KILLED);
                }
            }
        } else {
            boolean energyLayerVisible;

            if (deathTime > ExplodeByDieConstants.ENERGY_LAYER_START_TICK) {

                if (deathTime <= ExplodeByDieConstants.ENERGY_LAYER_BLINKING_START_TICK) {
                    energyLayerVisible = true;
                } else {
                    int energyTick = witherBossInstance.getExplodeByDieDeathBlinkingTick();

                    boolean preVisible = witherBossInstance.isExplodeByDieEnergyLayerVisible();

                    if (energyTick > 0) {
                        witherBossInstance.setExplodeByDieDeathBlinkingTick(energyTick - 1);
                        energyLayerVisible = preVisible;
                    } else {
                        RandomSource rnd = ths.getRandom();
                        boolean longBlinking;
                        int blinkingTick;

                        if (deathTime > ExplodeByDieConstants.ENERGY_LAYER_LONG_BLINKING_END_TICK) {
                            longBlinking = false;
                        } else {
                            float par = (float) (deathTime - ExplodeByDieConstants.ENERGY_LAYER_BLINKING_START_TICK) / (float) (ExplodeByDieConstants.ENERGY_LAYER_LONG_BLINKING_END_TICK - ExplodeByDieConstants.ENERGY_LAYER_BLINKING_START_TICK);
                            longBlinking = rnd.nextInt(4 + (int) ((1f - par) * 40f)) == 0;
                        }

                        if (preVisible) {
                            blinkingTick = ExplodeByDieConstants.ENERGY_LAYER_BLINKING_OFF_MIN_DURATION;
                            blinkingTick += rnd.nextInt((ExplodeByDieConstants.ENERGY_LAYER_BLINKING_OFF_MAX_DURATION) - blinkingTick + 1);
                        } else {
                            blinkingTick = longBlinking ? ExplodeByDieConstants.ENERGY_LAYER_LONG_BLINKING_ON_DURATION : ExplodeByDieConstants.ENERGY_LAYER_SHORT_BLINKING_ON_DURATION;
                        }

                        witherBossInstance.setExplodeByDieDeathBlinkingTick(blinkingTick);
                        energyLayerVisible = !preVisible;
                    }
                }

            } else {
                energyLayerVisible = true;
            }

            witherBossInstance.setExplodeByDieEnergyLayerVisible(energyLayerVisible);
        }
    }

    @Inject(method = "tickDeath",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;broadcastEntityEvent(Lnet/minecraft/world/entity/Entity;B)V"))
    private void tickDeathWrap2(CallbackInfo ci) {
        LivingEntity ths = (LivingEntity) (Object) this;
        if (!(ths instanceof WitherBoss witherBoss) || BEStyleWither.getConfig().isEnableExplodeByDie()) {
            return;
        }

        if (((BEWitherBoss) witherBoss).beStyleWither$getInstance().isSkipLootDrop()) {
            if (ths.level() instanceof ServerLevel serverLevel) {
                WitherBossInstance witherBossInstance = ((BEWitherBoss) witherBoss).beStyleWither$getInstance();
                witherBossInstance.setSkipLootDrop(false);

                DamageSource deathDamage = witherBossInstance.getExplodeByDieDeathDamageSource();
                if (deathDamage == null) {
                    deathDamage = serverLevel.damageSources().fellOutOfWorld();
                }

                dropAllDeathLoot(serverLevel, deathDamage);
            }
        }
    }
}
