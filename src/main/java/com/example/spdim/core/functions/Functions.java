package com.example.spdim.core.functions;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;

import com.example.spdim.core.Macro;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Random;
import java.util.HashSet;
import java.util.UUID;

public class Functions {
	private static final Random RANDOM = new Random();
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

	public static List<Vec3> generateConePositions(Vec3 origin, Vec3 forward) {
		List<Vec3> pos_list = new ArrayList<>();

		forward = forward.normalize();

		Vec3 worldUp = Math.abs(forward.y) > 0.99
				? new Vec3(1, 0, 0)
				: new Vec3(0, 1, 0);

		Vec3 right = forward.cross(worldUp).normalize();
		Vec3 up = right.cross(forward).normalize();

		for (double d = 0;
			 d <= Macro.WAND_OF_REGROWTH_CONE_RANGE_HEIGHT;
			 d += Macro.WAND_OF_REGROWTH_CONE_RANGE_STEP_HEIGHT) {

			double radius = d
					/ Macro.WAND_OF_REGROWTH_CONE_RANGE_HEIGHT
					* Macro.WAND_OF_REGROWTH_CONE_RANGE_RADIUS;

			double radiusSqr = radius * radius;

			for (double x = -radius;
				 x <= radius;
				 x += Macro.WAND_OF_REGROWTH_CONE_RANGE_STEP_RADIUS) {

				for (double y = -radius;
					 y <= radius;
					 y += Macro.WAND_OF_REGROWTH_CONE_RANGE_STEP_RADIUS) {

					if (x * x + y * y > radiusSqr) {
						continue;
					}

					Vec3 pos = origin
							.add(forward.scale(d))
							.add(right.scale(x))
							.add(up.scale(y));

					pos_list.add(pos);
				}
			}
		}

		return pos_list;
	}

	public static List<float[]> generateRandomColors(
			int count,
			float[][] colors,
			float[] weights
	) {
		List<float[]> color_list = new ArrayList<>();

		float totalWeight = 0.0F;

		for (float weight : weights) {
			totalWeight += weight;
		}

		for (int i = 0; i < count; i++) {
			float randomWeight = RANDOM.nextFloat() * totalWeight;
			int colorIndex = 0;

			for (; colorIndex < weights.length - 1; colorIndex++) {
				randomWeight -= weights[colorIndex];

				if (randomWeight < 0.0F) {
					break;
				}
			}

			color_list.add(colors[colorIndex]);
		}

		return color_list;
	}
}
