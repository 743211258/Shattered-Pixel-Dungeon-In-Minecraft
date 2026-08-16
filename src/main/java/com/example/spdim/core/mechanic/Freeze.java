package com.example.spdim.core.mechanic;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import com.example.spdim.core.data_structure.PosAndDirection;
import com.example.spdim.core.registry.ModEffects;

import java.util.HashMap;
import java.util.Map;

/*
When the freeze buff is applied to an living entity, the view and movement of that living entity is locked and reset to the original position for every tick.
If the living entity is also a mob, all AI behavior is further disabled.
*/

public class Freeze extends MobEffect {
	public static final Map<LivingEntity, PosAndDirection> LIVING_ENTITY_POSITION = new HashMap<>();

	public Freeze() {
		super(MobEffectCategory.HARMFUL, 0);
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int amplifier) {

		if (livingEntity instanceof Mob mob) {
			if (!LIVING_ENTITY_POSITION.containsKey(mob)) {
				PosAndDirection pd = new PosAndDirection();
				pd.pos = mob.position();
				pd.yRot = mob.getYRot();
				pd.xRot = mob.getXRot();
				pd.yHeadRot = mob.yHeadRot;
				pd.yBodyRot = mob.yBodyRot;
				LIVING_ENTITY_POSITION.put(mob, pd);
			}

			PosAndDirection pd = LIVING_ENTITY_POSITION.get(mob);
			// Reset the position.
			mob.setPos(pd.pos.x, pd.pos.y, pd.pos.z);

			// Turn off AI
			mob.setNoAi(true);

			// Reset the speed.
			mob.setDeltaMovement(Vec3.ZERO);

			// Stop auto navigation.
			mob.getNavigation().stop();

			// Reset the direction.
			mob.setYRot(pd.yRot);
			mob.setXRot(pd.xRot);
			mob.yHeadRot = pd.yHeadRot;
			mob.yBodyRot = pd.yBodyRot;
			// Update them to the client side.
			mob.hurtMarked = true;

		} else if (livingEntity instanceof ServerPlayer serverPlayer) {
			if (!LIVING_ENTITY_POSITION.containsKey(serverPlayer)) {
			PosAndDirection pd = new PosAndDirection();
				pd.pos = serverPlayer.position();
				pd.yRot = serverPlayer.getYRot();
				pd.xRot = serverPlayer.getXRot();
				pd.yHeadRot = serverPlayer.yHeadRot;
				pd.yBodyRot = serverPlayer.yBodyRot;
				LIVING_ENTITY_POSITION.put(serverPlayer, pd);
			}

			PosAndDirection pd = LIVING_ENTITY_POSITION.get(serverPlayer);
			// Reset the position.
			serverPlayer.setPos(pd.pos.x, pd.pos.y, pd.pos.z);

			// Reset the speed.
			serverPlayer.setDeltaMovement(Vec3.ZERO);

			// Reset the direction
			serverPlayer.setYRot(pd.yRot);
			serverPlayer.setXRot(pd.xRot);
			serverPlayer.yHeadRot = pd.yHeadRot;
			serverPlayer.yBodyRot = pd.yBodyRot;

			// Send the direction and location package to all player.
			serverPlayer.connection.teleport(
				pd.pos.x, pd.pos.y, pd.pos.z,
				pd.yRot, pd.xRot
			);
		}
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}

	@Override
	public void removeAttributeModifiers(LivingEntity livingEntity, AttributeMap attributes, int amplifier) {
		super.removeAttributeModifiers(livingEntity, attributes, amplifier);

		if (livingEntity instanceof Mob mob) {
			mob.setNoAi(false);
			mob.getNavigation().recomputePath();
			mob.setDeltaMovement(mob.getDeltaMovement());
			mob.hurtMarked = true;
		}	
		LIVING_ENTITY_POSITION.remove(livingEntity);
	}

	public static boolean isFrozen(LivingEntity entity) {
		return entity.hasEffect(ModEffects.FREEZE.get());
	}
}
