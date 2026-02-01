package com.ubico.trimmedeffects;

import com.ubico.trimmedeffects.block.blockRegistry;
import com.ubico.trimmedeffects.block.refinementTable.refinementTableBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, TrimmedEffects.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<refinementTableBlockEntity>> REFINEMENT_TABLE_BE =
            BLOCK_ENTITY_TYPES.register("refinement_table_block_entity",
                    () -> BlockEntityType.Builder.of(
                            refinementTableBlockEntity::new,
                            blockRegistry.REFINEMENT_TABLE.get() // Ensure this is registered!
                    ).build(null)
            );

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}