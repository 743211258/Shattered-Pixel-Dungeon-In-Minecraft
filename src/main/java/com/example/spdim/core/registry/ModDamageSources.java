package com.example.spdim.core.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level; 

public class ModDamageSources {
	public static final ResourceKey<DamageType> DAMAGE_OVER_TIME = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("spdim", "damage_over_time"));
	public static DamageSource viscosity(Level level) {
		Holder<DamageType> holder = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DAMAGE_OVER_TIME);
		return new DamageSource(holder);
	}
}



