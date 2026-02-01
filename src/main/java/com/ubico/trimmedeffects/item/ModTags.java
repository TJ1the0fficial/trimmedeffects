package com.ubico.trimmedeffects.item;

import com.ubico.trimmedeffects.TrimmedEffects;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {
    public static class Items {
        // MUST use Registries.ITEM and your MODID
        public static final TagKey<Item> REFINEMENT_TRIMS = TagKey.create(
                Registries.ITEM,
                ResourceLocation.fromNamespaceAndPath(TrimmedEffects.MODID, "refinement_trims")
        );
    }
}