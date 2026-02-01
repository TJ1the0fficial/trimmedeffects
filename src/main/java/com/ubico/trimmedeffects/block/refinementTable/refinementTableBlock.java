package com.ubico.trimmedeffects.block.refinementTable;

import com.mojang.serialization.MapCodec;
import com.ubico.trimmedeffects.block.refinementTable.gui.refinementTableBlockMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class refinementTableBlock extends Block implements EntityBlock {
    public static final MapCodec<refinementTableBlock> CODEC = simpleCodec(refinementTableBlock::new);
    private static final Component CONTAINER_TITLE = Component.translatable("container.upgrade");

    public MapCodec<refinementTableBlock> codec() {
        return CODEC;
    }

    // Constructor deferring to super.
    public refinementTableBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    // Return a new instance of our block entity here.
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new refinementTableBlockEntity(pos, state);
    }

    protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new SimpleMenuProvider((p_277304_, p_277305_, p_277306_) -> new refinementTableBlockMenu(p_277304_, p_277305_, ContainerLevelAccess.create(level, pos)), CONTAINER_TITLE);
    }

    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            player.openMenu(state.getMenuProvider(level, pos));
            player.awardStat(Stats.INTERACT_WITH_SMITHING_TABLE);
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof Container) {
                Containers.dropContents(level, pos, (Container)blockentity);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
}
