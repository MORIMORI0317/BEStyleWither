package net.morimori0317.bestylewither.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PowerableMob;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.morimori0317.bestylewither.BEStyleWither;
import net.morimori0317.bestylewither.entity.BEWitherBoss;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnergySwirlLayer.class)
public abstract class EnergySwirlLayerMixin<T extends Entity & PowerableMob> {
    @Unique
    private static final ResourceLocation POWER_LOCATION_BLUE = new ResourceLocation("textures/entity/creeper/creeper_armor.png");

    @Shadow
    protected abstract EntityModel<T> model();

    @Shadow
    protected abstract float xOffset(float f);


    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/Entity;FFFFFF)V", at = @At(value = "HEAD"), cancellable = true)
    private void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, T entity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        if (!BEStyleWither.getConfig().isEnableExplodeByDie())
            return;

        if (entity instanceof WitherBoss witherBoss && ((BEWitherBoss) witherBoss).getWitherDeathTime() > 0) {
            ci.cancel();
            float m = (float) entity.tickCount + h;
            m *= 1 + ((BEWitherBoss) witherBoss).getWitherDeathTime(h);
            VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.energySwirl(POWER_LOCATION_BLUE, this.xOffset(m) % 1.0F, m * 0.01F % 1.0F));
            EntityModel<T> entityModel = this.model();
            entityModel.setupAnim(entity, f, g, j, k, l);
            entityModel.renderToBuffer(poseStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY, 0.5F, 0.5F, 0.5F, 1.0F);
        }
    }
}
