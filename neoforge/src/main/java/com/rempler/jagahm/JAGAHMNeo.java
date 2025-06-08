package com.rempler.jagahm;

import com.agricraft.agricraft.api.crop.AgriCrop;
import com.agricraft.agricraft.api.plant.AgriPlant;
import com.rempler.jagahm.compat.AgriCraftCompat;
import com.rempler.jagahm.compat.MysticalAgriCompat;
import com.rempler.jagahm.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.TickEvent.PlayerTickEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(Constants.MOD_ID)
public class JAGAHMNeo {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, Constants.MOD_ID);
    public static final DeferredHolder<Item, PoopItem> POOP = ITEMS.register("poop", PoopItem::new);

    public JAGAHMNeo(IEventBus eventBus, ModContainer modContainer) {
        CommonClass.init();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC, Constants.MOD_ID + ".toml");
        NeoForge.EVENT_BUS.addListener(JAGAHMNeo::playerTickEvent);
        NeoForge.EVENT_BUS.addListener(JAGAHMNeo::onRightClickBlockEvent);
        NeoForge.EVENT_BUS.addListener(JAGAHMNeo::onBonemealEvent);
        ModSounds.init(eventBus);
        ITEMS.register(eventBus);
    }

    public static void playerTickEvent(PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Level level = event.player.level();
            Player player = event.player;
            if (player.isShiftKeyDown() || player.isSprinting()) {
                if (Config.shouldTwerk() && level.getRandom().nextDouble() < Config.getRandomSpeed() && JAGAHM.tickCounter >= Config.getGrowSpeed()) {
                    if (level.isClientSide) {
                        return;
                    }
                    JAGAHM.tickCounter = 0;
                    applyGrowing(player);
                }
                if (Config.shouldPoopSpawn() && player.isShiftKeyDown()) {
                    JAGAHM.fartCounter++;
                    if (JAGAHM.fartCounter > 200) {
                        if (level.getRandom().nextInt() * 200 <= JAGAHM.fartCounter) {
                            if (level.isClientSide) {
                                if (JAGAHM.hasPlayedSound) {
                                    return;
                                }
                                if (Config.shouldPoopSoundPlay()) {
                                    level.playSound(player, player.getX(), player.getY(), player.getZ(), ModSounds.FART.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
                                }
                                JAGAHM.hasPlayedSound = true;
                            } else if (JAGAHM.hasPlayedSound) {
                                level.addFreshEntity(new ItemEntity(level, player.getX(), player.getY(), player.getZ(), new ItemStack(POOP.get())));
                                JAGAHM.fartCounter = 0;
                                JAGAHM.hasPlayedSound = false;
                            }
                        }
                    }
                }
                JAGAHM.tickCounter++;
            }
        }
    }

    public static void onBonemealEvent(BonemealEvent event) {
        if (event.getStack().is(POOP.get())) {
            if (!(event.getBlock().is(BlockTags.CROPS) || event.getBlock().is(BlockTags.SAPLINGS) || event.getBlock().is(JAGAHM.WHITELIST))) {
                event.setCanceled(true);
            }
        }
    }

    public static void onRightClickBlockEvent(PlayerInteractEvent.RightClickBlock event) {
        InteractionResult result = JAGAHM.onRightClick(event.getEntity(), event.getHand(), event.getLevel(), event.getHitVec());

        if (result != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

    private static void applyGrowing(Player player) {
        Level level = player.level();
        BlockPos pos = player.blockPosition();

        if (level.isClientSide) {
            return;
        }

        int a = Config.getGrowRange()/2;
        int b = Config.getGrowHeight()/2;

        for (int x = -a; x <= a; x++) {
            for (int z = -a; z <= a; z++) {
                for (int y = -b; y <= b; y++) {
                    BlockPos blockPos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    BlockState state = level.getBlockState(blockPos);
                    if (Services.PLATFORM.isModLoaded("agricraft") && !(state.getBlock() instanceof CropBlock)) {
                        //if (Config.enablePosts()) {
                        //    player.sendSystemMessage(Component.literal("AgriCraft has no integration yet!"));
                        //}
                        if (Config.activateAgriCraft()) {
                            AgriCraftCompat.initAgriCompat(level, blockPos, player);
                        }
                    } else if (level.getRandom().nextDouble() < Config.getRandomSpeed()) {
                        if (!(state.getBlock() instanceof AirBlock || state.is(JAGAHM.BLACKLIST))) {
                            if (Services.PLATFORM.isModLoaded("mysticalagriculture")) {
                                //if (Config.enablePosts()) {
                                //    player.sendSystemMessage(Component.literal("Mystical Agriculture has no integration yet!"));
                                //}
                                if (Config.activateMystAgri()) {
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
            BoneMealItem.applyBonemeal(POOP.get().getDefaultInstance(), (Level) level, blockPos, player);
            spawnParticles(player, level, blockPos);
        } else if (state.getBlock() instanceof BonemealableBlock) {
            BoneMealItem.applyBonemeal(POOP.get().getDefaultInstance(), (Level) level, blockPos, player);
            spawnParticles(player, level, blockPos);
        } else if (state.hasProperty(SugarCaneBlock.AGE)) {
            for (int j = state.getValue(SugarCaneBlock.AGE); j <= 15; j++) {
                if (j == 15) {
                    ((Level) level).setBlockAndUpdate(blockPos.above(), state.setValue(SugarCaneBlock.AGE, 0));
                    net.neoforged.neoforge.common.CommonHooks.onCropsGrowPost((Level) level, blockPos.above(), state);
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
        if (Config.shouldSpawnParticles()) {
            JAGAHM.spawnParticles(player, (Level) level, blockPos);
        }
    }
}
