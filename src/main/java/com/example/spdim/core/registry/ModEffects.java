package com.example.spdim.core.registry;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import com.example.spdim.core.mechanic.Freeze;
import com.example.spdim.core.mechanic.Invincible;
import com.example.spdim.core.mechanic.RegenerationDisabled;
import com.example.spdim.core.mechanic.ViscosityEffect;

public class ModEffects {
	public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, "spdim");

	public static final RegistryObject<MobEffect> FREEZE = EFFECTS.register("freeze", () -> new Freeze());
	public static final RegistryObject<MobEffect> VISCOSITY_EFFECT = EFFECTS.register("viscosity", () -> new ViscosityEffect());
	public static final RegistryObject<MobEffect> INVINCIBLE = EFFECTS.register("invincible", () -> new Invincible());
	public static final RegistryObject<MobEffect> REGEN_DISABLED = EFFECTS.register("regen_disabled", () -> new RegenerationDisabled());

}
