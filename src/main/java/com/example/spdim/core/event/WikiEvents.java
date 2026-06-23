package com.example.spdim.core.event;

import com.example.spdim.core.wikiGUI.EncyclopediaScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = "spdim",
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public class WikiEvents {

    @SubscribeEvent
    public static void onClientTick(
            TickEvent.ClientTickEvent event
    ) {

        if (event.phase != TickEvent.Phase.END)
            return;

        while (
                ClientModEvents.OPEN_ENCYCLOPEDIA.consumeClick()
        ) {

            Minecraft.getInstance().setScreen(
                    new EncyclopediaScreen()
            );
        }
    }
}
