package com.ubico.trimmedeffects.block.refinementTable.recipe;

import com.ubico.trimmedeffects.AI_CODE.ModRecipes;
import com.ubico.trimmedeffects.block.blockRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public interface refinementTableBlockRecipe extends Recipe<refinementTableBlockRecipeInput> {
    @Override
    default RecipeType<?> getType() {
        return ModRecipes.REFINEMENT_RECIPE_TYPE.get();
    }

    default boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 1;
    }

    default ItemStack getToastSymbol() {
        return new ItemStack(blockRegistry.REFINEMENT_TABLE);
    }

    boolean isTemplateIngredient(ItemStack var1);

    boolean isBaseIngredient(ItemStack var1);

    boolean isAdditionIngredient(ItemStack var1);
}
