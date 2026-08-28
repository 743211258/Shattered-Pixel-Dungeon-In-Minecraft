package com.example.spdim.core.functions;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ChunkPos;

import java.util.Set;
import java.util.HashSet;
import java.util.UUID;

public class Functions {
	public static Entity findEntity(MinecraftServer server, UUID uuid) {
		for (ServerLevel level : server.getAllLevels()) {
			Entity entity = level.getEntity(uuid);
			if (entity != null) {
				return entity;
			}
		}
		return null;
	}

	public static LivingEntity findLivingEntity(MinecraftServer server, UUID uuid) {
		Entity entity = findEntity(server, uuid);
		if (entity instanceof LivingEntity livingEntity) {
			return livingEntity;
		}
		return null;
	}

	public static void addSurroundingChunks(ChunkPos center, Set<ChunkPos> chunks) {
		for (int dx = -1; dx <= 1; dx++) {
			for (int dz = -1; dz <= 1; dz++) {
				chunks.add(new ChunkPos(
					center.x + dx,
					center.z + dz
				));
			}
		}
	}

}
