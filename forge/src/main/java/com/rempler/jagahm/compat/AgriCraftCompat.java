package com.rempler.jagahm.compat;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.crop.AgriCrop;
import com.rempler.jagahm.Config;
import com.rempler.jagahm.JAGAHMForge;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class AgriCraftCompat {
    private AgriCraftCompat(){}
    private static final RandomSource rand = RandomSource.create();

    public static void initAgriCompat(Level level, BlockPos blockPos, Player player) {
        Optional<AgriCrop> optional = AgriApi.getCrop(level, blockPos);
        if (optional.isPresent()) {
            AgriCrop crop = optional.get();
            if (rand.nextDouble() < Config.getGrowSpeed()*Config.getRandomSpeed()) {
                if (!crop.isFertile() && !Config.shouldCheckFertile()) {
                    crop.setGrowthStage(crop.getGrowthStage().getNext(crop, rand));
                } else {
                    crop.applyGrowthTick();
                    JAGAHMForge.spawnParticles(player, level, blockPos);
                }
            }
        }
    }
}
