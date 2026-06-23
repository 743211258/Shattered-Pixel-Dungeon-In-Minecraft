package com.example.spdim.core.wikiGUI;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;

import com.example.spdim.core.wikiGUI.EmptyScreen;
import net.minecraft.client.Minecraft;


public class EncyclopediaScreen extends Screen {

    public EncyclopediaScreen() {
       super(Component.literal("Encyclopedia"));
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        this.renderBackground(graphics);

        int frameWidth = 300;
        int frameHeight = 200;

        int left = (width - frameWidth) / 2;
        int top = (height - frameHeight) / 2;

        // 背景
        graphics.fill(
                left,
                top,
                left + frameWidth,
                top + frameHeight,
                0xCC202020
        );

        // 边框
        graphics.fill(left, top, left + frameWidth, top + 1, 0xFFFFFFFF);
        graphics.fill(left, top + frameHeight - 1, left + frameWidth, top + frameHeight, 0xFFFFFFFF);
        graphics.fill(left, top, left + 1, top + frameHeight, 0xFFFFFFFF);
        graphics.fill(left + frameWidth - 1, top, left + frameWidth, top + frameHeight, 0xFFFFFFFF);

        graphics.drawString(
                font,
                "Encyclopedia",
                left + 10,
                top + 10,
                0xFFFFFF,
                false
        );

        super.render(
                graphics,
                mouseX,
                mouseY,
                partialTick
        );
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {

        int btnW = 16;
        int btnH = 16;
        int startX = this.width / 2 - 30;
        int y = 10;

        addRenderableWidget(
                new net.minecraft.client.gui.components.ImageButton(
                        startX,
                        y,
                        btnW,
                        btnH,
                        0,
                        0,
                        20,
                        new net.minecraft.resources.ResourceLocation("spdim", "textures/item/wand_of_blast_wave.png"),
                        16,
                        16,
                        b -> Minecraft.getInstance().setScreen(new EmptyScreen()),
                        Component.literal("")
                )
        );

        addRenderableWidget(
                new net.minecraft.client.gui.components.ImageButton(
                        startX + 25,
                        y,
                        btnW,
                        btnH,
                        0,
                        0,
                        20,
                        new net.minecraft.resources.ResourceLocation("spdim", "textures/item/timekeepers_hourglass.png"),
                        16,
                        16,
                        b -> Minecraft.getInstance().setScreen(new EmptyScreen()),
                        Component.literal("")
                )
        );

        addRenderableWidget(
                new net.minecraft.client.gui.components.ImageButton(
                        startX + 50,
                        y,
                        btnW,
                        btnH,
                        0,
                        0,
                        20,
                        new net.minecraft.resources.ResourceLocation("spdim", "textures/item/wand_of_lightning.png"),
                        16,
                        16,
                        b -> Minecraft.getInstance().setScreen(new EmptyScreen()),
                        Component.literal("")
                )
        );
    }

}
