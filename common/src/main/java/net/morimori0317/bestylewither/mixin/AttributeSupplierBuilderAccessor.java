package net.morimori0317.bestylewither.mixin;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(AttributeSupplier.Builder.class)
public interface AttributeSupplierBuilderAccessor {
    @Accessor
    Map<Attribute, AttributeInstance> getBuilder();
}
