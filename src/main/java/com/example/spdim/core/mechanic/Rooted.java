package com.example.spdim.core.mechanic;

import com.example.spdim.core.wand.energyWand.WandOfRegrowth;
import com.example.spdim.core.codec.UUIDCodec;
import com.example.spdim.core.codec.Vec3Codec;
import com.example.spdim.core.MapSavedData;
import com.example.spdim.core.registry.ModEffects;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Rooted extends MobEffect {
	// Hashmap to keep track of the location of them.
	public static final Map<UUID, Vec3> LOCKED = new HashMap<>();

	public Rooted() {
		super(MobEffectCategory.HARMFUL, 0);
	}

	public static MapSavedData<UUID, Vec3> getSavedData(ServerLevel level) {
		return level.getDataStorage().computeIfAbsent(
			tag -> MapSavedData.load(
				tag,
				LOCKED,
				new UUIDCodec(),
				new Vec3Codec()
			),
			() -> new MapSavedData<>(
				LOCKED,
				new UUIDCodec(),
				new Vec3Codec()
			),
			"spdim_rooted"
		);
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
		// Lock their location.
		UUID uuid = livingEntity.getUUID();

		if (!(livingEntity.level() instanceof ServerLevel level)) {
			return;
		}

		MinecraftServer server = level.getServer();

		ServerLevel overworld = server.overworld();

		if (livingEntity instanceof Mob mob) {
			if (!LOCKED.containsKey(uuid)) {
				LOCKED.put(uuid, mob.position());
				MapSavedData<UUID, Vec3> data = getSavedData(overworld);
				data.setDirty();
			}
			Vec3 lp = LOCKED.get(uuid);
			mob.setPos(lp.x, lp.y, lp.z);
			// Set the speed to zero.
			mob.setDeltaMovement(Vec3.ZERO);
			// Update the position and speed to the client side.
			mob.hurtMarked = true;
		} else if (livingEntity instanceof Player player) {
			if (!LOCKED.containsKey(uuid)) {
				LOCKED.put(uuid, player.position());
				MapSavedData<UUID, Vec3> data = getSavedData(overworld);
				data.setDirty();
			}
			Vec3 lp = LOCKED.get(uuid);
			player.setPos(lp.x, lp.y, lp.z);
			player.setDeltaMovement(Vec3.ZERO);
			player.hurtMarked = true;
		}
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}

	@Override
	public void removeAttributeModifiers(LivingEntity livingEntity, AttributeMap attributes, int amplifier) {
		super.removeAttributeModifiers(livingEntity, attributes, amplifier);

		if (!(livingEntity.level() instanceof ServerLevel level)) {
			return;
		}

		MinecraftServer server = level.getServer();

		ServerLevel overworld = server.overworld();

		MapSavedData<UUID, Vec3> data = getSavedData(overworld);
		delete(livingEntity);
		if (livingEntity instanceof Mob mob) {
			mob.setDeltaMovement(mob.getDeltaMovement());
			mob.hurtMarked = true;
		}	
		LOCKED.remove(livingEntity.getUUID());
		data.setDirty();
	}

	private static void delete(LivingEntity entity) {
		// Remove all blocks that surround the player.
		if (WandOfRegrowth.BLOCKS.containsKey(entity)) {
			for (BlockPos pos : WandOfRegrowth.BLOCKS.get(entity)) {
				entity.level().destroyBlock(pos, false);
			}
		}
	}
	public static boolean isRooted(LivingEntity entity) {
		return entity.hasEffect(ModEffects.ROOTED.get());
	}
}
