package net.morimori0317.bestylewither.mixin.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.morimori0317.bestylewither.BEStyleWither;
import net.morimori0317.bestylewither.entity.BEWitherBoss;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {
    @Inject(method = "getWhiteOverlayProgress", at = @At("RETURN"), cancellable = true)
    private void getWhiteOverlayProgress(T livingEntity, float f, CallbackInfoReturnable<Float> cir) {
        if (livingEntity instanceof WitherBoss witherBoss) {
            if (BEStyleWither.getConfig().isEnableSpinAndWhiteSummon()) {
                int i = witherBoss.getInvulnerableTicks();
                if (i > 80) {
                    cir.setReturnValue(Mth.clamp(1f - ((100 - i) / 20f), 0f, 1f));
                    return;
                }
            }

            if (BEStyleWither.getConfig().isEnableExplodeByDie()) {
                float wd = ((BEWitherBoss) witherBoss).getWitherDeathTime(f);
                if (wd > 0)
                    cir.setReturnValue(Mth.clamp(wd, 0f, 1f));
            }
        }
    }
}
