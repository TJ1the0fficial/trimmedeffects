package com.ubico.trimmedeffects.AI_CODE;

import com.ubico.trimmedeffects.block.refinementTable.recipe.RefinementRecipe;
import com.ubico.trimmedeffects.block.refinementTable.recipe.refinementTableBlockRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModRecipes {
    // This helper class handles the timing for you
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, "trimmedeffects");

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, "trimmedeffects");

    // This is your new "Holder" for the recipe type
    public static final DeferredHolder<RecipeType<?>, RecipeType<refinementTableBlockRecipe>> REFINEMENT_RECIPE_TYPE =
            RECIPE_TYPES.register("refinement", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return "refinement";
                }
            });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<RefinementRecipe>> REFINEMENT_SERIALIZER =
            SERIALIZERS.register("refinement", RefinementRecipe.Serializer::new);

    public static void register(IEventBus eventBus) {
        RECIPE_TYPES.register(eventBus);
        SERIALIZERS.register(eventBus);
    }
}
