package com.player.gigahertz.registry;

import com.player.gigahertz.Gigahertz;
import com.player.gigahertz.item.WalkieTalkie1GItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Gigahertz.MODID);

    public static final RegistryObject<Item> WALKIE_TALKIE_1G = ITEMS.register(
            "walkie_talkie_1g",
            () -> new WalkieTalkie1GItem(new Item.Properties().stacksTo(1))
    );

    // BlockItem регистрируется здесь — так блок появится в инвентаре
    public static final RegistryObject<Item> ANTENNA_TOWER_1G_ITEM = ITEMS.register(
            "antenna_tower_1g",
            () -> new BlockItem(ModBlocks.ANTENNA_TOWER_1G.get(), new Item.Properties())
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}