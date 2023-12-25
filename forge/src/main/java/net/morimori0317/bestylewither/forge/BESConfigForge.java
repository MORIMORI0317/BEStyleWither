package net.morimori0317.bestylewither.forge;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import net.morimori0317.bestylewither.config.BESConfig;
import net.morimori0317.bestylewither.util.BEStyleWitherUtils;

public class BESConfigForge implements BESConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Supplier<Boolean> ENABLE_DOUBLE_HEALTH = Suppliers.memoize(BESConfigForge::isPreEnableDoubleHealth);

    private static ForgeConfigSpec.ConfigValue<Boolean> ENABLE_BOUNCE_BLUE_WITHER_SKULL;
    private static ForgeConfigSpec.ConfigValue<Boolean> ENABLE_MORE_INERTIAL_BLUE_WITHER_SKULL;
    private static ForgeConfigSpec.ConfigValue<Boolean> ENABLE_SHOOT_MORE_BLUE_WITHER_SKULL;
    private static ForgeConfigSpec.ConfigValue<Boolean> ENABLE_SPIN_AND_WHITE_SUMMON;
    private static ForgeConfigSpec.ConfigValue<Boolean> ENABLE_EXPLODE_BY_HALF_HEALTH;
    private static ForgeConfigSpec.ConfigValue<Boolean> ENABLE_EXPLODE_BY_DIE;
    private static ForgeConfigSpec.ConfigValue<Boolean> ENABLE_CHARGE_ATTACK;
    private static ForgeConfigSpec.ConfigValue<Boolean> ENABLE_MAINTAIN_WEAKENED_STATE;

    public static void init() {
        var commonConfig = buildCommonConfig(new ForgeConfigSpec.Builder()).build();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, commonConfig);
    }

    private static ForgeConfigSpec.Builder buildCommonConfig(ForgeConfigSpec.Builder builder) {
        ENABLE_BOUNCE_BLUE_WITHER_SKULL = builder.define("Enable bounce blue wither skull", BESConfig.DEFAULT.isEnableBounceBlueWitherSkull());
        ENABLE_MORE_INERTIAL_BLUE_WITHER_SKULL = builder.define("Enable more inertial blue wither skull", BESConfig.DEFAULT.isEnableMoreInertialBlueWitherSkull());
        ENABLE_SHOOT_MORE_BLUE_WITHER_SKULL = builder.define("Enable shoot more blue wither skull", BESConfig.DEFAULT.isEnableShootMoreBlueWitherSkull());
        ENABLE_SPIN_AND_WHITE_SUMMON = builder.define("Enable spin and white summon", BESConfig.DEFAULT.isEnableSpinAndWhiteSummon());
        ENABLE_EXPLODE_BY_HALF_HEALTH = builder.define("Enable explode by half health", BESConfig.DEFAULT.isEnableExplodeByHalfHealth());
        ENABLE_EXPLODE_BY_DIE = builder.define("Enable explode by die", BESConfig.DEFAULT.isEnableExplodeByDie());
        ENABLE_CHARGE_ATTACK = builder.define("Enable charge attack", BESConfig.DEFAULT.isEnableChargeAttack());
        ENABLE_MAINTAIN_WEAKENED_STATE = builder.define("Enable maintain weakened state", BESConfig.DEFAULT.DEFAULT.isEnableMaintainWeakenedState());
        //ENABLE_DOUBLE_HEALTH = builder.define("Enable double health", BESConfig.DEFAULT.DEFAULT.isEnableDoubleHealth());
        return builder;
    }

    @Override
    public boolean isEnableBounceBlueWitherSkull() {
        return ENABLE_BOUNCE_BLUE_WITHER_SKULL.get();
    }

    @Override
    public boolean isEnableMoreInertialBlueWitherSkull() {
        return ENABLE_MORE_INERTIAL_BLUE_WITHER_SKULL.get();
    }

    @Override
    public boolean isEnableShootMoreBlueWitherSkull() {
        return ENABLE_SHOOT_MORE_BLUE_WITHER_SKULL.get();
    }

    @Override
    public boolean isEnableSpinAndWhiteSummon() {
        return ENABLE_SPIN_AND_WHITE_SUMMON.get();
    }

    @Override
    public boolean isEnableExplodeByHalfHealth() {
        return ENABLE_EXPLODE_BY_HALF_HEALTH.get();
    }

    @Override
    public boolean isEnableExplodeByDie() {
        return ENABLE_EXPLODE_BY_DIE.get();
    }

    @Override
    public boolean isEnableChargeAttack() {
        return ENABLE_CHARGE_ATTACK.get();
    }

    @Override
    public boolean isEnableDoubleHealth() {
        return ENABLE_DOUBLE_HEALTH.get();
    }

    @Override
    public boolean isEnableMaintainWeakenedState() {
        return ENABLE_MAINTAIN_WEAKENED_STATE.get();
    }

    private static boolean isPreEnableDoubleHealth() {
        return BEStyleWitherUtils.loadOrGenerateForgeBaseEnableDoubleHealth(FMLPaths.CONFIGDIR.get().toFile());
    }

}
