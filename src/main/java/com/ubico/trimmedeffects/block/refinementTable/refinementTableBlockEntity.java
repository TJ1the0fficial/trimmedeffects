package com.ubico.trimmedeffects.block.refinementTable;

import com.ubico.trimmedeffects.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class refinementTableBlockEntity extends BlockEntity {
    public refinementTableBlockEntity(BlockPos pos, BlockState state) {
        // Reference the holder from your new Registry class
        super(ModBlockEntities.REFINEMENT_TABLE_BE.get(), pos, state);
    }
}