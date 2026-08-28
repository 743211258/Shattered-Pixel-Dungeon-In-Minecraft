package com.example.spdim.core.mechanic;

import com.example.spdim.core.functions.Functions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
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
    private static Map<UUID, Float> taunt = new HashMap<>();
    private static Map<UUID, Boolean> isOn = new HashMap<>();
		private static Map<UUID, List<UUID>> tauntEntity = new HashMap<>();
		private static Map<UUID, List<UUID>> tauntedEntity = new HashMap<>();
		private static final float RADIUS = 16.0F;
		private static final float COST_PER_TICK = 0.1F;
		private static final float CHARGE_PER_TICK = 0.025F;
    // Put the target to the hashmap.
    public static void taunt(LivingEntity summonedTaunt) {
        if (summonedTaunt == null) {
            return;
        }
        taunt.put(summonedTaunt.getUUID(), 100.0F);
				isOn.put(summonedTaunt.getUUID(), false);
				clientTaunt.put(summonedTaunt.getUUID(), 100.0F);
				clientIsOn.put(summonedTaunt.getUUID(), false);
    }

		public static void control(LivingEntity summonedTaunt) {
			Boolean current = isOn.get(summonedTaunt.getUUID());
			if (current == null) {
				return;
			}
			boolean next = !current;
			isOn.put(summonedTaunt.getUUID(), next);
			clientIsOn.put(summonedTaunt.getUUID(), next);
		}

    public static void tick(MinecraftServer server) {
			Iterator<Map.Entry<UUID, Float>> iterator = taunt.entrySet().iterator();
			/*Iterator<Map.Entry<UUID, List<UUID>>> tempIterator = tauntedEntity.entrySet().iterator();
			while (tempIterator.hasNext()) {
				Map.Entry<UUID, List<UUID>> entry = tempIterator.next();
				UUID livingEntity = entry.getKey();
				if (livingEntity instanceof Mob mob) {
					mob.setTarget(null);
				}
			}*/
			tauntEntity.clear();
			tauntedEntity.clear();

			while (iterator.hasNext()) {
				Map.Entry<UUID, Float> entry = iterator.next();
				UUID summonedTaunt = entry.getKey();
				LivingEntity summonedTauntEntity = Functions.findLivingEntity(server, summonedTaunt);
				if (summonedTauntEntity == null || !summonedTauntEntity.isAlive() || summonedTauntEntity.isRemoved()) {
					iterator.remove();
					isOn.remove(summonedTaunt);
    			clientTaunt.remove(summonedTaunt);
    			clientIsOn.remove(summonedTaunt);
					System.out.println("First");
					continue;
				}
				Float charge = taunt.get(summonedTaunt);
				if (charge == null) {
					System.out.println("Second");
					continue;
				}
				float currentCharge = charge.floatValue();
				Boolean current = isOn.get(summonedTaunt);
				if (current == null) {
					System.out.println("Third");
					continue;
				}
				boolean isTauntOn = current.booleanValue();
				CompoundTag tag = null;
				UUID wolfUUID = null;
				if (summonedTauntEntity instanceof Wolf wolf) {
					UUID ownerUUID = wolf.getOwnerUUID();
					wolfUUID = wolf.getUUID();
					LivingEntity owner = Functions.findLivingEntity(server, ownerUUID);
					if (owner instanceof ServerPlayer serverPlayer) {
						ItemStack temp = serverPlayer.getOffhandItem();
						tag = temp.getTag();
					}
				}
				if (tag == null) {
					System.out.println("Fourth");
					continue;
				}
				if (!(tag.contains("SummonedUUID"))) {
					System.out.println("Fifth");
					continue;
				}
				boolean isLeftHandSummonItem = (tag.getUUID("SummonedUUID").equals(wolfUUID));
				if (isTauntOn) {
					if (currentCharge - COST_PER_TICK < 0.0F || !isLeftHandSummonItem) {
						Taunt.control(summonedTauntEntity);
						currentCharge += CHARGE_PER_TICK;
						taunt.put(summonedTaunt, currentCharge);
						clientTaunt.put(summonedTaunt, currentCharge);
						System.out.println("Sixth");
						continue;
					}
					summonedTauntEntity.addEffect(new MobEffectInstance(
						MobEffects.GLOWING,
						2,
						0,
						false,
						false
					));
	
					Vec3 center = summonedTauntEntity.getBoundingBox().getCenter();
					AABB box = new AABB(new Vec3(center.x - RADIUS, -64, center.z - RADIUS), new Vec3(center.x + RADIUS, 320, center.z + RADIUS));
					// Detect for living entities
					List<LivingEntity> entities = summonedTauntEntity.level().getEntitiesOfClass(
						LivingEntity.class,
						box,
						e -> {
							if (e == summonedTauntEntity || Invincible.isInvincible(e)) {
								return false;
							}
							CompoundTag tempTag = summonedTauntEntity.getPersistentData();
							if (tempTag.contains("Owner") && tempTag.getUUID("Owner").equals(e.getUUID())) {
								return false;
							}
							Vec3 targetCenter = e.getBoundingBox().getCenter();
							return ((targetCenter.x - center.x) * (targetCenter.x - center.x) + (targetCenter.z - center.z) * (targetCenter.z - center.z) <= RADIUS * RADIUS);
						}
					);
					List<UUID> livingEntities = new ArrayList<>();
					for (LivingEntity entity: entities) {
						if (taunt.containsKey(entity.getUUID())) {
							continue;
						}
						livingEntities.add(entity.getUUID());
						if (tauntedEntity.containsKey(entity.getUUID())) {
							tauntedEntity.get(entity.getUUID()).add(summonedTaunt);
						} else {
							List<UUID> temp = new ArrayList<>();
							temp.add(summonedTaunt);
							tauntedEntity.put(entity.getUUID(), temp);
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
				clientTaunt.put(summonedTaunt, currentCharge);
			}
			Iterator<Map.Entry<UUID, List<UUID>>> tauntedEntityIterator = tauntedEntity.entrySet().iterator();
			while (tauntedEntityIterator.hasNext()) {
				Map.Entry<UUID, List<UUID>> entry = tauntedEntityIterator.next();
				UUID targetEntity = entry.getKey();
				LivingEntity targetEntityLiving = Functions.findLivingEntity(server, targetEntity);
				if (!(targetEntityLiving instanceof Mob mob)) {
					continue;
				}
				Vec3 targetCenter = mob.getBoundingBox().getCenter();
				List<UUID> LivingEntities = entry.getValue();
				double closest = 10000.0D;
				LivingEntity target = null;
				for (UUID entity : LivingEntities) {
					LivingEntity entityLiving = Functions.findLivingEntity(server, entity);
					if (entityLiving == null) {
						continue;
					}
					Vec3 center = entityLiving.getBoundingBox().getCenter();
					double current = (targetCenter.x - center.x) * (targetCenter.x - center.x) + (targetCenter.z - center.z) * (targetCenter.z - center.z); 
					if (current < closest) {
						closest = current;
						target = entityLiving;
					}
				}
				mob.setTarget(target);
			}
    }

    public static boolean canAttack(Entity attacker, Entity defender) {
      if (attacker instanceof LivingEntity attackLivingEntity && defender instanceof LivingEntity defendLivingEntity) {
        if (!tauntedEntity.containsKey(attackLivingEntity.getUUID())) {
					return true;
        }
				return tauntedEntity.get(attackLivingEntity.getUUID()).contains(defendLivingEntity.getUUID());
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
