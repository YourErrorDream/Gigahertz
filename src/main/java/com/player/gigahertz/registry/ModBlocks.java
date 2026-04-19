package com.player.gigahertz.registry;

import com.player.gigahertz.Gigahertz;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Gigahertz.MODID);

    // Здесь будут блоки (пока пусто)
    // public static final RegistryObject<Block> ANTENNA_TOWER_1G = BLOCKS.register(...);

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}