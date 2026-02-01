package com.ubico.trimmedeffects.dataGen;

import com.ubico.trimmedeffects.block.blockRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Collections;
import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    public ModBlockLootTableProvider(HolderLookup.Provider registries) {
        // FeatureFlags.REGISTRY_GROUP is standard for 1.21.1
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        // This helper tells the block to drop itself
        this.dropSelf(blockRegistry.REFINEMENT_TABLE.get());

        // If you have other blocks, add them here
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        // Tell the provider to only look at blocks from your mod
        return blockRegistry.BLOCKS.getEntries().stream().map(e -> (Block) e.get())::iterator;
    }
}