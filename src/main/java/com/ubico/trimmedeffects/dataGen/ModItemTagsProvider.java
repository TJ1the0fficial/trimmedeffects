package com.ubico.trimmedeffects.dataGen;

import com.ubico.trimmedeffects.TrimmedEffects;
import com.ubico.trimmedeffects.item.itemRegistry;
import com.ubico.trimmedeffects.item.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                               CompletableFuture<TagLookup<net.minecraft.world.level.block.Block>> blockTags,
                               ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, TrimmedEffects.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // 1. Your existing refinement_trims logic
        for (DeferredItem<Item> trim : itemRegistry.ALL_TRIMS) {
            tag(ModTags.Items.REFINEMENT_TRIMS).add(trim.get());
        }

        // 2. Loop through generated items and sort them into enchantment tags
        itemRegistry.GENERATED_ITEM_LIST.forEach((name, itemHolder) -> {
            Item item = itemHolder.get();

            // Check if the item is one of your TrimmedTools
            if (item instanceof itemRegistry.TrimmedTool trimmedTool) {

                // Every tool should be able to get Unbreaking / Mending / Vanishing
                tag(ItemTags.DURABILITY_ENCHANTABLE).add(item);
                tag(ItemTags.VANISHING_ENCHANTABLE).add(item);

                // Now sort by the ToolType enum you created
                switch (trimmedTool.getToolType()) {
                    case itemRegistry.ToolType.PICKAXE, itemRegistry.ToolType.SHOVEL, itemRegistry.ToolType.AXE, itemRegistry.ToolType.HOE -> {
                        // Mining tools get Efficiency, Silk Touch, Fortune
                        tag(ItemTags.MINING_ENCHANTABLE).add(item);
                    }
                    case itemRegistry.ToolType.SWORD -> {
                        // Swords get Sharpness, Fire Aspect, Knockback, etc.
                        tag(ItemTags.SHARP_WEAPON_ENCHANTABLE).add(item);
                    }
                    case itemRegistry.ToolType.NONE -> {
                        // Do nothing or handle special cases
                    }
                }

                // Special case: Axes can be both Mining tools and Sharp Weapons in Vanilla
                if (trimmedTool.getToolType() == itemRegistry.ToolType.AXE) {
                    tag(ItemTags.SHARP_WEAPON_ENCHANTABLE).add(item);
                }
            }
        });
    }
}