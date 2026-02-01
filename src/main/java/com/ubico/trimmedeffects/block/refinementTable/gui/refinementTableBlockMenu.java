package com.ubico.trimmedeffects.block.refinementTable.gui;

import com.ubico.trimmedeffects.AI_CODE.ModRecipes;
import com.ubico.trimmedeffects.block.blockRegistry;
import com.ubico.trimmedeffects.block.refinementTable.recipe.refinementTableBlockRecipe;
import com.ubico.trimmedeffects.block.refinementTable.recipe.refinementTableBlockRecipeInput;
import com.ubico.trimmedeffects.item.ModTags;
import com.ubico.trimmedeffects.item.itemRegistry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.UnknownNullability;

import javax.annotation.Nullable;
import java.util.List;
import java.util.OptionalInt;

public class refinementTableBlockMenu extends ItemCombinerMenu {
    public static final int TRIM_SLOT = 0;
    public static final int BASE_SLOT = 1;
    public static final int ADDITIONAL_SLOT = 2;
    public static final int RESULT_SLOT = 3;
    public static final int TRIM_SLOT_X_PLACEMENT = 8;
    public static final int BASE_SLOT_X_PLACEMENT = 26;
    public static final int ADDITIONAL_SLOT_X_PLACEMENT = 44;
    private static final int RESULT_SLOT_X_PLACEMENT = 98;
    public static final int SLOT_Y_PLACEMENT = 48;
    private final Level level;
        @Nullable
        private RecipeHolder<refinementTableBlockRecipe> selectedRecipe;
        private final List<RecipeHolder<refinementTableBlockRecipe>> recipes;

    public refinementTableBlockMenu(int containerId, Inventory playerInventory) {
            this(containerId, playerInventory, ContainerLevelAccess.NULL);
        }

    public refinementTableBlockMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(MenuType.SMITHING, containerId, playerInventory, access);
        this.level = playerInventory.player.level();

        // CHANGE THIS LINE: Use .get() on your DeferredHolder
        this.recipes = this.level.getRecipeManager().getAllRecipesFor(ModRecipes.REFINEMENT_RECIPE_TYPE.get());
        System.out.println("Recipes loaded: " + this.recipes.size());
    }
        @Override
        protected ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
            return ItemCombinerMenuSlotDefinition.create()
                    // Slot 0: Template Slot (Only allows your trims)
                    .withSlot(0, 8, 48, (stack) -> stack.is(ModTags.Items.REFINEMENT_TRIMS))

                    // Slot 1: Base Item Slot (Example: only allows pickaxes)
                    .withSlot(1, 26, 48, (stack) -> stack.getItem() instanceof TieredItem)

                    // Slot 2: Addition Slot (Example: only allows iron/diamond/etc.)
                    .withSlot(2, 44, 48, (stack) -> stack.is(net.minecraft.tags.ItemTags.TRIM_MATERIALS))

                    .withResultSlot(3, 98, 48)
                    .build();

        }

        @Override
        protected boolean isValidBlock(BlockState state) {
            // Check if the block at the position is still your table
            return state.is(blockRegistry.REFINEMENT_TABLE.get());
        }

        protected boolean mayPickup(Player player, boolean hasStack) {
            return this.selectedRecipe != null && this.selectedRecipe.value().matches(this.createRecipeInput(), this.level);
        }

        protected void onTake(Player player, ItemStack stack) {
            stack.onCraftedBy(player.level(), player, stack.getCount());
            this.resultSlots.awardUsedRecipes(player, this.getRelevantItems());
            this.shrinkStackInSlot(0);
            this.shrinkStackInSlot(1);
            this.shrinkStackInSlot(2);
            this.access.execute((p_40263_, p_40264_) -> p_40263_.levelEvent(1044, p_40264_, 0));
        }

        private List<ItemStack> getRelevantItems() {
            return List.of(this.inputSlots.getItem(0), this.inputSlots.getItem(1), this.inputSlots.getItem(2));
        }

    private refinementTableBlockRecipeInput createRecipeInput() {
        return new refinementTableBlockRecipeInput(
                this.inputSlots.getItem(0), // Trim/Template Slot
                this.inputSlots.getItem(1), // Base Item Slot
                this.inputSlots.getItem(2)  // Additional/Ingredient Slot
        );
    }

    public void createResult() {
        refinementTableBlockRecipeInput refinementrecipeinput = this.createRecipeInput();

        // CHANGE THIS LINE: Use .get() on your DeferredHolder
        List<RecipeHolder<refinementTableBlockRecipe>> list = this.level.getRecipeManager().getRecipesFor(ModRecipes.REFINEMENT_RECIPE_TYPE.get(), refinementrecipeinput, this.level);

        if (list.isEmpty()) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
        } else {
            RecipeHolder<refinementTableBlockRecipe> recipeholder = list.get(0);
            ItemStack itemstack = recipeholder.value().assemble(refinementrecipeinput, this.level.registryAccess());
            if (itemstack.isItemEnabled(this.level.enabledFeatures())) {
                this.selectedRecipe = recipeholder;
                this.resultSlots.setRecipeUsed(recipeholder);
                this.resultSlots.setItem(0, itemstack);
            }
        }
    }

    private void shrinkStackInSlot(int index) {
            ItemStack itemstack = this.inputSlots.getItem(index);
            if (!itemstack.isEmpty()) {
                itemstack.shrink(1);
                this.inputSlots.setItem(index, itemstack);
            }
        }

        public int getSlotToQuickMoveTo(ItemStack stack) {
            return this.findSlotToQuickMoveTo(stack).orElse(0);
        }

        private static OptionalInt findSlotMatchingIngredient(@UnknownNullability refinementTableBlockRecipe recipe, ItemStack stack) {
            if (recipe.isTemplateIngredient(stack)) {
                return OptionalInt.of(0);
            } else if (recipe.isBaseIngredient(stack)) {
                return OptionalInt.of(1);
            } else {
                return recipe.isAdditionIngredient(stack) ? OptionalInt.of(2) : OptionalInt.empty();
            }
        }

        public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
            return slot.container != this.resultSlots && super.canTakeItemForPickAll(stack, slot);
        }

        public boolean canMoveIntoInputSlots(ItemStack stack) {
            return this.findSlotToQuickMoveTo(stack).isPresent();
        }

        private OptionalInt findSlotToQuickMoveTo(ItemStack stack) {
            return this.recipes.stream().flatMapToInt((p_300800_) -> findSlotMatchingIngredient(p_300800_.value(), stack).stream()).filter((p_294045_) -> !this.getSlot(p_294045_).hasItem()).findFirst();
        }
}
