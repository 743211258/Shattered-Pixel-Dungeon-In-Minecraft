package com.example.spdim.core.mechanic;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import com.example.spdim.core.data_structure.ViscosityRender;
import com.example.spdim.core.data_structure.ViscosityTotalDamageRender;
import com.example.spdim.core.mechanic.MixinReference;
import com.example.spdim.core.enchantment.Viscosity;
import com.example.spdim.core.registry.ModDamageSources;
import com.example.spdim.core.registry.ModEffects;

public class ViscosityEffect extends MobEffect {
	public ViscosityEffect() {
		super(MobEffectCategory.BENEFICIAL, 0x9D00FF);
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		if (entity.level().isClientSide()) {
			return;
		}
		CompoundTag data = entity.getPersistentData();

		if (!data.contains("ViscosityTick")) {
    	data.putInt("ViscosityTick", 0);
		}

		int tick = data.getInt("ViscosityTick");
		tick++;

		float totalDamage = data.getFloat("totalDamage");
    float nextDamage = totalDamage / 10.0F;
		if (tick >= 60) {
			tick = 0;
			if (nextDamage < 1.0F) {
				if (totalDamage >= 1.0F) {
					nextDamage = 1.0F;
				} else {
					nextDamage = totalDamage;
				}
			}
			entity.hurt(ModDamageSources.viscosity(entity.level()), nextDamage);
			if (!entity.isAlive()) {
				MixinReference.renderReference.remove(entity.getUUID());
				return;
			}
			totalDamage = totalDamage - nextDamage;
			data.putFloat("totalDamage", totalDamage);
		}
		data.putInt("ViscosityTick", tick);
		nextDamage = totalDamage / 10.0F;
		if (nextDamage < 1.0F) {
			if (totalDamage >= 1.0F) {
				nextDamage = 1.0F;
			} else {
				nextDamage = totalDamage;
			}
		}
		if (nextDamage == 0.0F) {
			entity.removeEffect(ModEffects.VISCOSITY_EFFECT.get());
			MixinReference.renderReference.remove(entity.getUUID());
			data.remove("totalDamage");
			data.remove("ViscosityTick");
			return;	
		}
		float absorption = entity.getAbsorptionAmount();
		float health = entity.getHealth();
		float maxHealth = entity.getMaxHealth();
		float remainingDamage = nextDamage - absorption;
		remainingDamage = nextDamage - absorption;
		if (remainingDamage <= 0.0F) {
			float absorptionMin = Mth.ceil(Mth.ceil(maxHealth / 2.0F) + (absorption - nextDamage) / 2.0F);
			float absorptionMax = Mth.ceil(Mth.ceil(maxHealth / 2.0F) + absorption / 2.0F);
			float healthMin = Float.MAX_VALUE;
			float healthMax = Float.MIN_VALUE;
			ViscosityRender render = new ViscosityRender(healthMin, healthMax, absorptionMin, absorptionMax);
			MixinReference.renderReference.put(entity.getUUID(), render);
		} else {
			float absorptionMin = Mth.ceil(maxHealth / 2.0F) + 1.0F;
			float absorptionMax = Mth.ceil(Mth.ceil(maxHealth / 2.0F) + absorption / 2.0F);
			float healthMin = Mth.ceil((health - remainingDamage) / 2.0F);
			float healthMax = Mth.ceil(health / 2.0F);
			ViscosityRender render = new ViscosityRender(healthMin, healthMax, absorptionMin, absorptionMax);
			MixinReference.renderReference.put(entity.getUUID(), render);
		}
		remainingDamage = totalDamage - absorption;
		if (remainingDamage <= 0.0F) {
			float absorptionMin = Mth.ceil(Mth.ceil(maxHealth / 2.0F) + (absorption - totalDamage) / 2.0F);
			float absorptionMax = Mth.ceil(Mth.ceil(maxHealth / 2.0F) + absorption / 2.0F);
			float healthMin = Float.MAX_VALUE;
			float healthMax = Float.MIN_VALUE;
			ViscosityTotalDamageRender render = new ViscosityTotalDamageRender(healthMin, healthMax, absorptionMin, absorptionMax);
			MixinReference.totalDamageRenderReference.put(entity.getUUID(), render);
		} else {
			float absorptionMin = Mth.ceil(maxHealth / 2.0F) + 1.0F;
			float absorptionMax = Mth.ceil(Mth.ceil(maxHealth / 2.0F) + absorption / 2.0F);
			float healthMin = Mth.ceil((health - remainingDamage) / 2.0F);
			float healthMax = Mth.ceil(health / 2.0F);
			ViscosityTotalDamageRender render = new ViscosityTotalDamageRender(healthMin, healthMax, absorptionMin, absorptionMax);
			MixinReference.totalDamageRenderReference.put(entity.getUUID(), render);
		}

	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}

}
