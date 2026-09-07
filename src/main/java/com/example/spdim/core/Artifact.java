package com.example.spdim.core;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import com.example.spdim.core.interfaces.EnergyBased;

public abstract class Artifact extends Item implements EnergyBased {

	public Artifact(Properties properties) {
		super(properties);
	}

	@Override
	public double getEnergyRestorationPerTick() {
		return 1.0D;
	}

	@Override
	public int getConsumptionAmount() {
		return 1;
	}

	@Override
	public int getRestorationAmount() {
		return 1;
	}

	public abstract boolean isApplicable(ItemStack stack, Level world);		
}
