package com.ubico.trimmedeffects.block.refinementTable.gui;

import com.ubico.trimmedeffects.TrimmedEffects;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class refinementTableBlockScreen extends ItemCombinerScreen<refinementTableBlockMenu> {
    // These are the sprites that cycle in the empty slot
    private static final List<ResourceLocation> TEMPLATE_SLOT_ICONS = List.of(
            ResourceLocation.fromNamespaceAndPath(TrimmedEffects.MODID, "item/empty_slot_tough_trim")
    );

    public refinementTableBlockScreen(refinementTableBlockMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title, ResourceLocation.withDefaultNamespace("textures/gui/container/smithing.png"));
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(graphics, partialTick, mouseX, mouseY);

        // If you want to change the ghost icon behavior, you can
        // manually draw sprites over empty slots here based on the 'TEMPLATE_SLOT_ICONS' list
        // and a timer (this.minecraft.level.getGameTime()).
    }

    @Override
    protected void renderErrorIcon(GuiGraphics guiGraphics, int i, int i1) {

    }
}
