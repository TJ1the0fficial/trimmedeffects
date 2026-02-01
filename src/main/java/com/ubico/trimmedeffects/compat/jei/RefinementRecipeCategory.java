package com.ubico.trimmedeffects.compat.jei;

import com.ubico.trimmedeffects.TrimmedEffects;
import com.ubico.trimmedeffects.block.blockRegistry;
import com.ubico.trimmedeffects.block.refinementTable.recipe.RefinementRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

// Note: Category now handles RecipeHolder<RefinementRecipe> to fix the ID issue
public class RefinementRecipeCategory implements IRecipeCategory<RecipeHolder<RefinementRecipe>> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(TrimmedEffects.MODID, "refinement");
    public static final RecipeType<RecipeHolder<RefinementRecipe>> TYPE = new RecipeType<>(UID, (Class<RecipeHolder<RefinementRecipe>>) (Object) RecipeHolder.class);

    private final IDrawable background;
    private final IDrawable icon;

    public RefinementRecipeCategory(IGuiHelper helper) {
        // Precise coordinates for 1.21.1 smithing.png to make it look clean
        ResourceLocation texture = ResourceLocation.withDefaultNamespace("textures/gui/container/smithing.png");

        // We crop just the area containing the 3 input slots, the arrow, and the output slot
        // x: 7, y: 47, width: 120, height: 18
        this.background = helper.createDrawable(texture, 7, 47, 120, 18);
        this.icon = helper.createDrawableItemStack(new ItemStack(blockRegistry.REFINEMENT_TABLE.get()));
    }

    @Override
    public RecipeType<RecipeHolder<RefinementRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.trimmedeffects.refinement_table");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<RefinementRecipe> holder, IFocusGroup focuses) {
        RefinementRecipe recipe = holder.value();

        // Coordinates are now relative to our cropped background (0,0 is now x=7, y=47)
        // Slot 1 (Template)
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addIngredients(recipe.template());

        // Slot 2 (Base)
        builder.addSlot(RecipeIngredientRole.INPUT, 19, 1).addIngredients(recipe.base());

        // Slot 3 (Addition)
        builder.addSlot(RecipeIngredientRole.INPUT, 37, 1).addIngredients(recipe.addition());

        // Output Slot
        builder.addSlot(RecipeIngredientRole.OUTPUT, 91, 1).addItemStack(recipe.result());
    }
}