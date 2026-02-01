package com.ubico.trimmedeffects.compat.jei;

import com.ubico.trimmedeffects.TrimmedEffects;
import com.ubico.trimmedeffects.AI_CODE.ModRecipes;
import com.ubico.trimmedeffects.block.blockRegistry;
import com.ubico.trimmedeffects.block.refinementTable.recipe.RefinementRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;
import java.util.stream.Collectors;

@JeiPlugin
public class TrimmedEffectsJeiPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(TrimmedEffects.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        // Register our custom category
        registration.addRecipeCategories(new RefinementRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager rm = Minecraft.getInstance().level.getRecipeManager();

        // Pass the HOLDERS, not the values. This keeps the IDs intact.
        List<RecipeHolder<RefinementRecipe>> recipeHolders = rm.getAllRecipesFor(ModRecipes.REFINEMENT_RECIPE_TYPE.get())
                .stream()
                .filter(holder -> holder.value() instanceof RefinementRecipe)
                .map(holder -> new RecipeHolder<>(holder.id(), (RefinementRecipe) holder.value()))
                .collect(Collectors.toList());

        // Register using the Holder list
        registration.addRecipes(RefinementRecipeCategory.TYPE, recipeHolders);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        // This makes the Refinement Table block 'clickable' in JEI to show the recipes
        registration.addRecipeCatalyst(new ItemStack(blockRegistry.REFINEMENT_TABLE.get()), RefinementRecipeCategory.TYPE);
    }
}