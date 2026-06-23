package com.example.spdim.core.event;

import com.example.spdim.core.mechanic.TargetLock;
import com.example.spdim.core.mechanic.Taunt;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "spdim", bus = Mod.EventBusSubscriber.Bus.FORGE)

public class DriedRoseServerEvents {
	@SubscribeEvent
	public static void onDogChangeTarget(LivingChangeTargetEvent event) {
		LivingEntity entity = event.getEntity(); 
		LivingEntity result = TargetLock.isLocked(entity);
		// Prevent the dog from choosing another target
		if (result != null) {
			event.setNewTarget(result);
		}
	}
	@SubscribeEvent
	public static void onTauntedPlayerAttack(LivingHurtEvent event) {
		Entity attacker = event.getSource().getEntity();
		Entity defender = event.getEntity();
		if (!Taunt.canAttack(attacker, defender)) {
			event.setCanceled(true);
		}
	}
}
