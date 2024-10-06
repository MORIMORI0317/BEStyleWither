package net.morimori0317.bestylewither.mixin;

import net.minecraft.world.entity.projectile.WitherSkull;
import net.morimori0317.bestylewither.entity.WitherSkullModification;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WitherSkull.class)
public abstract class WitherSkullMixin {

    @Inject(method = "getInertia", at = @At("RETURN"), cancellable = true)
    private void getInertia(CallbackInfoReturnable<Float> cir) {
        if (WitherSkullModification.isMoreInertial((WitherSkull) (Object) this)) {
            cir.setReturnValue(0.90F);
        }
    }

}
