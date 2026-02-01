package com.ubico.trimmedeffects.block.refinementTable.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record refinementTableBlockRecipeInput(ItemStack template, ItemStack base, ItemStack addition) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> this.template;
            case 1 -> this.base;
            case 2 -> this.addition;
            default -> throw new IllegalArgumentException("Unexpected slot index: " + index);
        };
    }

    @Override
    public int size() {
        return 3; // You have 3 input slots
    }
}
