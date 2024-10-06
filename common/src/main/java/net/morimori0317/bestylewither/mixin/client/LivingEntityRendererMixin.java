package net.morimori0317.bestylewither.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.morimori0317.bestylewither.BEStyleWither;
import net.morimori0317.bestylewither.util.BEStyleWitherUtils;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {

    // SpinAndWhiteSummon

    @Inject(method = "getWhiteOverlayProgress", at = @At("RETURN"), cancellable = true)
    private void getWhiteOverlayProgressInject(T livingEntity, float f, CallbackInfoReturnable<Float> cir) {
        if (BEStyleWither.getConfig().isEnableSpinAndWhiteSummon() && livingEntity instanceof WitherBoss witherBoss) {
            int i = witherBoss.getInvulnerableTicks();
            if (i > 80) {
                cir.setReturnValue(Mth.clamp(1f - ((100 - i) / 20f), 0f, 1f));
            }
        }
    }

    // EnableExplodeByDie

    @Inject(method = "getWhiteOverlayProgress", at = @At("RETURN"), cancellable = true)
    private void getWhiteOverlayProgressInject2(T livingEntity, float f, CallbackInfoReturnable<Float> cir) {
        if (BEStyleWither.getConfig().isEnableExplodeByDie() && livingEntity instanceof WitherBoss witherBoss) {
            float deathTime = BEStyleWitherUtils.getWitherDeltaDeathTime(witherBoss, f);
            if (deathTime > 0) {
                cir.setReturnValue(Mth.clamp(deathTime, 0f, 1f));
            }
        }
    }

    @WrapWithCondition(
            method = "setupRotations",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V", ordinal = 1)
    )
    private boolean setupRotationsWrapCondition(PoseStack instance, Quaternionf quaternionf, @Local(argsOnly = true) T livingEntity) {
        return !BEStyleWither.getConfig().isEnableExplodeByDie() || !(livingEntity instanceof WitherBoss);
    }

    @WrapOperation(
            method = "getOverlayCoords",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/OverlayTexture;v(Z)I", ordinal = 0)
    )
    private static int getOverlayCoordsWarp(boolean bl, Operation<Integer> original, @Local(argsOnly = true) LivingEntity livingEntity) {

        if (BEStyleWither.getConfig().isEnableExplodeByDie() && livingEntity instanceof WitherBoss) {
            return original.call(livingEntity.hurtTime > 0);
        }

        return original.call(bl);
    }
}
