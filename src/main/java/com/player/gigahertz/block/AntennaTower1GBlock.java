package com.player.gigahertz.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class AntennaTower1GBlock extends Block {

    public AntennaTower1GBlock() {
        super(Properties.of()
                .strength(3.5f, 6.0f)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops()
        );
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            player.displayClientMessage(
                    Component.literal("§8[§21G Tower§8] §7Active — relay range: §264 blocks"),
                    true  // action bar, not chat
            );
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}