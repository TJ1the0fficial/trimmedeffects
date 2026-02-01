package com.ubico.trimmedeffects.block;

import com.ubico.trimmedeffects.TrimmedEffects;
import com.ubico.trimmedeffects.block.refinementTable.refinementTableBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.*;

public class blockRegistry {

    public static DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(TrimmedEffects.MODID);

    public static DeferredBlock<Block> REFINEMENT_TABLE = BLOCKS.registerBlock(
            "refinement_table",
            (props) -> new refinementTableBlock(props),
            BlockBehaviour.Properties.ofFullCopy(Blocks.SMITHING_TABLE)
    );

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
