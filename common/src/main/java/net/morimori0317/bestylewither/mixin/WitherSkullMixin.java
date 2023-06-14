package net.morimori0317.bestylewither.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.morimori0317.bestylewither.BEStyleWither;
import net.morimori0317.bestylewither.explatform.BSWExpectPlatform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WitherSkull.class)
public abstract class WitherSkullMixin {

    @Shadow
    public abstract boolean isDangerous();

    @Inject(method = "getInertia", at = @At("RETURN"), cancellable = true)
    private void getInertia(CallbackInfoReturnable<Float> cir) {
        if (!BEStyleWither.getConfig().isEnableMoreInertialBlueWitherSkull())
            return;

        if (isDangerous())
            cir.setReturnValue(0.90F);
    }

    @Inject(method = "isPickable", at = @At("RETURN"), cancellable = true)
    private void isPickable(CallbackInfoReturnable<Boolean> cir) {
        if (!BEStyleWither.getConfig().isEnableBounceBlueWitherSkull())
            return;

        if (isDangerous() && !cir.getReturnValue())
            cir.setReturnValue(true);
    }

    @Inject(method = "hurt", at = @At("RETURN"), cancellable = true)
    private void hurt(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir) {
        if (!BEStyleWither.getConfig().isEnableBounceBlueWitherSkull())
            return;

        if (isDangerous() && !cir.getReturnValue()) {
            WitherSkull ths = (WitherSkull) (Object) this;
            if (ths.isInvulnerableTo(damageSource))
                return;

            ((EntityAccessor) ths).markHurtInvoker();
            var entity = damageSource.getEntity();

            if (entity != null && !(entity instanceof WitherBoss) && !(damageSource.getDirectEntity() instanceof WitherSkull)) {
                if (!ths.level().isClientSide) {
                    Vec3 vec3 = entity.getLookAngle();
                    ths.setDeltaMovement(vec3);
                    ths.xPower = vec3.x * 0.1;
                    ths.yPower = vec3.y * 0.1;
                    ths.zPower = vec3.z * 0.1;
                    ths.setOwner(entity);

                    LevelChunk lch = (LevelChunk) ths.level().getChunk(ths.blockPosition());
                    BSWExpectPlatform.sendWhitherSkullBouncePacket(lch, ths.getId(), vec3);
                }
                cir.setReturnValue(true);
            }
        }
    }
}
