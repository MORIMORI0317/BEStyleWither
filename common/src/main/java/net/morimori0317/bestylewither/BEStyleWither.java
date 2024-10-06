package net.morimori0317.bestylewither;

import net.morimori0317.bestylewither.config.BESConfig;
import net.morimori0317.bestylewither.explatform.BSWExpectPlatform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BEStyleWither {
    public static final Logger LOGGER = LogManager.getLogger(BEStyleWither.class);
    public static final String MODID = "bestylewither";

    public static void init() {
    }

    public static BESConfig getConfig() {
        return BSWExpectPlatform.getConfig();
    }
}
