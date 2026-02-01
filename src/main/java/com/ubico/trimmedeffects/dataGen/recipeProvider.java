package com.ubico.trimmedeffects.dataGen;

import com.ubico.trimmedeffects.TrimmedEffects;
import com.ubico.trimmedeffects.block.blockRegistry;
import com.ubico.trimmedeffects.item.itemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

public class recipeProvider extends RecipeProvider {
    public recipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput exporter) {
        // Your existing Bone Trim recipe
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, itemRegistry.TOUGH_TRIM.get(), 2)
                .pattern("bTb")
                .pattern("bBb")
                .pattern("bbb")
                .define('T', itemRegistry.TOUGH_TRIM.get())
                .define('B', Items.BONE_BLOCK)
                .define('b', Items.BONE)
                .unlockedBy("has_bone", has(Items.BONE))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, itemRegistry.BOLT_TRIM.get(), 2)
                .pattern("gTg")
                .pattern("gGg")
                .pattern("ggg")
                .define('T', itemRegistry.BOLT_TRIM.get())
                .define('G', Items.GOLD_BLOCK)
                .define('g', Items.GOLD_INGOT)
                .unlockedBy("has_gold", has(Items.GOLD_INGOT))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, itemRegistry.FURY_TRIM.get(), 2)
                .pattern("nTn")
                .pattern("nSn")
                .pattern("nnn")
                .define('T', itemRegistry.FURY_TRIM.get())
                .define('S', Items.SOUL_SOIL)
                .define('n', Items.NETHERRACK)
                .unlockedBy("has_gold", has(Items.GOLD_INGOT))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, itemRegistry.BREEZE_TRIM.get(), 2)
                .pattern("cTc")
                .pattern("cSc")
                .pattern("ccc")
                .define('T', itemRegistry.BREEZE_TRIM.get())
                .define('S', Items.SOUL_LANTERN)
                .define('c', Items.COPPER_INGOT)
                .unlockedBy("has_gold", has(Items.GOLD_INGOT))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, blockRegistry.REFINEMENT_TABLE.get(), 1)
                .pattern("gg")
                .pattern("dd")
                .pattern("dd")
                .define('g', Items.GOLD_INGOT)
                .define('d', Items.DEEPSLATE)
                .unlockedBy("has_gold", has(Items.GOLD_INGOT))
                .save(exporter);

        // --- AUTOMATIC REFINEMENT RECIPES ---
//        String[] tiers = {"wooden", "stone", "iron", "golden", "diamond", "netherite"};
//        String[] trims = {"ironed", "redstoned", "goldened", "diamonded"};

        // FORCE item generation if the list is empty during DataGen
        if (itemRegistry.GENERATED_ITEM_LIST.isEmpty()) {
            System.out.println("DataGen: Item list was empty! Forcing generation...");
            itemRegistry.generateItems();
        }

        System.out.println("DataGen: Generating recipes for " + itemRegistry.GENERATED_ITEM_LIST.size() + " items.");
        int counter = 0;
        for (String tierName : itemRegistry.id_tiers) {
            for (String trimName : itemRegistry.id_trims) {
                for (String toolName : itemRegistry.id_tools) {
                    for (itemRegistry.TrimType trimType : itemRegistry.TrimType.values()) {
                        String resultKey = trimType.get() + "_" + trimName + "_" + tierName + "_" + toolName;

                        if (resultKey.equals("bolt_ironed_diamond_shovel")) {
                            System.out.println("Found! -> "+counter+". item : bolt_ironed_diamond_shovel");
                        }

                        if (!itemRegistry.GENERATED_ITEM_LIST.containsKey(resultKey)) {
                            System.out.println("Check recipeProvider!");
                            continue;
                        }

                        if (itemRegistry.GENERATED_ITEM_LIST.containsKey(resultKey)) {
//                            counter++;
//                            System.out.println(
//                                    "\nnewRecipe : " + resultKey +
//                                    "\n - recipe counter : " + counter +
//                                    "\n - trim's name : " + trimType.get() +
//                                    "\n - trimmed_name : " + trimName +
//                                    "\n - tier : " + tierName +
//                                    "\n - tool : " + toolName +
//                                    "\n - ingeridents : [\n\t" + trimType.get() + "_trim" + ",\n\t"+ trimName + ",\n\t" + tierName+"_"+toolName + "\n]\n"
//                            );
                            RefinementRecipeBuilder.refinement(
                                            Ingredient.of(getTrims(trimType)),   // Template
                                            Ingredient.of(getVanillaTool(tierName,toolName)),     // Base (e.g. Diamond Pickaxe)
                                            Ingredient.of(getMaterialForTrim(trimName)),    // Addition (e.g. Iron Ingot)
                                            itemRegistry.GENERATED_ITEM_LIST.get(resultKey).get() // Result Item
                                    )
                                    .unlockedBy("has_base", has(getVanillaTool(tierName,toolName)))
                                    .save(exporter, ResourceLocation.fromNamespaceAndPath(TrimmedEffects.MODID, resultKey));

                        System.out.println("DataGen: Generated recipe for " + resultKey);

                        if (tierName == "diamond") {
                            String smithingOutput = trimType.get() + "_" + trimName + "_" + "netherite" + "_" + toolName;
                            SmithingTransformRecipeBuilder.smithing(
                                        // The template ingredient.
                                        Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                                        // The base ingredient.
                                        Ingredient.of(itemRegistry.GENERATED_ITEM_LIST.get(resultKey).asItem()),
                                        // The addition ingredient.
                                        Ingredient.of(Items.NETHERITE_INGOT),
                                        // The recipe book category.
                                        RecipeCategory.TOOLS,
                                        // The result item. Note that while the recipe codec accepts an item stack here, the builder does not.
                                        // If you need an item stack output, you need to use your own builder.
                                        itemRegistry.GENERATED_ITEM_LIST.get(smithingOutput).asItem()
                                )
                                // The recipe advancement, like with the other recipes above.
                                .unlocks("has_netherite_ingot", has(Items.NETHERITE_INGOT))
                                // This overload of #save allows us to specify a name.
                                .save(exporter, resultKey + "_smithing");
                        }
                    }
                }
            }
        }
    }
}

    private Item getTrims(itemRegistry.TrimType trimType) {
        return switch (trimType) {
            case itemRegistry.TrimType.TOUGH -> itemRegistry.TOUGH_TRIM.get();
            case itemRegistry.TrimType.BOLT -> itemRegistry.BOLT_TRIM.get();
            case itemRegistry.TrimType.FURY -> itemRegistry.FURY_TRIM.get();
            case itemRegistry.TrimType.BREEZE -> itemRegistry.BREEZE_TRIM.get();
        };
    }

    private Item getVanillaTool(String tier,String tool) {
        return switch (tier) {
            case "wooden" -> BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace("wooden_" + tool));
            case "stone" -> BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace("stone_" + tool));
            case "iron" -> BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace("iron_" + tool));
            case "golden" -> BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace("golden_" + tool));
            case "diamond" -> BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace("diamond_" + tool));
            case "netherite" -> BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace("netherite_" + tool));
            default -> Items.AIR;
        };
    }

    private Item getMaterialForTrim(String trim) {
        return switch (trim) {
            case "ironed" -> Items.IRON_INGOT;
            case "redstoned" -> Items.REDSTONE;
            case "goldened" -> Items.GOLD_INGOT;
            case "diamonded" -> Items.DIAMOND;
            default -> Items.AIR;
        };
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> registries = event.getLookupProvider();

        // Add the language provider
        generator.addProvider(event.includeClient(), new recipeProvider(output,registries));
    }
}