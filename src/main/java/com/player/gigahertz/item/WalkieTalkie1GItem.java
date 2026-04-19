package com.player.gigahertz.item;

import com.player.gigahertz.screen.WalkieTalkieScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class WalkieTalkie1GItem extends Item {

    public WalkieTalkie1GItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            // DistExecutor keeps Minecraft (a client class) off the server classpath entirely
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    Minecraft.getInstance().setScreen(new WalkieTalkieScreen())
            );
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}