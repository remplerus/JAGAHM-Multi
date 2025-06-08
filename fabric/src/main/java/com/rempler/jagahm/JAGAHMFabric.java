package com.rempler.jagahm;

import com.rempler.jagahm.compat.MysticalAgriCompat;
import com.rempler.jagahm.platform.Services;
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
    public static final SoundEvent FART = SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "fart"));
    
    @Override
    public void onInitialize() {
        CommonClass.init();
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "poop"), POOP);
        Registry.register(BuiltInRegistries.SOUND_EVENT, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "fart"), FART);
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
                    if (Services.PLATFORM.isModLoaded("agricraft")) {
                        if (ModConfig.enablePosts()) {
                            player.sendSystemMessage(Component.literal("AgriCraft has no integration yet!"));
                        }
                        //if (Config.activateAgriCraft()) {
                        //    AgriCraftCompat.initAgriCompat(level, blockPos, player);
                        //}
                    } else if (level.getRandom().nextDouble() < ModConfig.getRandomSpeed()) {
                        BlockState state = level.getBlockState(blockPos);
                        if (!(state.getBlock() instanceof AirBlock || state.is(JAGAHM.BLACKLIST))) {
                            if (Services.PLATFORM.isModLoaded("mysticalagriculture")) {
                                //if (Config.enablePosts()) {
                                //    player.sendSystemMessage(Component.literal("Mystical Agriculture has no integration yet!"));
                                //}
                                if (ModConfig.activateMystAgri()) {
                                    MysticalAgriCompat.initMysticalAgriCompat(level, blockPos, state, player);
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
