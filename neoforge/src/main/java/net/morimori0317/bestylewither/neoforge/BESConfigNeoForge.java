package net.morimori0317.bestylewither.neoforge;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.morimori0317.bestylewither.BEStyleWither;
import net.morimori0317.bestylewither.config.BESConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.io.FileUtils;

import java.io.*;

public class BESConfigNeoForge implements BESConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Supplier<Boolean> ENABLE_DOUBLE_HEALTH = Suppliers.memoize(BESConfigNeoForge::isPreEnableDoubleHealth);

    private static ModConfigSpec.ConfigValue<Boolean> ENABLE_BOUNCE_BLUE_WITHER_SKULL;
    private static ModConfigSpec.ConfigValue<Boolean> ENABLE_MORE_INERTIAL_BLUE_WITHER_SKULL;
    private static ModConfigSpec.ConfigValue<Boolean> ENABLE_SHOOT_MORE_BLUE_WITHER_SKULL;
    private static ModConfigSpec.ConfigValue<Boolean> ENABLE_SPIN_AND_WHITE_SUMMON;
    private static ModConfigSpec.ConfigValue<Boolean> ENABLE_EXPLODE_BY_HALF_HEALTH;
    private static ModConfigSpec.ConfigValue<Boolean> ENABLE_EXPLODE_BY_DIE;
    private static ModConfigSpec.ConfigValue<Boolean> ENABLE_CHARGE_ATTACK;
    private static ModConfigSpec.ConfigValue<Boolean> ENABLE_MAINTAIN_WEAKENED_STATE;

    public static void init(ModContainer container) {
        ModConfigSpec commonConfig = buildCommonConfig(new ModConfigSpec.Builder()).build();
        container.registerConfig(ModConfig.Type.COMMON, commonConfig);

        ENABLE_DOUBLE_HEALTH.get();
    }

    private static ModConfigSpec.Builder buildCommonConfig(ModConfigSpec.Builder builder) {
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
        return loadOrGenerateForgeBaseEnableDoubleHealth(FMLPaths.CONFIGDIR.get().toFile());
    }

    private static boolean loadOrGenerateForgeBaseEnableDoubleHealth(File configFolder) {
        File configFile = new File(configFolder, BEStyleWither.MODID + "-pre.json");

        // Create config folder
        if (!configFolder.exists() && !configFolder.mkdirs()) {
            BEStyleWither.LOGGER.error("Failed to create folder for pre config");
            return BESConfig.DEFAULT.isEnableDoubleHealth();
        }

        boolean generateJson = false;
        JsonObject jo;

        if (configFile.exists()) {
            // Load json config
            try (Reader reader = new FileReader(configFile); Reader bufReader = new BufferedReader(reader)) {
                jo = GSON.fromJson(bufReader, JsonObject.class);
                boolean ret = jo.get("Enable double health").getAsBoolean();

                BEStyleWither.LOGGER.info("Pre config loading completed");
                return ret;
            } catch (IOException e) {
                BEStyleWither.LOGGER.error("Failed to load pre config", e);
            } catch (JsonSyntaxException e) {
                BEStyleWither.LOGGER.error("Pre config is corrupted and will be regenerated", e);

                // Backup corrupted config
                try {
                    File backupConfigFile = new File(configFolder, BEStyleWither.MODID + "-pre_bk.json");

                    // Delete existing backup
                    if (backupConfigFile.exists()) {
                        FileUtils.delete(backupConfigFile);
                    }

                    FileUtils.moveFile(configFile, backupConfigFile);

                    generateJson = true;
                } catch (IOException ioe) {
                    BEStyleWither.LOGGER.error("Failed to backup corrupted pre config", ioe);
                }
            }
        } else {
            BEStyleWither.LOGGER.info("Generate pre config because it does not exist");
            generateJson = true;
        }

        if (generateJson) {
            // Generate default config
            jo = new JsonObject();
            jo.addProperty("Enable double health", BESConfig.DEFAULT.isEnableDoubleHealth());
            try (Writer writer = new FileWriter(configFile); Writer bufWriter = new BufferedWriter(writer)) {
                GSON.toJson(jo, bufWriter);
            } catch (IOException e) {
                BEStyleWither.LOGGER.error("Failed to generate pre config", e);
            }
        }

        return BESConfig.DEFAULT.isEnableDoubleHealth();
    }
}
