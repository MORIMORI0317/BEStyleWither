package net.morimori0317.bestylewither.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.morimori0317.bestylewither.entity.WitherSkullModification;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public class PlayerMixin {

    @ModifyExpressionValue(
            method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;is(Lnet/minecraft/tags/TagKey;)Z")
    )
    private boolean attackModifyExpression(boolean original, @Local(argsOnly = true) Entity entity) {

        if (entity instanceof WitherSkull witherSkull && WitherSkullModification.isBounce(witherSkull)) {
            return true;
        }

        return original;
    }

}
