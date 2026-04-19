package com.player.gigahertz.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class WalkieTalkie1GItem extends Item {

    public WalkieTalkie1GItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (level.isClientSide) {
            // Здесь будем открывать GUI (пока просто выводим сообщение в лог)
            System.out.println("Открываем интерфейс рации 1G");
            // TODO: добавить открытие экрана
        }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }
}