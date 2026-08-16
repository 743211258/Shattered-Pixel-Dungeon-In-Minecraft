package com.example.spdim.core.mechanic;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Taunt {
    // Hashmap to store all player with the effect active.
    private static Map<UUID, Float> clientTaunt = new HashMap<>();
    private static Map<UUID, Boolean> clientIsOn = new HashMap<>();
    private static Map<LivingEntity, Float> taunt = new HashMap<>();
    private static Map<LivingEntity, Boolean> isOn = new HashMap<>();
		private static Map<LivingEntity, List<LivingEntity>> tauntEntity = new HashMap<>();
		private static Map<LivingEntity, List<LivingEntity>> tauntedEntity = new HashMap<>();
		private static final float RADIUS = 16.0F;
		private static final float COST_PER_TICK = 0.1F;
		private static final float CHARGE_PER_TICK = 0.025F;
    // Put the target to the hashmap.
    public static void taunt(LivingEntity summonedTaunt) {
        if (summonedTaunt == null) {
            return;
        }
        taunt.put(summonedTaunt, 100.0F);
				isOn.put(summonedTaunt, false);
				clientTaunt.put(summonedTaunt.getUUID(), 100.0F);
				clientIsOn.put(summonedTaunt.getUUID(), false);
    }

		public static void control(LivingEntity summonedTaunt) {
			Boolean current = isOn.get(summonedTaunt);
			if (current == null) {
				return;
			}
			boolean next = !current;
			isOn.put(summonedTaunt, next);
			clientIsOn.put(summonedTaunt.getUUID(), next);
		}

    public static void tick() {
			Iterator<Map.Entry<LivingEntity, Float>> iterator = taunt.entrySet().iterator();
			/*Iterator<Map.Entry<LivingEntity, List<LivingEntity>>> tempIterator = tauntedEntity.entrySet().iterator();
			while (tempIterator.hasNext()) {
				Map.Entry<LivingEntity, List<LivingEntity>> entry = tempIterator.next();
				LivingEntity livingEntity = entry.getKey();
				if (livingEntity instanceof Mob mob) {
					mob.setTarget(null);
				}
			}*/
			tauntEntity.clear();
			tauntedEntity.clear();

			while (iterator.hasNext()) {
				Map.Entry<LivingEntity, Float> entry = iterator.next();
				LivingEntity summonedTaunt = entry.getKey();
				if (summonedTaunt == null || !summonedTaunt.isAlive() || summonedTaunt.isRemoved()) {
					iterator.remove();
					isOn.remove(summonedTaunt);
    			clientTaunt.remove(summonedTaunt.getUUID());
    			clientIsOn.remove(summonedTaunt.getUUID());
					continue;
				}
				Float charge = taunt.get(summonedTaunt);
				if (charge == null) {
					continue;
				}
				float currentCharge = charge.floatValue();
				Boolean current = isOn.get(summonedTaunt);
				if (current == null) {
					continue;
				}
				boolean isTauntOn = current.booleanValue();
				CompoundTag tag = null;
				UUID wolfUUID = null;
				if (summonedTaunt instanceof Wolf wolf) {
					Entity owner = wolf.getOwner();
					wolfUUID = wolf.getUUID();
					if (owner instanceof ServerPlayer serverPlayer) {
						ItemStack temp = serverPlayer.getOffhandItem();
						tag = temp.getTag();
					}
				}
				if (tag == null) {
					continue;
				}
				if (!(tag.contains("SummonedUUID"))) {
					continue;
				}
				boolean isLeftHandSummonItem = (tag.getUUID("SummonedUUID").equals(wolfUUID));
				if (isTauntOn) {
					if (currentCharge - COST_PER_TICK < 0.0F || !isLeftHandSummonItem) {
						Taunt.control(summonedTaunt);
						currentCharge += CHARGE_PER_TICK;
						taunt.put(summonedTaunt, currentCharge);
						clientTaunt.put(summonedTaunt.getUUID(), currentCharge);
						continue;
					}
					summonedTaunt.addEffect(new MobEffectInstance(
						MobEffects.GLOWING,
						2,
						0,
						false,
						false
					));
	
					Vec3 center = summonedTaunt.getBoundingBox().getCenter();
					AABB box = new AABB(new Vec3(center.x - RADIUS, -64, center.z - RADIUS), new Vec3(center.x + RADIUS, 320, center.z + RADIUS));
					// Detect for living entities
					List<LivingEntity> entities = summonedTaunt.level().getEntitiesOfClass(
						LivingEntity.class,
						box,
						e -> {
							if (e == summonedTaunt || Invincible.isInvincible(e)) {
								return false;
							}
							CompoundTag tempTag = summonedTaunt.getPersistentData();
							if (tempTag.contains("Owner") && tempTag.getUUID("Owner").equals(e.getUUID())) {
								return false;
							}
							Vec3 targetCenter = e.getBoundingBox().getCenter();
							return ((targetCenter.x - center.x) * (targetCenter.x - center.x) + (targetCenter.z - center.z) * (targetCenter.z - center.z) <= RADIUS * RADIUS);
						}
					);
					List<LivingEntity> livingEntities = new ArrayList<>();
					for (LivingEntity entity: entities) {
						if (taunt.containsKey(entity)) {
							continue;
						}
						livingEntities.add(entity);
						if (tauntedEntity.containsKey(entity)) {
							tauntedEntity.get(entity).add(summonedTaunt);
						} else {
							List<LivingEntity> temp = new ArrayList<>();
							temp.add(summonedTaunt);
							tauntedEntity.put(entity, temp);
						}
					}
					tauntEntity.put(summonedTaunt, livingEntities);
					currentCharge -= COST_PER_TICK;
					if (currentCharge < 0.0F) {
						currentCharge = 0.0F;
					}
				} else {	
					currentCharge += CHARGE_PER_TICK;
					if (currentCharge > 100.0F) {
						currentCharge = 100.0F;
					}
				}
				taunt.put(summonedTaunt, currentCharge);
				clientTaunt.put(summonedTaunt.getUUID(), currentCharge);
			}
			Iterator<Map.Entry<LivingEntity, List<LivingEntity>>> tauntedEntityIterator = tauntedEntity.entrySet().iterator();
			while (tauntedEntityIterator.hasNext()) {
				Map.Entry<LivingEntity, List<LivingEntity>> entry = tauntedEntityIterator.next();
				LivingEntity targetEntity = entry.getKey();
				if (!(targetEntity instanceof Mob mob)) {
					continue;
				}
				Vec3 targetCenter = mob.getBoundingBox().getCenter();
				List<LivingEntity> LivingEntities = entry.getValue();
				double closest = 10000.0D;
				LivingEntity target = null;
				for (LivingEntity entity : LivingEntities) {
					Vec3 center = entity.getBoundingBox().getCenter();
					double current = (targetCenter.x - center.x) * (targetCenter.x - center.x) + (targetCenter.z - center.z) * (targetCenter.z - center.z); 
					if (current < closest) {
						closest = current;
						target = entity;
					}
				}
				mob.setTarget(target);
			}
    }

    public static boolean canAttack(Entity attacker, Entity defender) {
      if (attacker instanceof LivingEntity attackLivingEntity && defender instanceof LivingEntity defendLivingEntity) {
        if (!tauntedEntity.containsKey(attackLivingEntity)) {
					return true;
        }
				return tauntedEntity.get(attackLivingEntity).contains(defendLivingEntity);
      }	
      return true;
    }

    public static float getEnergyFromUUID(UUID summoned) {
    	Float energy = clientTaunt.get(summoned);
    	if (energy == null) {
    		return -100.0F;
    	}
      return energy.floatValue();
    }

    public static boolean getBooleanFromUUID(UUID summoned) {
    	Boolean isTauntOn = clientIsOn.get(summoned);
    	if (isTauntOn == null) {
    		return false;
    	}
    	return isTauntOn.booleanValue();
    }
}
