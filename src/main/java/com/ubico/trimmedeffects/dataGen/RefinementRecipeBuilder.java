package com.ubico.trimmedeffects.dataGen;

import com.ubico.trimmedeffects.block.refinementTable.recipe.RefinementRecipe;
import com.ubico.trimmedeffects.item.itemRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class RefinementRecipeBuilder implements RecipeBuilder {
    private final Ingredient template;
    private final Ingredient base;
    private final Ingredient addition;
    private final Item result;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public RefinementRecipeBuilder(Ingredient template, Ingredient base, Ingredient addition, Item result) {
        this.template = template;
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    public static RefinementRecipeBuilder refinement(Ingredient template, Ingredient base, Ingredient addition, Item result) {
        return new RefinementRecipeBuilder(template, base, addition, result);
    }

    @Override
    public RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String group) { return this; }

    @Override
    public Item getResult() { return this.result; }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        Advancement.Builder advancementBuilder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancementBuilder::addCriterion);

        RefinementRecipe recipe = new RefinementRecipe(template, base, addition, new ItemStack(result));
        output.accept(id, recipe, advancementBuilder.build(id.withPrefix("recipes/")));
    }
}
