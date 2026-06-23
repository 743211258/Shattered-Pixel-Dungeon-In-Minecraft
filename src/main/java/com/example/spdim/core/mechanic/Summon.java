package com.example.spdim.core.mechanic;

import com.example.spdim.core.mechanic.RegenerationDisabled;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import java.util.function.Consumer;
import java.util.Objects;

import net.minecraft.nbt.CompoundTag;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class Summon {

	private static Map<Entity, Player> summonedEntity = new HashMap<>();

	private Summon() {};
	
	public static Entity summon(ServerLevel level, EntityType<?> type, Player player, Consumer<Entity> init) {

		Objects.requireNonNull(level, "mechanic-Summon-level");
		Objects.requireNonNull(type, "mechanic-Summon-type");
		Objects.requireNonNull(player, "mechanic-Summon-player");

		Entity entity = type.create(level);
		if (entity == null) {
			return null;
		}
		Vec3 position = player.position();
		entity.moveTo(position.x, position.y, position.z);
		if (init != null) {
			init.accept(entity);
		}
		level.addFreshEntity(entity);
		summonedEntity.put(entity, player);
		CompoundTag tag = entity.getPersistentData();
		tag.putUUID("Owner", player.getUUID());
		return entity;
	}

	public static void tick() {
		Iterator<Map.Entry<Entity, Player>> iterator = summonedEntity.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Entity, Player> entry = iterator.next();
			Entity summoned = entry.getKey();
			Player summoner = entry.getValue();
			
			if (summoned == null || !summoned.isAlive() || summoned.isRemoved()) {
				System.out.println("Triggers\n");	
				iterator.remove();
				continue;
			}
			CompoundTag tag = summoner.getOffhandItem().getTag();
			if ((summoner.getOffhandItem().isEmpty()) || (tag == null) || (!(tag.contains("SummonedUUID"))) || (!tag.getUUID("SummonedUUID").equals(summoned.getUUID()))) {
				summoned.hurt(summoned.damageSources().fellOutOfWorld(), 0.5f);
				if(summoned instanceof LivingEntity livingEntity) {
					RegenerationDisabled.disable(livingEntity, 2100000);
				}
			} else {
				if (summoned instanceof LivingEntity livingEntity) {
					RegenerationDisabled.disableRemove(livingEntity);
				}
      }
		}
	}

}
