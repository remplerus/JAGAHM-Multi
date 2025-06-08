package com.rempler.jagahm;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {
    public static DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Constants.MOD_ID);
    public static final DeferredHolder<SoundEvent, SoundEvent> FART = SOUND_EVENTS.register("fart", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Constants.MOD_ID, "fart")));

    public static void init(IEventBus eventBus) {
        Constants.LOG.info("Registering sounds");
        SOUND_EVENTS.register(eventBus);
    }
}
