package net.morimori0317.bestylewither.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.morimori0317.bestylewither.entity.BEWitherBoss;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {
    @Inject(method = "getWhiteOverlayProgress", at = @At("RETURN"), cancellable = true)
    private void getWhiteOverlayProgress(T livingEntity, float f, CallbackInfoReturnable<Float> cir) {
        if (livingEntity instanceof WitherBoss witherBoss) {
            int i = witherBoss.getInvulnerableTicks();
            if (i > 80) {
                cir.setReturnValue(Mth.clamp(1f - ((100 - i) / 20f), 0f, 1f));
                return;
            }
            float wd = ((BEWitherBoss) witherBoss).getWitherDeathTime(f);
            if (wd > 0)
                cir.setReturnValue(Mth.clamp(wd, 0f, 1f));
        }
    }

    @Inject(method = "setupRotations", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;sqrt(F)F", ordinal = 0), cancellable = true)
    private void setupRotations(T livingEntity, PoseStack poseStack, float f, float g, float h, CallbackInfo ci) {
        if (livingEntity instanceof WitherBoss)
            ci.cancel();
    }

    @Inject(method = "getOverlayCoords", at = @At("HEAD"), cancellable = true)
    private static void getOverlayCoords(LivingEntity livingEntity, float f, CallbackInfoReturnable<Integer> cir) {
        if (livingEntity instanceof WitherBoss)
            cir.setReturnValue(OverlayTexture.pack(OverlayTexture.u(f), OverlayTexture.v(livingEntity.hurtTime > 0)));
    }
}
