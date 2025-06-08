package com.rempler.jagahm;

import com.teamresourceful.resourcefulconfig.api.annotations.Comment;
import com.teamresourceful.resourcefulconfig.api.annotations.Config;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.types.options.EntryType;

@Config(value = Constants.MOD_ID)
public class ModConfig {
    @ConfigEntry(
            id = "twerk_enabled",
            type = EntryType.BOOLEAN,
            translation = "config."+Constants.MOD_ID+".twerk_enabled"
    )
    @Comment("Set to false to disable twerking. Twerking is a mechanic that allows players to speed up crop growth by sneaking and jumping near them.")
    public static boolean twerkEnabled = true;
    @ConfigEntry(
            id = "poop_sound",
            type = EntryType.BOOLEAN,
            translation = "config."+Constants.MOD_ID+".poop_sound"
    )
    @Comment("Set to false to disable the poop sound when twerking.")
    public static boolean poopSound = true;
    @ConfigEntry(
            id = "poop_spawn",
            type = EntryType.BOOLEAN,
            translation = "config."+Constants.MOD_ID+".poop_spawn"
    )
    @Comment("Set to false to disable dropping poop when twerking.")
    public static boolean poopSpawn = true;
    @ConfigEntry(
            id = "activate_myst_agri",
            type = EntryType.BOOLEAN,
            translation = "config."+Constants.MOD_ID+".activate_myst_agri"
    )
    @Comment("Set to true to activate Mystical Agriculture compat (not working atm).")
    public static boolean activateMystAgri = false;
    @ConfigEntry(
            id = "activate_agricraft",
            type = EntryType.BOOLEAN,
            translation = "config."+Constants.MOD_ID+".activate_agricraft"
    )
    @Comment("Set to true to activate AgriCraft compat.")
    public static boolean activateAgriCraft = true;
    @ConfigEntry(
            id = "spawn_particles",
            type = EntryType.BOOLEAN,
            translation = "config."+Constants.MOD_ID+".spawn_particles"
    )
    @Comment("Set to false if you don't want to spawn particles on twerking / sprinting.")
    public static boolean spawnParticles = true;
    @ConfigEntry(
            id = "grow_speed",
            type = EntryType.INTEGER,
            translation = "config."+Constants.MOD_ID+".grow_speed"
    )
    @Comment("Change the speed of crop growth. Higher values mean faster growth. (Min: 1, Max: 10000)")
    public static int growSpeed = 20;
    @ConfigEntry(
            id = "random_speed",
            type = EntryType.DOUBLE,
            translation = "config."+Constants.MOD_ID+".random_speed"
    )
    @Comment("Change the random crop growth speed. Higher values mean faster growth. (Min: 0.1, Max: 1.0)")
    public static double randomSpeed = 0.1;
    @ConfigEntry(
            id = "grow_range",
            type = EntryType.INTEGER,
            translation = "config."+Constants.MOD_ID+".grow_range"
    )
    @Comment("Change the grow range of crops. Higher values mean larger range, but also more lag.")
    public static int growRange = 4;
    @ConfigEntry(
            id = "grow_height",
            type = EntryType.INTEGER,
            translation = "config."+Constants.MOD_ID+".grow_height"
    )
    @Comment("Change the grow height of crops. Higher values mean larger range, but also more lag.")
    public static int growHeight = 2;
    @ConfigEntry(
            id = "check_fertile",
            type = EntryType.BOOLEAN,
            translation = "config."+Constants.MOD_ID+".check_fertile"
    )
    @Comment("Should check for fertile soil when twerking. If set to false, twerking will work on any block. (AgriCraft only)")
    public static boolean checkFertile = true;

    public static boolean shouldTwerk() {
        return twerkEnabled;
    }
    public static boolean shouldPoopSpawn() {
        return poopSpawn;
    }
    public static boolean shouldPoopSoundPlay() {
        return poopSound;
    }
    public static int getGrowSpeed() {
        return growSpeed;
    }
    public static double getRandomSpeed() {
        return randomSpeed;
    }
    public static boolean activateMystAgri() {
        return activateMystAgri;
    }
    public static int getGrowRange() {
        return growRange;
    }
    public static int getGrowHeight() {
        return growHeight;
    }
    public static boolean shouldSpawnParticles() {
        return spawnParticles;
    }
    public static boolean activateAgriCraft() {
        return activateAgriCraft;
    }
    public static boolean shouldCheckFertile() { return checkFertile; }
}
