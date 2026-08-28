package com.example.spdim.core.mechanic;

import com.example.spdim.core.registry.ModEffects;
import com.example.spdim.core.functions.Functions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import java.util.function.Consumer;
import java.util.Objects;

import net.minecraft.nbt.CompoundTag;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;


public final class Summon {

	private static Map<Entity, Player> summonedEntity = new HashMap<>();
	private static Map<Entity, ChunkPos> forceLoadChunksCenter = new HashMap<>();

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
		ChunkPos chunkPos = entity.chunkPosition();
		Set<ChunkPos> chunks = new HashSet<>();
		Functions.addSurroundingChunks(chunkPos, chunks);
		for (ChunkPos chunk : chunks) {
			level.setChunkForced(chunk.x, chunk.z, true);
		}
		summonedEntity.put(entity, player);
		forceLoadChunksCenter.put(entity, chunkPos);
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
					livingEntity.addEffect(new MobEffectInstance(ModEffects.REGEN_DISABLED.get(), 2100000));
				}
			} else {
				if (summoned instanceof LivingEntity livingEntity) {
					livingEntity.removeEffect(ModEffects.REGEN_DISABLED.get());
				}
      }
		}
		Map<ServerLevel, Set<ChunkPos>> oldChunksByLevel = new HashMap<>();
		Map<ServerLevel, Set<ChunkPos>> newChunksByLevel = new HashMap<>();
		Iterator<Map.Entry<Entity, ChunkPos>> forceLoadChunksCenterIterator = forceLoadChunksCenter.entrySet().iterator();
		while (forceLoadChunksCenterIterator.hasNext()) {
			Map.Entry<Entity, ChunkPos> entry = forceLoadChunksCenterIterator.next();
			Entity summoned = entry.getKey();
			ChunkPos oldCenter = entry.getValue();
			if (summoned == null || !summoned.isAlive() || summoned.isRemoved()) {
				forceLoadChunksCenterIterator.remove();
				continue;
			}   			
			if (!(summoned.level() instanceof ServerLevel level)) {
				continue;
			}
			Functions.addSurroundingChunks(oldCenter, oldChunksByLevel.computeIfAbsent(level, k -> new HashSet<>()));
			Functions.addSurroundingChunks(summoned.chunkPosition(), newChunksByLevel.computeIfAbsent(level, k -> new HashSet<>()));
		}

		for (ServerLevel level : oldChunksByLevel.keySet()) {
			Set<ChunkPos> oldChunks = oldChunksByLevel.get(level);
			Set<ChunkPos> newChunks = newChunksByLevel.getOrDefault(level, Set.of());

			Set<ChunkPos> difference = new HashSet<>(oldChunks);
			difference.removeAll(newChunks);
			for (ChunkPos chunk : difference) {
				level.setChunkForced(chunk.x, chunk.z, false);
			}
		}

		for (ServerLevel level : newChunksByLevel.keySet()) {
			Set<ChunkPos> newChunks = newChunksByLevel.get(level);
			Set<ChunkPos> oldChunks = oldChunksByLevel.getOrDefault(level, Set.of());

			Set<ChunkPos> difference = new HashSet<>(newChunks);
			difference.removeAll(oldChunks);
			for (ChunkPos chunk : difference) {
				level.setChunkForced(chunk.x, chunk.z, true);
			}
		}
	}
	public static boolean isSummoned(Entity entity) {
		return summonedEntity.containsKey(entity); 
	}
}
