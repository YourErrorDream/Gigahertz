package com.player.gigahertz.registry;

import com.player.gigahertz.Gigahertz;
import com.player.gigahertz.block.AntennaTower1GBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Gigahertz.MODID);

    public static final RegistryObject<Block> ANTENNA_TOWER_1G = BLOCKS.register(
            "antenna_tower_1g",
            AntennaTower1GBlock::new
    );

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}