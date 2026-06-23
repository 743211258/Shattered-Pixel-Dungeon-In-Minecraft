package com.example.spdim.core.wikiGUI;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class EmptyScreen extends Screen {

    public EmptyScreen() {
        super(Component.literal("Empty Screen"));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);

        graphics.drawString(
                font,
                "This is an empty screen",
                20,
                20,
                0xFFFFFF,
                false
        );

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
