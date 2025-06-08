package com.rempler.jagahm;

import com.rempler.jagahm.compat.AgriCraftCompat;
import com.rempler.jagahm.compat.MysticalAgriCompat;
import com.rempler.jagahm.platform.Services;
import com.teamresourceful.resourcefulconfig.api.loader.Configurator;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;

public class JAGAHMFabric implements ModInitializer {
    public static final PoopItem POOP = new PoopItem();
    public static final SoundEvent FART = SoundEvent.createVariableRangeEvent(new ResourceLocation(Constants.MOD_ID, "fart"));
    public static final Configurator CONFIGURATOR = new Configurator(Constants.MOD_ID);

    @Override
    public void onInitialize() {
        CommonClass.init();
        CONFIGURATOR.register(ModConfig.class);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Constants.MOD_ID, "poop"), POOP);
        Registry.register(BuiltInRegistries.SOUND_EVENT, new ResourceLocation(Constants.MOD_ID, "fart"), FART);
    }

    public static void applyGrowing(Player player) {
        Level level = player.level();
        BlockPos pos = player.blockPosition();

        if (level.isClientSide) {
            return;
        }

        int a = ModConfig.getGrowRange()/2;
        int b = ModConfig.getGrowHeight()/2;

        for (int x = -a; x <= a; x++) {
            for (int z = -a; z <= a; z++) {
                for (int y = -b; y <= b; y++) {
                    BlockPos blockPos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    BlockState state = level.getBlockState(blockPos);
                    if (Services.PLATFORM.isModLoaded("agricraft") && !(state.getBlock() instanceof CropBlock)) {
                        if (ModConfig.activateAgriCraft()) {
                            AgriCraftCompat.initAgriCompat(level, blockPos, player);
                        }
                    } else if (level.getRandom().nextDouble() < ModConfig.getRandomSpeed()) {
                        if (!(state.getBlock() instanceof AirBlock || state.is(JAGAHM.BLACKLIST))) {
                            if (Services.PLATFORM.isModLoaded("mysticalagriculture")) {
                                if (ModConfig.activateMystAgri()) {
                                    //MysticalAgriCompat.initMysticalAgriCompat(level, blockPos, state, player);
                                    //TODO: Implement Mystical Agriculture compat when available
                                    standardGrow(player, level, blockPos, state);
                                } else {
                                    standardGrow(player, level, blockPos, state);
                                }
                            } else {
                                standardGrow(player, level, blockPos, state);
                            }
                        }
                    }
                }
            }
        }
    }

    private static void standardGrow(Player player, LevelAccessor level, BlockPos blockPos, BlockState state) {
        if (state.hasProperty(CropBlock.AGE)) {
            if (JAGAHM.isMature(state)) {
                return;
            }
            BoneMealItem.growCrop(POOP.getDefaultInstance(), (Level) level, blockPos);
            spawnParticles(player, level, blockPos);
        } else if (state.getBlock() instanceof BonemealableBlock) {
            BoneMealItem.growCrop(POOP.getDefaultInstance(), (Level) level, blockPos);
            spawnParticles(player, level, blockPos);
        } else if (state.hasProperty(SugarCaneBlock.AGE)) {
            for (int j = state.getValue(SugarCaneBlock.AGE); j <= 15; j++) {
                if (j == 15) {
                    ((Level) level).setBlockAndUpdate(blockPos.above(), state.setValue(SugarCaneBlock.AGE, 0));
                    level.setBlock(blockPos, state.setValue(SugarCaneBlock.AGE, 0), 4);
                } else {
                    int newAge = j + 5;
                    if (newAge > 15) {
                        newAge = 15;
                    }
                    level.setBlock(blockPos, state.setValue(SugarCaneBlock.AGE, newAge), 4);
                    break;
                }
            }
        }
    }

    public static void spawnParticles(Player player, LevelAccessor level, BlockPos blockPos) {
        if (ModConfig.shouldSpawnParticles()) {
            JAGAHM.spawnParticles(player, (Level) level, blockPos);
        }
    }
}
