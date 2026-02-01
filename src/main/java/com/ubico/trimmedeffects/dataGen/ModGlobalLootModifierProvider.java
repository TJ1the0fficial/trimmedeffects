package com.ubico.trimmedeffects.dataGen;

import com.ubico.trimmedeffects.TrimmedEffects;
import com.ubico.trimmedeffects.item.itemRegistry;
import com.ubico.trimmedeffects.loot.AddItemModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;

import java.util.concurrent.CompletableFuture;

public class ModGlobalLootModifierProvider extends GlobalLootModifierProvider {
    public ModGlobalLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        // Super now takes: output, registries, and modId
        super(output, registries, TrimmedEffects.MODID);
    }

    @Override
    protected void start() {
        // Loop through ALL your trims
        for (net.neoforged.neoforge.registries.DeferredItem<net.minecraft.world.item.Item> trimHolder : itemRegistry.ALL_TRIMS) {
            net.minecraft.world.item.Item trimItem = trimHolder.get();
            String name = trimHolder.getId().getPath();

            add(name + "_in_chests", new AddItemModifier(new LootItemCondition[] {
                    // We ONLY need the random chance here.
                    // The "is it a chest?" check is now inside the AddItemModifier Java class!
                    LootItemRandomChanceCondition.randomChance(0.20f).build()
            }, trimItem));
        }
    }
}