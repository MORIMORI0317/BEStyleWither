package net.morimori0317.bestylewither.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.boss.wither.WitherBoss;

public final class WitherBossInstance {
    public static final int HALF_HEALTH_EXPLODE_RETENTION_TICK = 20 * 2;
    public static final int HALF_HEALTH_EXPLODE_MAX_ELAPSED_TICK = 20 * 3;

    private final WitherBoss witherBoss;
    private final EntityDataAccessor<Boolean> onceWeakenedDataAccessor;

    private int chargeCoolDown;
    private int clientChargeTick;
    private int clientChargeTickOld;

    private boolean halfHealthExploded;
    private int halfHealthExplodeFallTick;
    private int halfHealthExplodeElapsedTick;

    private int explodeByDieDeathBlinkingTick;
    private boolean explodeByDieEnergyLayerVisible;

    private DamageSource explodeByDieDeathDamageSource;
    private boolean skipLootDrop;

    public WitherBossInstance(WitherBoss witherBoss, EntityDataAccessor<Boolean> onceWeakenedDataAccessor) {
        this.witherBoss = witherBoss;
        this.onceWeakenedDataAccessor = onceWeakenedDataAccessor;
    }

    public void saveToTag(CompoundTag tag) {
        tag.putBoolean("OnceWeakened", isOnceWeakened());
        tag.putBoolean("HalfHealthExploded", isHalfHealthExploded());
        tag.putBoolean("SkipLootDrop", isSkipLootDrop());
    }

    public void loadFromTag(CompoundTag tag) {
        setOnceWeakened(tag.getBoolean("OnceWeakened"));
        setHalfHealthExploded(tag.getBoolean("HalfHealthExploded"));
        setSkipLootDrop(tag.getBoolean("SkipLootDrop"));
    }

    public int getChargeCoolDown() {
        return chargeCoolDown;
    }

    public void setChargeCoolDown(int chargeCoolDown) {
        this.chargeCoolDown = chargeCoolDown;
    }

    public void setClientChargeTick(int clientChargeTick) {
        this.clientChargeTick = clientChargeTick;
    }

    public float getClientChargeTick(float delta) {
        return Mth.lerp(delta, clientChargeTickOld, clientChargeTick);
    }

    public int getClientChargeTick() {
        return clientChargeTick;
    }

    public int getClientChargeTickOld() {
        return clientChargeTickOld;
    }

    public void setClientChargeTickOld(int clientChargeTickOld) {
        this.clientChargeTickOld = clientChargeTickOld;
    }

    public void setOnceWeakened(boolean onceWeakened) {
        witherBoss.getEntityData().set(onceWeakenedDataAccessor, onceWeakened);
    }

    public boolean isOnceWeakened() {
        return witherBoss.getEntityData().get(onceWeakenedDataAccessor);
    }

    public void setHalfHealthExploded(boolean halfHealthExploded) {
        this.halfHealthExploded = halfHealthExploded;
    }

    public boolean isHalfHealthExploded() {
        return halfHealthExploded;
    }

    public void setHalfHealthExplodeFallTick(int halfHealthExplodeFallTick) {
        this.halfHealthExplodeFallTick = halfHealthExplodeFallTick;
    }

    public int getHalfHealthExplodeFallTick() {
        return halfHealthExplodeFallTick;
    }

    public void setHalfHealthExplodeElapsedTick(int halfHealthExplodeElapsedTick) {
        this.halfHealthExplodeElapsedTick = halfHealthExplodeElapsedTick;
    }

    public int getHalfHealthExplodeElapsedTick() {
        return halfHealthExplodeElapsedTick;
    }

    public void setExplodeByDieDeathDamageSource(DamageSource explodeByDieDeathDamageSource) {
        this.explodeByDieDeathDamageSource = explodeByDieDeathDamageSource;
    }

    public DamageSource getExplodeByDieDeathDamageSource() {
        return explodeByDieDeathDamageSource;
    }

    public int getExplodeByDieDeathBlinkingTick() {
        return explodeByDieDeathBlinkingTick;
    }

    public void setExplodeByDieDeathBlinkingTick(int explodeByDieDeathBlinkingTick) {
        this.explodeByDieDeathBlinkingTick = explodeByDieDeathBlinkingTick;
    }

    public boolean isExplodeByDieEnergyLayerVisible() {
        return explodeByDieEnergyLayerVisible;
    }

    public void setExplodeByDieEnergyLayerVisible(boolean explodeByDieEnergyLayerVisible) {
        this.explodeByDieEnergyLayerVisible = explodeByDieEnergyLayerVisible;
    }

    public void setSkipLootDrop(boolean skipLootDrop) {
        this.skipLootDrop = skipLootDrop;
    }

    public boolean isSkipLootDrop() {
        return skipLootDrop;
    }
}
