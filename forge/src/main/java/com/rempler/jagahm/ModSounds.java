package com.rempler.jagahm;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT.key(), Constants.MOD_ID);
    public static final RegistryObject<SoundEvent> FART = SOUND_EVENTS.register("fart", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Constants.MOD_ID, "fart")));

    public static void init(IEventBus eventBus) {
        Constants.LOG.info("Registering sounds");
        SOUND_EVENTS.register(eventBus);
    }
}
