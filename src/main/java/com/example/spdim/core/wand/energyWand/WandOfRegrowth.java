package com.example.spdim.core.wand.energyWand;

import com.example.spdim.SPDIM;
import com.example.spdim.core.MapSavedData;
import com.example.spdim.core.mechanic.Invincible;
import com.example.spdim.core.mechanic.Rooted;
import com.example.spdim.core.mechanic.CooldownSystem;
import com.example.spdim.core.Macro;
import com.example.spdim.core.wand.EnergyWand;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import com.example.spdim.core.registry.ModEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;


public class WandOfRegrowth extends EnergyWand {

	public static final Map<LivingEntity, Set<BlockPos>> BLOCKS = new HashMap<>();

	public WandOfRegrowth(Properties properties, int maxEnergy, int energyCost, int cooldown, Component name) {
		super(properties, maxEnergy, energyCost, cooldown, name);
	}

	@Override
	protected void cast(ServerLevel world, Player player, ItemStack stack) {
		if (!CooldownSystem.hasPositiveEnergy(stack)) {
			return;
		}
		Vec3 origin = player.getEyePosition(1.0F);

		// Build a Lookat matrix
		Vec3 forward = player.getLookAngle().normalize();
		Vec3 worldUp = Math.abs(forward.y) > 0.99
				? new Vec3(1, 0, 0)
				: new Vec3(0, 1, 0);
		Vec3 right = forward.cross(worldUp).normalize();
		Vec3 up = right.cross(forward).normalize();

		Vec3 pointOne = origin.add(forward.scale(Macro.WAND_OF_REGROWTH_CONE_RANGE_HEIGHT)).add(right.scale(Macro.WAND_OF_REGROWTH_CONE_RANGE_RADIUS));
		Vec3 pointTwo = origin.add(forward.scale(Macro.WAND_OF_REGROWTH_CONE_RANGE_HEIGHT)).subtract(right.scale(Macro.WAND_OF_REGROWTH_CONE_RANGE_RADIUS));
		Vec3 pointThree = origin.add(forward.scale(Macro.WAND_OF_REGROWTH_CONE_RANGE_HEIGHT)).add(up.scale(Macro.WAND_OF_REGROWTH_CONE_RANGE_RADIUS));
		Vec3 pointFour = origin.add(forward.scale(Macro.WAND_OF_REGROWTH_CONE_RANGE_HEIGHT)).subtract(up.scale(Macro.WAND_OF_REGROWTH_CONE_RANGE_RADIUS));

		double minX = Math.min(Math.min(Math.min(Math.min(origin.x, pointOne.x), pointTwo.x), pointThree.x), pointFour.x);
		double minY = Math.min(Math.min(Math.min(Math.min(origin.y, pointOne.y), pointTwo.y), pointThree.y), pointFour.y);
		double minZ = Math.min(Math.min(Math.min(Math.min(origin.z, pointOne.z), pointTwo.z), pointThree.z), pointFour.z);

		double maxX = Math.max(Math.max(Math.max(Math.max(origin.x, pointOne.x), pointTwo.x), pointThree.x), pointFour.x);
		double maxY = Math.max(Math.max(Math.max(Math.max(origin.y, pointOne.y), pointTwo.y), pointThree.y), pointFour.y);
		double maxZ = Math.max(Math.max(Math.max(Math.max(origin.z, pointOne.z), pointTwo.z), pointThree.z), pointFour.z);

		AABB collision_box = new AABB(minX, minY, minZ, maxX, maxY, maxZ);

		// Detect for living entities
		List<LivingEntity> entities = world.getEntitiesOfClass(
				LivingEntity.class,
				collision_box,
				e -> {
					if (e == player ||
							Invincible.isInvincible(e)) {
						return false;
					}
					Vec3 vectorFromOriginToEntity = e.position().subtract(origin);
					double projectionLength = vectorFromOriginToEntity.dot(forward);
					if (projectionLength <= 0 || projectionLength > Macro.WAND_OF_REGROWTH_CONE_RANGE_HEIGHT) {
						return false;
					}
					double verticalLengthFromEntityToProjectionSqr = vectorFromOriginToEntity.lengthSqr() - (projectionLength * projectionLength);
					double radiusAtSamePlane = projectionLength / Macro.WAND_OF_REGROWTH_CONE_RANGE_HEIGHT * Macro.WAND_OF_REGROWTH_CONE_RANGE_RADIUS;;
					return (verticalLengthFromEntityToProjectionSqr <= radiusAtSamePlane * radiusAtSamePlane);
				}
		);

		for (LivingEntity entity : entities) {
			entity.addEffect(new MobEffectInstance(ModEffects.ROOTED.get(), 60));
			AABB box = entity.getBoundingBox();
			int blockMinX = Mth.floor(box.minX);
			int blockMinY = Mth.floor(box.minY);
			int blockMinZ = Mth.floor(box.minZ);
			int blockMaxX = Mth.floor(box.maxX);
			int blockMaxY = Mth.floor(box.maxY);
			int blockMaxZ = Mth.floor(box.maxZ);
			for (int x = blockMinX; x <= blockMaxX; x++) {
				for (int y = blockMinY; y <= blockMaxY; y++) {
					for (int z = blockMinZ; z <= blockMaxZ; z++) {

						Set<BlockPos> set = WandOfRegrowth.BLOCKS.computeIfAbsent(
								entity, k -> new HashSet<>()
						);

						BlockPos woodPos = new BlockPos(x, y, z);


						world.destroyBlock(woodPos, false);
						world.setBlock(woodPos, Blocks.OAK_WOOD.defaultBlockState(), 11);

						set.add(woodPos.immutable());
					}
				}
			}
			Rooted.LOCKED.put(entity.getUUID(), entity.position());
		}
		MinecraftServer server = world.getServer();

		ServerLevel overworld = server.overworld();

		MapSavedData<UUID, Vec3> data = Rooted.getSavedData(overworld);
		data.setDirty();

		for (double d = 0; d <= Macro.WAND_OF_REGROWTH_CONE_RANGE_HEIGHT; d += Macro.WAND_OF_REGROWTH_CONE_RANGE_STEP_HEIGHT) {
			double radius = d / Macro.WAND_OF_REGROWTH_CONE_RANGE_HEIGHT * Macro.WAND_OF_REGROWTH_CONE_RANGE_RADIUS;
			double radiusSqr = radius * radius;
			for (double x = -radius; x <= radius; x += Macro.WAND_OF_REGROWTH_CONE_RANGE_STEP_RADIUS) {
				for (double y = -radius; y <= radius; y += Macro.WAND_OF_REGROWTH_CONE_RANGE_STEP_RADIUS) {

					if (x * x + y * y > radiusSqr) {
						continue;
					}

					Vec3 pos = origin.add(forward.scale(d)).add(right.scale(x)).add(up.scale(y));

					// Spawn particles
					spawnBlockParticles(world, pos);
				}
			}
		}
		CooldownSystem.consumeAnyEnergy(stack, 1, world);
	}

	private void spawnBlockParticles(Level world, Vec3 pos) {
		if (!(world instanceof ServerLevel server)) return;

		// generate a random number
		double r = Math.random();

		// 50% to be grass particles
		if (r < 0.5) {
			server.sendParticles(
					new BlockParticleOption(ParticleTypes.BLOCK, Blocks.GRASS.defaultBlockState()),
					pos.x, pos.y, pos.z,
					2,
					0.12, 0.12, 0.12,
					0.02
			);
		// 30% to be leaf particles
		} else if (r < 0.8) {
			server.sendParticles(
					new BlockParticleOption(ParticleTypes.BLOCK, Blocks.OAK_LEAVES.defaultBlockState()),
					pos.x, pos.y, pos.z,
					2,
					0.12, 0.12, 0.12,
					0.02
			);
		// 20% to be log particles
		} else {
			server.sendParticles(
					new BlockParticleOption(ParticleTypes.BLOCK, Blocks.OAK_LOG.defaultBlockState()),
					pos.x, pos.y, pos.z,
					1,
					0.12, 0.12, 0.12,
					0.02
			);
		}
	}
}
