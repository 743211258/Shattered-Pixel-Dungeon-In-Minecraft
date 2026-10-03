package com.example.spdim.core.mechanic;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.LivingEntity;

import com.example.spdim.core.functions.Functions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
public class TargetLock {
	// Hashmap to store all player with the effect active.
	private static Map<UUID, UUID> targetLock = new HashMap<>();
	// Put the target to the hashmap.
	public static void lockTarget(LivingEntity attacker, LivingEntity target) {
		if (attacker == null || target == null) {
			return;
		}
		targetLock.put(attacker.getUUID(), target.getUUID());
	}

	public static void tick(MinecraftServer server) {
		Iterator<Map.Entry<UUID, UUID>> iterator = targetLock.entrySet().iterator();

		while (iterator.hasNext()) {
			Map.Entry<UUID, UUID> entry = iterator.next();
			LivingEntity entity = Functions.findLivingEntity(server, entry.getValue());
			// Remove the entity from the hashmap if the effect doesn't apply anymore.
			if (entity == null || !entity.isAlive() || entity.isRemoved()) {
				iterator.remove();
				continue;
			}
		}
	}

	public static void removeLockTarget(LivingEntity attacker) {
		targetLock.remove(attacker.getUUID());
	}


	public static UUID isLocked(UUID attacker) {
		return targetLock.get(attacker);
	}
}

