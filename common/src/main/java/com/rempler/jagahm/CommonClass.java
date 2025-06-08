package com.rempler.jagahm;

import com.rempler.jagahm.platform.Services;

public class CommonClass {
    public static void init() {
        Constants.LOG.info("Loading "+ Constants.MOD_NAME + " " + Services.PLATFORM.getPlatformName() + " Mod");
    }
}