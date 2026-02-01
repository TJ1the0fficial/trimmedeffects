package com.ubico.trimmedeffects.block.refinementTable.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ubico.trimmedeffects.AI_CODE.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public record RefinementRecipe(
        Ingredient template,
        Ingredient base,
        Ingredient addition,
        ItemStack result
) implements refinementTableBlockRecipe {

    @Override
    public boolean matches(refinementTableBlockRecipeInput input, Level level) {
        return this.template.test(input.template()) &&
                this.base.test(input.base()) &&
                this.addition.test(input.addition());
    }

    @Override
    public ItemStack assemble(refinementTableBlockRecipeInput input, HolderLookup.Provider registries) {
        ItemStack resultStack = this.result.copy();

        // Safety check: only copy if the base is not empty
        if (!input.base().isEmpty()) {
            resultStack.applyComponents(input.base().getComponents());
        }

        return resultStack;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.result;
    }

    // These helpers are used by your Menu for slot validation
    @Override
    public boolean isTemplateIngredient(ItemStack stack) {
        return this.template.test(stack); // 'template' is the Ingredient from the JSON
    }

    @Override
    public boolean isBaseIngredient(ItemStack stack) {
        return this.base.test(stack);
    }

    @Override
    public boolean isAdditionIngredient(ItemStack stack) {
        return this.addition.test(stack);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        // This MUST return the Serializer registered in ModRecipes
        return ModRecipes.REFINEMENT_SERIALIZER.get();
    }

    // --- SERIALIZER LOGIC ---
    public static class Serializer implements RecipeSerializer<RefinementRecipe> {
        // MapCodec defines how to read the JSON
        public static final MapCodec<RefinementRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.fieldOf("template").forGetter(RefinementRecipe::template),
                Ingredient.CODEC.fieldOf("base").forGetter(RefinementRecipe::base),
                Ingredient.CODEC.fieldOf("addition").forGetter(RefinementRecipe::addition),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(RefinementRecipe::result)
        ).apply(inst, RefinementRecipe::new));

        // StreamCodec defines how to send the recipe across the network (Server to Client)
        public static final StreamCodec<RegistryFriendlyByteBuf, RefinementRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, recipe) -> {
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.template);
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.base);
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.addition);
                    ItemStack.STREAM_CODEC.encode(buf, recipe.result);
                },
                buf -> new RefinementRecipe(
                        Ingredient.CONTENTS_STREAM_CODEC.decode(buf),
                        Ingredient.CONTENTS_STREAM_CODEC.decode(buf),
                        Ingredient.CONTENTS_STREAM_CODEC.decode(buf),
                        ItemStack.STREAM_CODEC.decode(buf)
                )
        );

        @Override public MapCodec<RefinementRecipe> codec() { return CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf, RefinementRecipe> streamCodec() { return STREAM_CODEC; }
    }
}