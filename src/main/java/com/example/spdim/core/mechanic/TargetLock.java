package com.example.spdim.core.mechanic;

import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
public class TargetLock {
    // Hashmap to store all player with the effect active.
    private static Map<LivingEntity, LivingEntity> targetLock = new HashMap<>();
    // Put the target to the hashmap.
    public static void lockTarget(LivingEntity attacker, LivingEntity target) {
        if (attacker == null || target == null) {
            return;
        }
       	targetLock.put(attacker, target);
    }

    public static void tick() {
        Iterator<Map.Entry<LivingEntity, LivingEntity>> iterator = targetLock.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<LivingEntity, LivingEntity> entry = iterator.next();
            LivingEntity entity = entry.getValue();
            // Remove the entity from the hashmap if the effect doesn't apply anymore.
            if (entity == null || !entity.isAlive() || entity.isRemoved()) {
                iterator.remove();
                continue;
            }
        }
    }

		public static void removeLockTarget(LivingEntity attacker) {
			targetLock.remove(attacker);
		}

    public static LivingEntity isLocked(LivingEntity attacker) {
        return targetLock.get(attacker);
    }
}

