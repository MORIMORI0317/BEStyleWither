package net.morimori0317.bestylewither.fabric;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.minecraft.client.gui.screens.Screen;
import net.morimori0317.bestylewither.BEStyleWither;
import net.morimori0317.bestylewither.config.BESConfig;

@Config(name = BEStyleWither.MODID)
public class BESConfigFabric implements ConfigData, BESConfig {
    public boolean enableBounceBlueWitherSkull = DEFAULT.isEnableBounceBlueWitherSkull();
    public boolean enableMoreInertialBlueWitherSkull = DEFAULT.isEnableMoreInertialBlueWitherSkull();
    public boolean enableShootMoreBlueWitherSkull = DEFAULT.isEnableShootMoreBlueWitherSkull();
    public boolean enableSpinAndWhiteSummon = DEFAULT.isEnableSpinAndWhiteSummon();
    public boolean enableExplodeByHalfHealth = DEFAULT.isEnableExplodeByHalfHealth();
    public boolean enableExplodeByDie = DEFAULT.isEnableExplodeByDie();
    public boolean enableChargeAttack = DEFAULT.isEnableChargeAttack();
    @ConfigEntry.Gui.RequiresRestart
    public boolean enableDoubleHealth = DEFAULT.isEnableDoubleHealth();
    public boolean enableMaintainWeakenedState = DEFAULT.isEnableMaintainWeakenedState();

    @Override
    public boolean isEnableBounceBlueWitherSkull() {
        return enableBounceBlueWitherSkull;
    }

    @Override
    public boolean isEnableMoreInertialBlueWitherSkull() {
        return enableMoreInertialBlueWitherSkull;
    }

    @Override
    public boolean isEnableShootMoreBlueWitherSkull() {
        return enableShootMoreBlueWitherSkull;
    }

    @Override
    public boolean isEnableSpinAndWhiteSummon() {
        return enableSpinAndWhiteSummon;
    }

    @Override
    public boolean isEnableExplodeByHalfHealth() {
        return enableExplodeByHalfHealth;
    }

    @Override
    public boolean isEnableExplodeByDie() {
        return enableExplodeByDie;
    }

    @Override
    public boolean isEnableChargeAttack() {
        return enableChargeAttack;
    }

    @Override
    public boolean isEnableDoubleHealth() {
        return enableDoubleHealth;
    }

    @Override
    public boolean isEnableMaintainWeakenedState() {
        return enableMaintainWeakenedState;
    }

    public static BESConfigFabric createConfig() {
        return AutoConfig.register(BESConfigFabric.class, Toml4jConfigSerializer::new).getConfig();
    }

    public static Screen createConfigScreen(Screen parent) {
        return AutoConfig.getConfigScreen(BESConfigFabric.class, parent).get();
    }
}
