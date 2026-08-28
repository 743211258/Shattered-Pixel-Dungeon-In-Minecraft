package com.example.spdim.core.event;

import com.example.spdim.core.mechanic.Summon;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "spdim")

public class SummonServerEvents {
	@SubscribeEvent
	public static void onEntityTravelToDimension(EntityTravelToDimensionEvent event) {
		if (Summon.isSummoned(event.getEntity())) {
			event.setCanceled(true);
		}
	}
}

