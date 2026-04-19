package com.player.gigahertz.registry;

import com.player.gigahertz.Gigahertz;
import com.player.gigahertz.item.WalkieTalkie1GItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Gigahertz.MODID);

    // Регистрируем рацию 1G
    public static final RegistryObject<Item> WALKIE_TALKIE_1G = ITEMS.register(
            "walkie_talkie_1g",
            () -> new WalkieTalkie1GItem(new Item.Properties().stacksTo(1))
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}