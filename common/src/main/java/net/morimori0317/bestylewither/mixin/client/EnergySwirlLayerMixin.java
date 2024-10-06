package net.morimori0317.bestylewither.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PowerableMob;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.morimori0317.bestylewither.BEStyleWither;
import net.morimori0317.bestylewither.entity.BEWitherBoss;
import net.morimori0317.bestylewither.entity.ExplodeByDieConstants;
import net.morimori0317.bestylewither.util.BEStyleWitherUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EnergySwirlLayer.class)
public abstract class EnergySwirlLayerMixin<T extends Entity & PowerableMob, M extends EntityModel<T>> {

    @Unique
    private static final ResourceLocation POWER_LOCATION_BLUE = ResourceLocation.withDefaultNamespace("textures/entity/creeper/creeper_armor.png");

    // EnableExplodeByDie

    @ModifyExpressionValue(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/PowerableMob;isPowered()Z", ordinal = 0)
    )
    private boolean renderModifyExpression(boolean original, @Local(argsOnly = true) T entity) {

        if (BEStyleWither.getConfig().isEnableExplodeByDie() &&
                entity instanceof WitherBoss witherBoss && witherBoss.deathTime > ExplodeByDieConstants.ENERGY_LAYER_START_TICK) {
            return ((BEWitherBoss) witherBoss).beStyleWither$getInstance().isExplodeByDieEnergyLayerVisible();
        }

        return original;
    }

    @ModifyVariable(method = "render", at = @At("STORE"), ordinal = 6)
    private float renderModifyVar(float m, @Local(argsOnly = true) T entity, @Local(argsOnly = true, ordinal = 2) float delta) {

        if (BEStyleWither.getConfig().isEnableExplodeByDie() &&
                entity instanceof WitherBoss witherBoss && witherBoss.deathTime > ExplodeByDieConstants.ENERGY_LAYER_START_TICK) {
            return m * (1 + BEStyleWitherUtils.getWitherDeltaDeathTime(witherBoss, delta));
        }

        return m;
    }

    @WrapOperation(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/EnergySwirlLayer;getTextureLocation()Lnet/minecraft/resources/ResourceLocation;", ordinal = 0)
    )
    private ResourceLocation renderWarp(EnergySwirlLayer<T, M> instance, Operation<ResourceLocation> original, @Local(argsOnly = true) T entity) {

        if (BEStyleWither.getConfig().isEnableExplodeByDie() &&
                entity instanceof WitherBoss witherBoss && witherBoss.deathTime > ExplodeByDieConstants.ENERGY_LAYER_START_TICK) {
            return POWER_LOCATION_BLUE;
        }

        return original.call(instance);
    }
}
