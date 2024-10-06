package net.morimori0317.bestylewither.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.morimori0317.bestylewither.entity.WitherSkullModification;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Projectile.class)
public class ProjectileMixin {

    @Inject(method = "isPickable", at = @At("RETURN"), cancellable = true)
    private void isPickableInject(CallbackInfoReturnable<Boolean> cir) {

        if (cir.getReturnValue()) {
            return;
        }

        Projectile ths = (Projectile) (Object) this;
        if (ths instanceof WitherSkull witherSkull && WitherSkullModification.isBounce(witherSkull)) {
            cir.setReturnValue(true);
        }
    }

    @ModifyExpressionValue(
            method = "onHit",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;is(Lnet/minecraft/tags/TagKey;)Z")
    )
    private boolean onHitModifyExpression(boolean original, @Local() Entity entity) {

        if (entity instanceof WitherSkull witherSkull && WitherSkullModification.isBounce(witherSkull)) {
            return true;
        }

        return original;
    }
}
