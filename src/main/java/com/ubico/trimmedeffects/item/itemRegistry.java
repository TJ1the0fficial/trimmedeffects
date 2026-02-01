package com.ubico.trimmedeffects.item;

import com.ibm.icu.number.Precision;
import com.ubico.trimmedeffects.TrimmedEffects;
import com.ubico.trimmedeffects.block.blockRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;

import net.minecraft.world.item.ItemStack; // Standard Minecraft
import net.neoforged.neoforge.common.ItemAbility; // NeoForge singular type
import net.neoforged.neoforge.common.ItemAbilities; // NeoForge plural constants

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class itemRegistry {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TrimmedEffects.MODID);

    // ------ Items ------ //
    public static final DeferredItem<Item> WEIRD_APPLE = ITEMS.registerItem(
            "weird_apple",
            Item::new,
            new Item.Properties().component(
                    DataComponents.FOOD,
                    new FoodProperties.Builder()
                            .nutrition(4)
                            .saturationModifier(3.0f)
                            .effect(
                                    new MobEffectInstance(
                                            MobEffects.DIG_SPEED,
                                            1000,
                                            254
                                    ),
                                    1.0f
                            )
                            .alwaysEdible()
                            .build()
            )
    );

    public static final DeferredItem<Item> TOUGH_TRIM = ITEMS.registerItem(
            "tough_trim",
            Item::new,
            new Item.Properties()
    );

    public static final DeferredItem<Item> BOLT_TRIM = ITEMS.registerItem(
            "bolt_trim",
            Item::new,
            new Item.Properties()
    );

    public static final DeferredItem<Item> FURY_TRIM = ITEMS.registerItem(
            "fury_trim",
            Item::new,
            new Item.Properties()
    );

    public static final DeferredItem<Item> BREEZE_TRIM = ITEMS.registerItem(
            "breeze_trim",
            Item::new,
            new Item.Properties()
    );

    public static final List<DeferredItem<Item>> ALL_TRIMS = new ArrayList<>();

    // -- Block -- //
    public static final DeferredItem<BlockItem> REFINEMENT_TABLE = ITEMS.registerSimpleBlockItem(
            "refinement_table",
            blockRegistry.REFINEMENT_TABLE
    );

    // ------ Generated Item's list ------ //
    public static final Map<String,DeferredItem<Item>> GENERATED_ITEM_LIST = new HashMap<>();
    public static final Map<String,String> GENERATED_ITEM_LIST_NAMES = new HashMap<>();
    public static final Map<String,String> GENERATED_ITEM_MODEL_LIST_NAMES = new HashMap<>();
    public static final Map<String,String> GENERATED_ITEM_TRIM_LIST = new HashMap<>();
    public static final Map<String, String> GENERATED_ITEM_TRIM_TYPE = new HashMap<>();
    public static final Map<String,String> GENERATED_ITEM_TOOL_TYPES = new HashMap<>();
    public static final Map<String,String> GENERATED_ITEM_TOOL_TIER = new HashMap<>();

    public static void recordGeneratedItems(
            String final_name,
            String final_model_name,
            String final_display_name,
            String final_trim,
            String final_trim_type,
            String final_tool_type,
            String final_tier,
            DeferredItem<Item> item

    )
    {
        GENERATED_ITEM_LIST.put(final_name, item);
        GENERATED_ITEM_MODEL_LIST_NAMES.put(final_name, final_model_name);
        GENERATED_ITEM_LIST_NAMES.put(final_name, final_display_name);
        GENERATED_ITEM_TRIM_LIST.put(final_name, final_trim);
        GENERATED_ITEM_TRIM_TYPE.put(final_name, final_trim_type);
        GENERATED_ITEM_TOOL_TIER.put(final_name, final_tier);
        GENERATED_ITEM_TOOL_TYPES.put(final_name, final_tool_type);
    }


    // ------ Trimmed Tools ------ //

    // Trimmed Tools
    public enum TrimLevel { // trims of the tools
        NONE(0),
        IRON(1),
        REDSTONE(2),
        GOLD(3),
        DIAMOND(4);

        private final int TrimLevelValue;

        TrimLevel(int TrimLevelValue) {
            this.TrimLevelValue = TrimLevelValue;
        }

        public int getTrimLevelValue() {
            return TrimLevelValue;
        }
    }

    public enum TrimType {
        TOUGH("tough"),
        BOLT("bolt"),
        FURY("fury"),
        BREEZE("breeze");

        String type;

        TrimType(String type) {this.type = type;}
        public String get() {return this.type;}
    }

    private static Tier tierFromName(String name) { // possible tiers of tools
        return switch (name) {
            case "wooden" -> Tiers.WOOD;
            case "stone" -> Tiers.STONE;
            case "iron" -> Tiers.IRON;
            case "golden" -> Tiers.GOLD;
            case "diamond" -> Tiers.DIAMOND;
            case "netherite" -> Tiers.NETHERITE;
            default -> null;
        };
    }

    public enum ToolType { // tool type for the attributes
        NONE,
        SHOVEL,
        PICKAXE,
        AXE,
        HOE,
        SWORD
    }

    public static class TrimmedTool extends DiggerItem { // All the generated trimmable items come from this
        private final TrimLevel trimLevel;
        private final TrimType trimType;
        private final ToolType toolType;
        private final TagKey<Block> blockTags;

        public TrimmedTool(
                Tier tier,
                Properties properties,
                TrimLevel trimLevel,
                TrimType trimType,
                ToolType toolType,
                TagKey<Block> blockTags
        ) {
            super(tier, blockTags, properties);
            this.trimLevel = trimLevel;
            this.trimType = trimType;
            this.toolType = toolType;
            this.blockTags = blockTags;
        }

        @Override
        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

            int base = this.getTier().getUses();

            int level = this.trimLevel.getTrimLevelValue();

            // 1. Tough Trim (durability)
            if (this.trimType == TrimType.TOUGH && level > 0) {
                int durability = (int) (level * Math.sqrt(base) * 10.0f);

                tooltipComponents.add(Component.literal(" Durability Upgrade Tier: ")
                        .append(Component.literal("*" + level).withStyle(ChatFormatting.GREEN)));

                tooltipComponents.add(Component.literal(" New Bonus Max Durability: ")
                        .append(Component.literal("=" + durability).withStyle(ChatFormatting.GREEN)));
            }

            // 2. BOLT TRIM (Mining Speed)
            // Mining speed is HIDDEN in vanilla. To see it, we MUST add it here.
            if (this.trimType == TrimType.BOLT && level > 0) {
                // We call your existing speed logic
                float speed = this.getDestroySpeed(stack, Blocks.STONE.defaultBlockState());

                tooltipComponents.add(Component.literal(" Mining Speed: ")
                        .append(Component.literal("+" + String.format("%.1f", speed)).withStyle(ChatFormatting.GOLD)));
            }

            // 3. FURY TRIM (Attack Damage)
            if (this.trimType == TrimType.FURY && level > 0) {
                // This is just a visual reminder since the "Attack Damage" line
                // won't update automatically from your getAttackDamageBonus logic.
                float bonus = (level); // Match your Fury math here
                tooltipComponents.add(Component.literal(" Fury Bonus: ")
                        .append(Component.literal("+" + bonus + " Damage").withStyle(ChatFormatting.RED)));
            }

            if (this.trimType == TrimType.BREEZE && level > 0) {
                float speed = (level*2);

                tooltipComponents.add(Component.literal(" Breeze Upgrade Tier: ")
                        .append(Component.literal("*" + String.format("%.1f", speed)).withStyle(ChatFormatting.AQUA)));
            }
        }

        @Override
        public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
            // Check based on the toolType enum we assigned during generation
            if (this.toolType == ToolType.AXE && itemAbility == ItemAbilities.AXE_STRIP) return true;
            if (this.toolType == ToolType.SHOVEL && itemAbility == ItemAbilities.SHOVEL_FLATTEN) return true;
            if (this.toolType == ToolType.HOE && itemAbility == ItemAbilities.HOE_TILL) return true;

            // Fallback to default behavior for other actions
            return super.canPerformAction(stack, itemAbility);
        }

        /**
         * Logic to handle Axe Stripping, Shovel Flattening, and Hoe Tilling
         * Updated for NeoForge 1.21.1 using ItemAbilities
         */
        @Override
        public InteractionResult useOn(UseOnContext context) {
            Level level = context.getLevel();
            BlockPos pos = context.getClickedPos();
            BlockState state = level.getBlockState(pos);

            ItemAbility ability = null;
            SoundEvent sound = null;

            // Determine ability and sound based on your custom toolType enum
            if (this.toolType == ToolType.AXE) {
                ability = ItemAbilities.AXE_STRIP;
                sound = SoundEvents.AXE_STRIP;
            } else if (this.toolType == ToolType.SHOVEL) {
                ability = ItemAbilities.SHOVEL_FLATTEN;
                sound = SoundEvents.SHOVEL_FLATTEN;
            } else if (this.toolType == ToolType.HOE) {
                // The standard ability for tilling is HOE_TILL
                ability = ItemAbilities.HOE_TILL;
                sound = SoundEvents.HOE_TILL;
            }

            if (ability != null) {
                // NeoForge 1.21.1 uses getToolModifiedState with ItemAbility
                BlockState modifiedState = state.getToolModifiedState(context, ability, false);

                if (modifiedState != null) {
                    level.playSound(context.getPlayer(), pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
                    if (!level.isClientSide) {
                        level.setBlock(pos, modifiedState, 11);
                        if (context.getPlayer() != null) {
                            // Damage the tool
                            context.getItemInHand().hurtAndBreak(1, context.getPlayer(), LivingEntity.getSlotForHand(context.getHand()));
                        }
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }

            return super.useOn(context);
        }

        public TrimLevel getTrimLevel() {
            return trimLevel;
        }
        public ToolType getToolType() { return toolType; }

        @Override
        public int getEnchantmentValue() {
            return this.getTier().getEnchantmentValue();
        }

        // ------------ Trims's effects ------------ //
        @Override // durability increase
        public int getMaxDamage(ItemStack stack) { // This override was made by ChatGPT and I don't understand how it solved my issue, but it did ( The horrors of ItemStack )
            int base = this.getTier().getUses();
            int trimLevel = this.trimLevel.getTrimLevelValue();

            if (trimType == TrimType.TOUGH) { // I changed its code btw.

                if (trimLevel == 0) {
                    return base;
                }

                double k = 10.0;
                int bonus = (int) (trimLevel * Math.sqrt(base) * k);

                return base + bonus;
            }
            else { return base; }
        }

        @Override
        public float getDestroySpeed(ItemStack stack, BlockState state) {
//            return super.getDestroySpeed(stack, state);

            float base = super.getDestroySpeed(stack,state);

            int trimLevel = this.trimLevel.getTrimLevelValue();

            if (trimType == TrimType.BOLT) {

                if (trimLevel == 0) {
                    return base;
                }

                float k = 3.0f;

                base = (float) (trimLevel * Math.sqrt(base) * k);
            }
            return base;
        }

        @Override
        public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            // If it's a sword, only take 1 durability damage
            if (this.toolType == ToolType.SWORD) {
                stack.hurtAndBreak(1, attacker, LivingEntity.getSlotForHand(attacker.getUsedItemHand()));
                return true;
            }

            // Otherwise, use the default DiggerItem behavior (which is 2)
            return super.hurtEnemy(stack, target, attacker);
        }

        @Override
        public float getAttackDamageBonus(Entity target, float damage, DamageSource damageSource) {
            float bonus = 0; // Don't rely on super here
            int level = this.trimLevel.getTrimLevelValue();

            if (trimType == TrimType.FURY && level > 0) {
                // Use a flat bonus or a different calculation
                // because 'damage' (the parameter) is the total damage of the attack
                float k = 1.0f;
                bonus = (float) (level * k);
            }
            return bonus;
        }
    }

    public static double getAttackDamage(ItemStack stack) {
        // 1.0 is the base attack damage of a player (unarmed)
        double damage = 0.0;

        ItemAttributeModifiers modifiers = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);

        if (modifiers != null) {
            for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
                // Check if this modifier affects Attack Damage
                if (entry.attribute().equals(Attributes.ATTACK_DAMAGE)) {
                    damage += entry.modifier().amount();
                }
            }
        }
        return damage;
    }

    public static double getDefaultSpeed(Item item) {
        double speed = 1.0;

        // Access the default components assigned to the item during registration
        ItemAttributeModifiers modifiers = item.components().get(DataComponents.ATTRIBUTE_MODIFIERS);

        if (modifiers != null) {
            for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
                if (entry.attribute().equals(Attributes.ATTACK_SPEED)) {
                    speed += entry.modifier().amount();
                }
            }
        }
        return speed;
    }

    public static final Item.Properties newTrimmedToolItem( // this is a factory to make Item.Properties, so attributes can be specified by parameters
            Tier tier,
            float attackDamage,
            float attackSpeed
    ) {
        Item.Properties itemProperties =
                new Item.Properties()
                        .attributes(
                            DiggerItem.createAttributes(
                                    tier,
                                    attackDamage,
                                    attackSpeed
                            )
                        );
        if (tier == Tiers.NETHERITE) {
            itemProperties = itemProperties.fireResistant();
        }
        return itemProperties;
    }

    public static String[] id_tiers = {
            "wooden",
            "stone",
            "iron",
            "golden",
            "diamond",
            "netherite"
    };
    public static String[] id_tools = {
            "shovel",
            "pickaxe",
            "axe",
            "hoe",
            "sword"
    };
    public static String[] id_trims = {
            "ironed",
            "redstoned",
            "goldened",
            "diamonded"
    };

    public static void generateItems() // this generates all the trimmable items
    {
        System.out.println("\n\t --- Item generation will attempt to start... --- \t\n");

        String final_name;
        String final_display_name;
        String final_model_name;
        String final_trim;

        TagKey blocktag = null;

        ToolType currentToolType = null;

        int counter = 0;

        for (String name : id_tiers) {
            Tier tier = tierFromName(name);
            for (String trim : id_trims) {
                for (String tool : id_tools) {
                    for (TrimType trimType : TrimType.values()) {
                        TrimLevel trimLevel = switch (trim) {
                            case ("ironed") -> TrimLevel.IRON;
                            case ("redstoned") -> TrimLevel.REDSTONE;
                            case ("goldened") -> TrimLevel.GOLD;
                            case ("diamonded") -> TrimLevel.DIAMOND;
                            default -> TrimLevel.REDSTONE;
                        };

                        // finalize variables
                        final_name = trimType.get() + "_" + trim + "_" + name + "_" + tool;
                        final_model_name = name + "_" + tool;

                        String display_name = name.substring(0, 1).toUpperCase() + name.substring(1);
                        String display_tool = tool.substring(0, 1).toUpperCase() + tool.substring(1);

                        final_display_name = display_name + " " + display_tool;
                        final_trim = trim;

                        switch (tool) // I made this separate, because I was searching for a bug, then I thought that I actually don't need to change it.
                        {
                            case ("pickaxe") : {
                                currentToolType = ToolType.PICKAXE;
                                break;
                            }
                            case ("axe") : {
                                currentToolType = ToolType.AXE;
                                break;
                            }
                            case ("shovel") : {
                                currentToolType = ToolType.SHOVEL;
                                break;
                            }
                            case ("hoe") : {
                                currentToolType = ToolType.HOE;
                                break;
                            }
                            case ("sword") : {
                                currentToolType = ToolType.SWORD;
                                break;
                            }
                            default: {
                                currentToolType = ToolType.NONE;
                                break;
                            }
                        }

                        switch (currentToolType) {
                            case ToolType.NONE: {
                                break;
                            }
                            case ToolType.PICKAXE : {
                                blocktag = BlockTags.MINEABLE_WITH_PICKAXE;
                                break;
                            }
                            case ToolType.AXE : {
                                blocktag = BlockTags.MINEABLE_WITH_AXE;
                                break;
                            }
                            case ToolType.SHOVEL : {
                                blocktag = BlockTags.MINEABLE_WITH_SHOVEL;
                                break;
                            }
                            case ToolType.HOE : {
                                blocktag = BlockTags.MINEABLE_WITH_HOE;
                                break;
                            }
                            case ToolType.SWORD : {
                                blocktag = BlockTags.SWORD_EFFICIENT;
                                break;
                            }
                        }

                        ToolType finalCurrentToolType = currentToolType;
                        TagKey<Block> finalBlocktag = blocktag;

                        float attackDamage = 0;
                        float attackSpeed = 0;

                        switch (currentToolType) {
                            case SWORD -> {
                                attackDamage = 3.0f;
                                attackSpeed = -2.4f;
                            }
                            case PICKAXE -> {
                                attackDamage = 1.0f;
                                attackSpeed = -2.8f;
                            }
                            case SHOVEL -> {
                                attackDamage = 1.5f;
                                attackSpeed = -3.0f;
                            }
                            case HOE -> {
                                // This cancels out the Tier bonus so damage is always 1.0
                                attackDamage = -tier.getAttackDamageBonus();
                                attackSpeed = -3.0f;
                            }
                            case AXE -> {
                                // This makes the axe always 9.0 total damage
                                attackDamage = 8.0f - tier.getAttackDamageBonus();
                                attackSpeed = -3.1f;
                            }
                        }

                        if (currentToolType == ToolType.AXE) {
                            switch (tier) {
                                case Tiers.WOOD : {
                                    attackDamage -= 2;
                                    attackSpeed -= 0.1f;
                                    break;
                                }
                                case Tiers.STONE : {
                                    attackSpeed -= 0.1f;
                                    break;
                                }
                                case Tiers.IRON: {
                                    attackDamage += 0;
                                    break;
                                }
                                case Tiers.GOLD : {
                                    attackDamage -= 2;
                                    attackSpeed += 0.1f;
                                    break;
                                }
                                case Tiers.DIAMOND: {
                                    attackDamage += 0;
                                    attackSpeed += 0.1f;
                                    break;
                                }
                                case Tiers.NETHERITE: {
                                    attackDamage += 1;
                                    attackSpeed += 0.1f;
                                    break;
                                }
                                default:
                                    throw new IllegalStateException("Unexpected value: " + tier);
                            }
                        }

                        if (trimType == TrimType.BREEZE) {
                            attackSpeed += (float) (trimLevel.TrimLevelValue*2)/10;
                        }

                        // Now call your factory with these calculated values
                        Item.Properties props = newTrimmedToolItem(tier, attackDamage, attackSpeed);

                        DeferredItem<Item> item = ITEMS.register(
                                final_name,
                                () -> new TrimmedTool(
                                        tier,
                                        props,
                                        trimLevel,
                                        trimType,
                                        finalCurrentToolType,
                                        finalBlocktag
                                )
                        );
                        recordGeneratedItems(
                                final_name,
                                final_model_name,
                                final_display_name,
                                final_trim,
                                trimType.get(),
                                tool,
                                name,
                                item
                        );
                        counter++;
                        System.out.println("Item registered : " + final_name);
                        System.out.println("TrimType : "+ trimType.get());
                        System.out.println("counter : "+counter);
                    }
                }
            }
        }
    }
    public static void register(IEventBus eventBus) {
        ALL_TRIMS.add(TOUGH_TRIM);
        ALL_TRIMS.add(BOLT_TRIM);
        ALL_TRIMS.add(FURY_TRIM);
        ALL_TRIMS.add(BREEZE_TRIM);

        generateItems();
        ITEMS.register(eventBus);
    }
}
