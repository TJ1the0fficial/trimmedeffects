package com.ubico.trimmedeffects.dataGen;

import com.ubico.trimmedeffects.TrimmedEffects;
import com.ubico.trimmedeffects.block.blockRegistry;
import com.ubico.trimmedeffects.item.itemRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredItem;


public class itemModelProvider extends ItemModelProvider {
    public itemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, TrimmedEffects.MODID, existingFileHelper);
    }

    public static String getNameOnlyOfItem(DeferredItem<Item> item) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item.get());
        return id.getPath();
    }

    @Override
    protected void registerModels() {
        // Block items generally use their corresponding block models as parent.
//        withExistingParent(MyItemsClass.EXAMPLE_BLOCK_ITEM.getId().toString(), modLoc("block/example_block"));

        // Items generally use a simple parent and one texture. The most common parents are item/generated and item/handheld.
        // In this example, the item texture would be located at assets/examplemod/textures/item/example_item.png.
        // If you want a more complex model, you can use getBuilder() and then work from that, like you would with block models.
//        withExistingParent(itemRegistry.TOUGH_TRIM.getId().toString(), mcLoc("item/generated"))
//                .texture("layer0", "item/example_item");

        // The above line is so common that there is a shortcut for it. Note that the item registry name and the
        // texture path, relative to textures/item, must match.
//        basicItem(MyItemsClass.EXAMPLE_ITEM.get());

        withExistingParent(
                itemRegistry.WEIRD_APPLE.getId().toString(),
                mcLoc("item/generated"))
                    .texture("layer0","item/"+getNameOnlyOfItem(itemRegistry.WEIRD_APPLE));

        for (DeferredItem<Item> trim : itemRegistry.ALL_TRIMS) {
            withExistingParent(
                    trim.getId().toString(),
                    mcLoc("item/generated"))
                            .texture("layer0","item/"+getNameOnlyOfItem(trim));
        }

        withExistingParent(
                blockRegistry.REFINEMENT_TABLE.getId().toString(),
                modLoc("block/refinement_table"));

        for (String itemName : itemRegistry.GENERATED_ITEM_LIST.keySet()) {
            String itemModelName = itemRegistry.GENERATED_ITEM_MODEL_LIST_NAMES.get(itemName);
            DeferredItem<Item> item = itemRegistry.GENERATED_ITEM_LIST.get(itemName);
            String itemTrim = itemRegistry.GENERATED_ITEM_TRIM_LIST.get(itemName);
            String itemTrimType = itemRegistry.GENERATED_ITEM_TRIM_TYPE.get(itemName);
            String toolType = itemRegistry.GENERATED_ITEM_TOOL_TYPES.get(itemName);
            String itemTier = itemRegistry.GENERATED_ITEM_TOOL_TIER.get(itemName);

            withExistingParent(
                    item.getId().toString(),
                    mcLoc("item/generated"))
                        .texture("layer0", "item/trims/" + itemTrimType + "/" + toolType + "/" + itemTrimType + "_" + itemTrim + "_" + itemTier + "_" + toolType);
        }
    }

    @SubscribeEvent // on the mod event bus
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        // other providers here
        generator.addProvider(
                event.includeClient(),
                new itemModelProvider(output, existingFileHelper)
        );
    }
}
