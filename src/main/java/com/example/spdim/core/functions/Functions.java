package com.example.spdim.core.functions;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

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
}
