package com.example.spdim.core.event;

import com.example.spdim.core.mechanic.RegenerationDisabled;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "spdim", bus = Mod.EventBusSubscriber.Bus.FORGE)

public class ChaliceOfBloodServerEvents {
    @SubscribeEvent
    public static void onLivingEntityHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        if (RegenerationDisabled.isDisabled(entity)) {
            event.setCanceled(true);
        }
    }
}
