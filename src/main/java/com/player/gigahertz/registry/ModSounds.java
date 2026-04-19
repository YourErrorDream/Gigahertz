package com.player.gigahertz.registry;

import com.player.gigahertz.Gigahertz;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Gigahertz.MODID);

    // Пример регистрации звука
    public static final RegistryObject<SoundEvent> RADIO_STATIC = SOUNDS.register(
            "radio_static",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Gigahertz.MODID, "radio_static"))
    );

    public static void register(IEventBus eventBus) {
        SOUNDS.register(eventBus);
    }
}