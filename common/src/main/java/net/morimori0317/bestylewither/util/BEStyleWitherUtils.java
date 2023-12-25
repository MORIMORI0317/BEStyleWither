package net.morimori0317.bestylewither.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.morimori0317.bestylewither.BEStyleWither;
import net.morimori0317.bestylewither.config.BESConfig;
import org.apache.commons.io.FileUtils;

import java.io.*;

public class BEStyleWitherUtils {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static boolean loadOrGenerateForgeBaseEnableDoubleHealth(File configFolder) {
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
