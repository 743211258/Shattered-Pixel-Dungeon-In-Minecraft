package com.example.spdim.core.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.tags.DamageTypeTags;

import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.example.spdim.core.data_structure.ViscosityRender;
import com.example.spdim.core.mechanic.MixinReference;
import com.example.spdim.core.enchantment.Viscosity;
import com.example.spdim.core.mechanic.ViscosityEffect;
import com.example.spdim.core.registry.ModDamageSources;
import com.example.spdim.ExampleMod;

@Mod.EventBusSubscriber(modid = "spdim", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ViscosityServerEvents {

	public static float absorptionBeforeDamage;

	@SubscribeEvent
	public static void onHurt(LivingHurtEvent event) {
		LivingEntity livingEntity = event.getEntity();
		absorptionBeforeDamage = livingEntity.getAbsorptionAmount();
	}

	@SubscribeEvent
	public static void onDamage(LivingDamageEvent event) {
		LivingEntity livingEntity = event.getEntity();
		ItemStack chestplate = livingEntity.getItemBySlot(EquipmentSlot.CHEST);
		int level = chestplate.getEnchantmentLevel(ExampleMod.VISCOSITY.get());
		System.out.println(level);
		if (level <= 0) {
			return;
		}
		DamageSource source = ModDamageSources.viscosity(livingEntity.level());

		System.out.println(
				source.is(DamageTypeTags.BYPASSES_ARMOR)
		);

		System.out.println(
				source.is(DamageTypeTags.BYPASSES_EFFECTS)
		);

		System.out.println(
				source.is(DamageTypeTags.BYPASSES_ENCHANTMENTS)
		);
		if (event.getSource().is(ModDamageSources.DAMAGE_OVER_TIME)) {
			return;
		}	
		float absorption = livingEntity.getAbsorptionAmount();
		float damage = event.getAmount() + (absorptionBeforeDamage - absorption);
		System.out.println("damage: " + damage);
		float viscosityDamage = damage; //* ((float) (level + 1) / (float) (level + 6));
		float damageToLivingEntity = damage - viscosityDamage;
		System.out.println("damageToLivingEntity: " + damageToLivingEntity);
		event.setAmount(damageToLivingEntity);
		livingEntity.setAbsorptionAmount(absorptionBeforeDamage);
		absorption = livingEntity.getAbsorptionAmount();
		CompoundTag tag = livingEntity.getPersistentData();
		float totalDamage = tag.getFloat("totalDamage");
		totalDamage += viscosityDamage;
		tag.putFloat("totalDamage", totalDamage);
		MobEffectInstance oldEffect = livingEntity.getEffect(ExampleMod.VISCOSITY_EFFECT.get());
		float nextDamage = totalDamage / 10.0F;
		if (nextDamage < 1.0F) {
			if (totalDamage >= 1.0F) {
				nextDamage = 1.0F;
			} else {
				nextDamage = totalDamage;
			}
		}
		livingEntity.addEffect(new MobEffectInstance(ExampleMod.VISCOSITY_EFFECT.get(), MobEffectInstance.INFINITE_DURATION, Mth.ceil(nextDamage) - 1));
		float health = livingEntity.getHealth();
		float maxHealth = livingEntity.getMaxHealth();
		float remainingDamage = damageToLivingEntity - absorption;
		if (remainingDamage >= 0.0F) {
			absorption = 0.0F;
			health = health - remainingDamage;
			if (health <= 0.0F) {
				return;
			}
		} else {
			absorption = absorption - damageToLivingEntity;
		}
		remainingDamage = nextDamage - absorption;
		// Print the values of these variables before entering the conditional block
         System.out.println("========== [Debug Damage Calculation] ==========");
         System.out.println("maxHealth: " + maxHealth);
         System.out.println("health: " + health);
         System.out.println("absorption: " + absorption);
         System.out.println("nextDamage: " + nextDamage);
         System.out.println("totalDamage: " + totalDamage);
         System.out.println("remainingDamage: " + remainingDamage);
         System.out.println("================================================");
		if (remainingDamage <= 0.0F) {
			float absorptionMin = Mth.ceil(Mth.ceil(maxHealth / 2.0F) + (absorption - nextDamage) / 2.0F);
			float absorptionMax = Mth.ceil(Mth.ceil(maxHealth / 2.0F) + absorption / 2.0F);
			float healthMin = Float.MAX_VALUE;
			float healthMax = Float.MIN_VALUE;
			ViscosityRender render = new ViscosityRender(healthMin, healthMax, absorptionMin, absorptionMax);
			MixinReference.renderReference.put(livingEntity.getUUID(), render);
		} else {
			float absorptionMin = Mth.ceil(maxHealth / 2.0F) + 1.0F;
			float absorptionMax = Mth.ceil(Mth.ceil(maxHealth / 2.0F) + absorption / 2.0F);
			float healthMin = Mth.ceil((health - remainingDamage) / 2.0F);
			float healthMax = Mth.ceil(health / 2.0F);
			ViscosityRender render = new ViscosityRender(healthMin, healthMax, absorptionMin, absorptionMax);
			MixinReference.renderReference.put(livingEntity.getUUID(), render);
		}
	}
}
