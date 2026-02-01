package com.ubico.trimmedeffects;

import com.ubico.trimmedeffects.AI_CODE.ModRecipes;
import com.ubico.trimmedeffects.block.blockRegistry;
import com.ubico.trimmedeffects.dataGen.*;
import com.ubico.trimmedeffects.item.itemRegistry;
import com.ubico.trimmedeffects.loot.ModLootModifiers;


import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.item.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(TrimmedEffects.MODID)
public class TrimmedEffects {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "trimmedeffects";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    //



    //

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public TrimmedEffects(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        //

        ModRecipes.register(modEventBus); // AI code

        itemRegistry.register(modEventBus); // To register items

        blockRegistry.register(modEventBus);

        ModBlockEntities.register(modEventBus);

        ModLootModifiers.register(modEventBus);

        modEventBus.register(languageProvider.class);
        modEventBus.register(itemModelProvider.class);
        modEventBus.register(recipeProvider.class);

        //

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (TrimmedEffects) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        // Tell the mod bus to listen for the data generation event
        modEventBus.addListener(this::gatherData);
    }

    private void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> registries = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        // 1. Create the Block Tags provider and store it in a variable
        ModBlockTagsProvider blockTags = new ModBlockTagsProvider(output, registries, existingFileHelper);

        // 2. Add the Block Tags provider
        generator.addProvider(event.includeServer(), blockTags);

        // 3. Now you can use the 'blockTags' variable to call contentsGetter()
        generator.addProvider(event.includeServer(), new ModItemTagsProvider(output, registries, blockTags.contentsGetter(), existingFileHelper));

        // 4. Add the Loot Table provider
        generator.addProvider(event.includeServer(), new LootTableProvider(output, Set.of(),
                List.of(new LootTableProvider.SubProviderEntry(ModBlockLootTableProvider::new, LootContextParamSets.BLOCK)), registries));

        generator.addProvider(event.includeServer(),
                new ModGlobalLootModifierProvider(output, registries));
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        // 1. Functional Blocks
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.insertAfter(
                    new ItemStack(Items.SMITHING_TABLE),
                    new ItemStack(blockRegistry.REFINEMENT_TABLE.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
                    );
        }

        // 2. Ingredients
        else if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.insertAfter(
                    new ItemStack(Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE),
                    new ItemStack(itemRegistry.TOUGH_TRIM.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertAfter(
                    new ItemStack(itemRegistry.TOUGH_TRIM.get()),
                    new ItemStack(itemRegistry.BOLT_TRIM.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertAfter(
                    new ItemStack(itemRegistry.BOLT_TRIM.get()),
                    new ItemStack(itemRegistry.FURY_TRIM.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertAfter(
                    new ItemStack(itemRegistry.FURY_TRIM.get()),
                    new ItemStack(itemRegistry.BREEZE_TRIM.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
            );
        }

        // 3. Tools and Utilities (Everything EXCEPT Swords)
        else if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            // We iterate in the order we want: Tool Type -> Tier -> Trim
            for (String tool : itemRegistry.id_tools) {
                if (tool.equals("sword")) continue; // Swords go in Combat

                for (String tierName : itemRegistry.id_tiers) {
                    for (String trim : itemRegistry.id_trims) {
                        for (itemRegistry.TrimType trimType : itemRegistry.TrimType.values()) {
                            // Construct the key exactly as it was registered in generateItems()
                            String key = trimType.get() + "_" + trim + "_" + tierName + "_" + tool;

                            DeferredItem<Item> item = itemRegistry.GENERATED_ITEM_LIST.get(key);
                            if (item != null) {
                                event.accept(item);
                            }
                        }
                    }
                }
            }
        }

        // 4. Combat (Swords)
        else if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            for (String tierName : itemRegistry.id_tiers) {
                for (String trim : itemRegistry.id_trims) {
                    for (itemRegistry.TrimType trimType : itemRegistry.TrimType.values()) {
                        String key = trimType.get() + "_" + trim + "_" + tierName + "_sword";

                        DeferredItem<Item> item = itemRegistry.GENERATED_ITEM_LIST.get(key);
                        if (item != null) {
                            event.accept(item);
                        }
                    }
                }
            }
        }

        // 5. Food
        else if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.insertAfter(
                    new ItemStack(Items.APPLE),
                    new ItemStack(itemRegistry.WEIRD_APPLE.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
                            );
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }
}
