package com.player.gigahertz;

import com.player.gigahertz.registry.ModBlocks;
import com.player.gigahertz.registry.ModItems;
import com.player.gigahertz.registry.ModSounds;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Gigahertz.MODID)
public class Gigahertz {
    public static final String MODID = "assets/gigahertz";
    public static final Logger LOGGER = LogManager.getLogger();

    public Gigahertz() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();

        // Регистрируем все DeferredRegister'ы
        ModItems.register(bus);
        ModBlocks.register(bus);
        ModSounds.register(bus);

        bus.addListener(this::commonSetup);

        LOGGER.info("Gigahertz mod initialized. Prepare for cellular evolution!");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Gigahertz common setup complete.");
    }
}