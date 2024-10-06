package net.morimori0317.bestylewither.mixin;

import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


@Mixin(WitherBoss.class)
public interface WitherBossAccessor {
    @Accessor
    void setDestroyBlocksTick(int destroyBlocksTick);

    @Accessor
    ServerBossEvent getBossEvent();
}
