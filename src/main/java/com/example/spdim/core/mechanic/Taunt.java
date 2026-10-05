package com.example.spdim.core.mechanic;

import com.example.spdim.core.functions.Functions;
import com.example.spdim.core.Macro;
import com.example.spdim.core.MapSavedData;
import com.example.spdim.core.codec.BooleanCodec;
import com.example.spdim.core.codec.FloatCodec;
import com.example.spdim.core.codec.IntegerCodec;
import com.example.spdim.core.codec.UUIDCodec;
import com.example.spdim.core.codec.UUIDListCodec;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
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
	// Put the target to the hashmap.
	public static void taunt(LivingEntity summonedTaunt) {
		if (summonedTaunt == null) {
			return;
		}
		if (!(summonedTaunt.level() instanceof ServerLevel level)) {
			return;
		}
		MinecraftServer server = level.getServer();

		ServerLevel overworld = server.overworld();

		MapSavedData<UUID, Float> tauntData = getTauntSavedData(overworld);
		MapSavedData<UUID, Boolean> isOnData = getIsOnSavedData(overworld);
		MapSavedData<UUID, Float> clientTauntData = getClientTauntSavedData(overworld);
		MapSavedData<UUID, Boolean> clientIsOnData = getClientIsOnSavedData(overworld);

		taunt.put(summonedTaunt.getUUID(), 100.0F);
		isOn.put(summonedTaunt.getUUID(), false);
		clientTaunt.put(summonedTaunt.getUUID(), 100.0F);
		clientIsOn.put(summonedTaunt.getUUID(), false);
		tauntData.setDirty();
		isOnData.setDirty();
		clientTauntData.setDirty();
		clientIsOnData.setDirty();
	}

	public static void control(LivingEntity summonedTaunt) {
		if (summonedTaunt == null) {
			return;
		}
		if (!(summonedTaunt.level() instanceof ServerLevel level)) {
			return;
		}

		MinecraftServer server = level.getServer();

		ServerLevel overworld = server.overworld();
		
		MapSavedData<UUID, Boolean> isOnData = getIsOnSavedData(overworld);
		MapSavedData<UUID, Boolean> clientIsOnData = getClientIsOnSavedData(overworld);

		Boolean current = isOn.get(summonedTaunt.getUUID());
		if (current == null) {
			return;
		}
		boolean next = !current;
		isOn.put(summonedTaunt.getUUID(), next);
		clientIsOn.put(summonedTaunt.getUUID(), next);
		isOnData.setDirty();
		clientIsOnData.setDirty();
	}

	public static MapSavedData<UUID, Float> getClientTauntSavedData(ServerLevel level) {
		return level.getDataStorage().computeIfAbsent(
			tag -> MapSavedData.load(
				tag,
				clientTaunt,
				new UUIDCodec(),
				new FloatCodec()
			),
			() -> new MapSavedData<>(
				clientTaunt,
				new UUIDCodec(),
				new FloatCodec()
			),
			"spdim_client_taunt"
		);
	}

	public static MapSavedData<UUID, Boolean> getClientIsOnSavedData(ServerLevel level) {
		return level.getDataStorage().computeIfAbsent(
			tag -> MapSavedData.load(
				tag,
				clientIsOn,
				new UUIDCodec(),
				new BooleanCodec()
			),
			() -> new MapSavedData<>(
				clientIsOn,
				new UUIDCodec(),
				new BooleanCodec()
			),
			"spdim_client_is_on"
		);
	}

	public static MapSavedData<UUID, Float> getTauntSavedData(ServerLevel level) {
		return level.getDataStorage().computeIfAbsent(
			tag -> MapSavedData.load(
				tag,
				taunt,
				new UUIDCodec(),
				new FloatCodec()
			),
			() -> new MapSavedData<>(
				taunt,
				new UUIDCodec(),
				new FloatCodec()
			),
			"spdim_taunt"
		);
	}

	public static MapSavedData<UUID, Boolean> getIsOnSavedData(ServerLevel level) {
		return level.getDataStorage().computeIfAbsent(
			tag -> MapSavedData.load(
				tag,
				isOn,
				new UUIDCodec(),
				new BooleanCodec()
			),
			() -> new MapSavedData<>(
				isOn,
				new UUIDCodec(),
				new BooleanCodec()
			),
			"spdim_is_on"
		);
	}

	public static MapSavedData<UUID, List<UUID>> getTauntEntitySavedData(ServerLevel level) {
		return level.getDataStorage().computeIfAbsent(
			tag -> MapSavedData.load(
				tag,
				tauntEntity,
				new UUIDCodec(),
				new UUIDListCodec()
			),
			() -> new MapSavedData<>(
				tauntEntity,
				new UUIDCodec(),
				new UUIDListCodec()
			),
			"spdim_taunt_entity"
		);
	}

	public static MapSavedData<UUID, List<UUID>> getTauntedEntitySavedData(ServerLevel level) {
		return level.getDataStorage().computeIfAbsent(
			tag -> MapSavedData.load(
				tag,
				tauntedEntity,
				new UUIDCodec(),
				new UUIDListCodec()
			),
			() -> new MapSavedData<>(
				tauntedEntity,
				new UUIDCodec(),
				new UUIDListCodec()
			),
			"spdim_taunted_entity"
		);
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

			ServerLevel overworld = server.overworld();

			MapSavedData<UUID, Float> tauntData = getTauntSavedData(overworld);
			MapSavedData<UUID, Boolean> isOnData = getIsOnSavedData(overworld);
			MapSavedData<UUID, Float> clientTauntData = getClientTauntSavedData(overworld);
			MapSavedData<UUID, Boolean> clientIsOnData = getClientIsOnSavedData(overworld);

			while (iterator.hasNext()) {
				Map.Entry<UUID, Float> entry = iterator.next();
				UUID summonedTaunt = entry.getKey();
				LivingEntity summonedTauntEntity = Functions.findLivingEntity(server, summonedTaunt);
				if (summonedTauntEntity == null || !summonedTauntEntity.isAlive() || summonedTauntEntity.isRemoved()) {
					iterator.remove();
					isOn.remove(summonedTaunt);
					clientTaunt.remove(summonedTaunt);
					clientIsOn.remove(summonedTaunt);
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
					continue;
				}
				if (!(tag.contains("SummonedUUID"))) {
					continue;
				}
				boolean isLeftHandSummonItem = (tag.getUUID("SummonedUUID").equals(wolfUUID));
				if (isTauntOn) {
					if (currentCharge - Macro.TAUNT_COST_PER_TICK < 0.0F || !isLeftHandSummonItem) {
						Taunt.control(summonedTauntEntity);
						currentCharge += Macro.TAUNT_CHARGE_PER_TICK;
						taunt.put(summonedTaunt, currentCharge);
						clientTaunt.put(summonedTaunt, currentCharge);
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
					AABB box = new AABB(new Vec3(center.x - Macro.TAUNT_EFFECT_RADIUS, -64, center.z - Macro.TAUNT_EFFECT_RADIUS), new Vec3(center.x + Macro.TAUNT_EFFECT_RADIUS, 320, center.z + Macro.TAUNT_EFFECT_RADIUS));
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
							return ((targetCenter.x - center.x) * (targetCenter.x - center.x) + (targetCenter.z - center.z) * (targetCenter.z - center.z) <= Macro.TAUNT_EFFECT_RADIUS * Macro.TAUNT_EFFECT_RADIUS);
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
					currentCharge -= Macro.TAUNT_COST_PER_TICK;
					if (currentCharge < 0.0F) {
						currentCharge = 0.0F;
					}
				} else {	
					currentCharge += Macro.TAUNT_CHARGE_PER_TICK;
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
			tauntData.setDirty();
			isOnData.setDirty();
			clientTauntData.setDirty();
			clientIsOnData.setDirty();
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
