package com.rempler.jagahm;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;

public class JAGAHM {
    public static final TagKey<Block> WHITELIST = TagKey.create(BuiltInRegistries.BLOCK.key(), ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "whitelist"));
    public static final TagKey<Block> BLACKLIST = TagKey.create(BuiltInRegistries.BLOCK.key(), ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "blacklist"));
    public static boolean hasPlayedSound = false;
    public static int tickCounter = 0;
    public static int fartCounter = 0;

    public static BlockState getReplantState(BlockState state) {
        if (state.getBlock() instanceof CocoaBlock) {
            return state.setValue(CocoaBlock.AGE, 0);
        } else if (state.getBlock() instanceof CropBlock cropBlock) {
            return cropBlock.getStateForAge(0);
        } else if (state.getBlock() instanceof NetherWartBlock) {
            return state.setValue(NetherWartBlock.AGE, 0);
        }

        return state;
    }

    public static void dropStacks(BlockState state, ServerLevel level, BlockPos pos, Entity entity,
                                  ItemStack toolStack) {
        Item replant = state.getBlock().getCloneItemStack(level, pos, state).getItem();
        final boolean[] removedReplant = { false };
        Block.getDrops(state, level, pos, null, entity, toolStack).forEach(stack -> {
            if (!removedReplant[0] && stack.getItem() == replant) {
                stack.setCount(stack.getCount() - 1);
                removedReplant[0] = true;
            }
            Block.popResource(level, pos, stack);
        });
        state.spawnAfterBreak(level, pos, toolStack, true);
    }

    public static boolean isMature(BlockState state) {
        Block block = state.getBlock();
        return switch (block) {
            case CocoaBlock ignored -> state.getValue(CocoaBlock.AGE) >= CocoaBlock.MAX_AGE;
            case CropBlock cropBlock -> cropBlock.isMaxAge(state);
            case NetherWartBlock ignored2 -> state.getValue(NetherWartBlock.AGE) >= NetherWartBlock.MAX_AGE;
            case SugarCaneBlock ignored3 -> state.getValue(SugarCaneBlock.AGE) >= BlockStateProperties.MAX_AGE_15;
            default -> false;
        };
    }

    public static InteractionResult onRightClick(Player player, InteractionHand hand, Level level, BlockHitResult blockHit) {
        if (player.isSpectator() || hand == InteractionHand.OFF_HAND) {
            return InteractionResult.PASS;
        }

        BlockState state = level.getBlockState(blockHit.getBlockPos());
        Block originalBlock = state.getBlock();
        ItemStack stack = player.getItemInHand(hand);

        if (originalBlock instanceof CropBlock || originalBlock instanceof CocoaBlock || originalBlock instanceof NetherWartBlock) {
            if (JAGAHM.isMature(state)) {
                if (!level.isClientSide) {
                    level.setBlockAndUpdate(blockHit.getBlockPos(), JAGAHM.getReplantState(state));
                    JAGAHM.dropStacks(state, (ServerLevel) level, blockHit.getBlockPos(), player, player.getItemInHand(hand));
                } else {
                    player.playSound(originalBlock instanceof NetherWartBlock ? SoundEvents.NETHER_WART_PLANTED : SoundEvents.CROP_PLANTED, 1.0f, 1.0f);
                }

                return InteractionResult.SUCCESS;
            }
        }
        else if (originalBlock instanceof SugarCaneBlock) {
            if (blockHit.getDirection() == Direction.UP && stack.getItem() == Items.SUGAR_CANE) {
                return InteractionResult.PASS;
            }
            int count = 1;
            BlockPos bottom = blockHit.getBlockPos().below();
            while (level.getBlockState(bottom).is(Blocks.SUGAR_CANE)) {
                count++;
                bottom = bottom.below();
            }

            if (count == 1 && !level.getBlockState(blockHit.getBlockPos().above()).is(Blocks.SUGAR_CANE)) {
                return InteractionResult.PASS;
            }

            if (!level.isClientSide) {
                level.destroyBlock(bottom.above(2), true);
            } else {
                player.playSound(SoundEvents.CROP_PLANTED, 1.0f, 1.0f);
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    public static void spawnParticles(Player player, Level level, BlockPos blockPos) {
        double d0 = level.getRandom().nextDouble();
        for (int a = 0; a < 2; a++) {
            //TODO: make custom particle
            ((ServerLevel) level).sendParticles((ServerPlayer) player, ParticleTypes.CLOUD, false, blockPos.getX() + d0,
                    blockPos.getY() + d0, blockPos.getZ() + d0, 1, 0.5, 0.5, 0.5, 0.01);
        }
    }
}
