package com.ubico.trimmedeffects.dataGen;

import com.ubico.trimmedeffects.TrimmedEffects;
import com.ubico.trimmedeffects.block.blockRegistry;
import com.ubico.trimmedeffects.item.itemRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.data.LanguageProvider;

import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredItem;

import static com.ubico.trimmedeffects.item.itemRegistry.GENERATED_ITEM_LIST;
import static com.ubico.trimmedeffects.item.itemRegistry.GENERATED_ITEM_LIST_NAMES;

public class languageProvider extends LanguageProvider {

    public languageProvider(PackOutput output) {
        super(output, TrimmedEffects.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addItem(itemRegistry.WEIRD_APPLE, "Weird Apple");
        addItem(itemRegistry.TOUGH_TRIM, "Tough Trim");
        addItem(itemRegistry.BOLT_TRIM, "Bolt Trim");
        addItem(itemRegistry.FURY_TRIM, "Fury Trim");
        addItem(itemRegistry.BREEZE_TRIM, "Breeze Trim");

        addBlock(blockRegistry.REFINEMENT_TABLE, "Refinement Table");
//        addItem(itemRegistry.REFINEMENT_TABLE, "Refinement Table");

        for (String itemName : GENERATED_ITEM_LIST.keySet()) {
            DeferredItem<Item> item = GENERATED_ITEM_LIST.get(itemName);
            addItem(item, GENERATED_ITEM_LIST_NAMES.get(itemName));
        }
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();

        // Add the language provider
        generator.addProvider(event.includeClient(), new languageProvider(output));
    }
}


