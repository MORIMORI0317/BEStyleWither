package net.morimori0317.bestylewither.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.WitherBossModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.morimori0317.bestylewither.entity.BEWitherBoss;
import net.morimori0317.bestylewither.entity.goal.WitherChargeAttackGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WitherBossModel.class)
public class WitherBossModelMixin<T extends WitherBoss> {
    @Shadow
    @Final
    private ModelPart ribcage;

    @Shadow
    @Final
    private ModelPart tail;

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/boss/wither/WitherBoss;FFFFF)V", at = @At("TAIL"))
    private void setupAnim(T witherBoss, float f, float g, float h, float i, float j, CallbackInfo ci) {
        float ch = ((BEWitherBoss) witherBoss).getClientCharge(getPartialTicks());
        float cp = ch / ((float) WitherChargeAttackGoal.chargeTime);
        cp = Math.abs(-0.5f + cp);
        cp = 1f - cp / 0.5f;
        cp = Math.min(cp, 0.3f) / 0.3f;
        float val = 47f * cp;
        if (val > 0) {
            float b = -(float) Math.PI * 2f / 360f;
            this.ribcage.xRot += -val * b;
            this.tail.setPos(-2.0F, 6.9F + Mth.cos(this.ribcage.xRot) * 10.0F, -0.5F + Mth.sin(this.ribcage.xRot) * 10.0F);
            this.tail.xRot += -val * b;
        }
    }

    private float getPartialTicks() {
        var mc = Minecraft.getInstance();
        return mc.isPaused() ? mc.pausePartialTick : mc.getFrameTime();
    }
}
