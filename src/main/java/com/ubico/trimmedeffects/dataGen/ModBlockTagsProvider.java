package com.ubico.trimmedeffects.dataGen;

import com.ubico.trimmedeffects.TrimmedEffects;
import com.ubico.trimmedeffects.block.blockRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, TrimmedEffects.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // This line makes the Axe mine the block faster
        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(blockRegistry.REFINEMENT_TABLE.get());

        // OPTIONAL: If you want to require a certain tier (e.g. Stone or better)
        // tag(BlockTags.NEEDS_STONE_TOOL).add(blockRegistry.REFINEMENT_TABLE.get());
    }
}