package net.morimori0317.bestylewither.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.WitherBossRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.morimori0317.bestylewither.BEStyleWither;
import net.morimori0317.bestylewither.entity.BEWitherBoss;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WitherBossRenderer.class)
public class WitherBossRendererMixin {

    @Shadow
    @Final
    private static ResourceLocation WITHER_INVULNERABLE_LOCATION;

    @Inject(method = "scale(Lnet/minecraft/world/entity/boss/wither/WitherBoss;Lcom/mojang/blaze3d/vertex/PoseStack;F)V", at = @At("HEAD"))
    private void scale(WitherBoss witherBoss, PoseStack poseStack, float f, CallbackInfo ci) {
        if (!BEStyleWither.getConfig().isEnableExplodeByDie())
            return;

        float wd = ((BEWitherBoss) witherBoss).getWitherDeathTime(f);
        if (wd > 0) {
            float wd2 = wd + 2f;
            float h = 1.0F + Mth.sin(wd2 * (wd2 / 20f) * 100f) * wd2 * 0.0075F;
            wd = Mth.clamp(wd, 0.0F, 1.0F);
            wd *= wd;
            wd *= wd;
            float i = (1.0F + wd * 0.4F) * h;
            float j = (1.0F + wd * 0.1F) / h;

            poseStack.scale(i, j, i);
        }
    }

    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/boss/wither/WitherBoss;)Lnet/minecraft/resources/ResourceLocation;", at = @At("RETURN"), cancellable = true)
    public void getTextureLocation(WitherBoss witherBoss, CallbackInfoReturnable<ResourceLocation> cir) {
        if (!BEStyleWither.getConfig().isEnableExplodeByDie())
            return;

        int wd = ((BEWitherBoss) witherBoss).getWitherDeathTime();
        if (wd > 0)
            cir.setReturnValue(WITHER_INVULNERABLE_LOCATION);
    }
}
