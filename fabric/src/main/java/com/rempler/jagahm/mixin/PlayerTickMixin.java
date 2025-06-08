package com.rempler.jagahm.mixin;

import com.rempler.jagahm.ModConfig;
import com.rempler.jagahm.JAGAHM;
import com.rempler.jagahm.JAGAHMFabric;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerTickMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo info) {
        onTick();
    }

    public void onTick() {
        Player player = (Player) (Object) this;
        Level level = player.level();

        if (player.isShiftKeyDown() || player.isSprinting()) {
            if (ModConfig.shouldTwerk() && level.getRandom().nextDouble() < ModConfig.getRandomSpeed() && JAGAHM.tickCounter >= ModConfig.getGrowSpeed()) {
                if (level.isClientSide) {
                    return;
                }
                JAGAHM.tickCounter = 0;
                JAGAHMFabric.applyGrowing(player);
            }
            if (ModConfig.shouldPoopSpawn() && player.isShiftKeyDown()) {
                JAGAHM.fartCounter++;
                if (JAGAHM.fartCounter > 200) {
                    if (level.getRandom().nextInt() * 200 <= JAGAHM.fartCounter) {
                        if (level.isClientSide) {
                            if (JAGAHM.hasPlayedSound) {
                                return;
                            }
                            if (ModConfig.shouldPoopSoundPlay()) {
                                level.playSound(player, player.getX(), player.getY(), player.getZ(), JAGAHMFabric.FART, SoundSource.PLAYERS, 1.0f, 1.0f);
                            }
                            JAGAHM.hasPlayedSound = true;
                        } else if (JAGAHM.hasPlayedSound) {
                            level.addFreshEntity(new ItemEntity(level, player.getX(), player.getY(), player.getZ(), new ItemStack(JAGAHMFabric.POOP)));
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
