package com.example.spdim.core.mechanic;

import com.example.spdim.core.data_structure.CooldownState;
import com.example.spdim.core.interfaces.EnergyBased;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CooldownSystem {
	
	public static void createCooldownState(ItemStack stack, int maxEnergy, int currentEnergy, long cooldown, long lastUseTime) {
		CompoundTag root = stack.getTag();
		if (root != null && root.contains("CooldownSystem")) {
			return;
		}
		root = stack.getOrCreateTag();
		CompoundTag tag = new CompoundTag();
		tag.putInt("MaxEnergy", maxEnergy);
		tag.putInt("CurrentEnergy", currentEnergy);
		tag.putLong("Cooldown", cooldown);
		tag.putLong("LastUseTime", lastUseTime);
		root.put("CooldownSystem", tag);
	}

	public static CooldownState readCooldownState(ItemStack stack) {
		CompoundTag root = stack.getTag();
		if (root == null || !root.contains("CooldownSystem")) {
			return null;
		}
		CompoundTag tag = root.getCompound("CooldownSystem");
		CooldownState state = new CooldownState();
		state.maxEnergy = tag.getInt("MaxEnergy");
		state.currentEnergy = tag.getInt("CurrentEnergy");
		state.cooldown = tag.getLong("Cooldown");
		state.lastUseTime = tag.getLong("LastUseTime");
		return state;
	}

	public static void updateCooldownState(ItemStack stack, CooldownState state) {
		CompoundTag root = stack.getTag();
		if (root == null || !root.contains("CooldownSystem")) {
			return;
		}
		CompoundTag tag = root.getCompound("CooldownSystem");
		tag.putInt("MaxEnergy", state.maxEnergy);
		tag.putInt("CurrentEnergy", state.currentEnergy);
		tag.putLong("Cooldown", state.cooldown);
		tag.putLong("LastUseTime", state.lastUseTime);
	}

	public static void deleteCooldownState(ItemStack stack) {
		CompoundTag root = stack.getTag();
		if (root == null || !root.contains("CooldownSystem")) {
			return;
		}
		root.remove("CooldownSystem");
	}

	public static void updateAtCycleStart(ItemStack stack, Level level, CooldownState state) {
		if (!(stack.getItem() instanceof EnergyBased energyBased)) {
			return;
		}
		double restoration = energyBased.getEnergyRestorationPerTick();
		state.cooldown = (long) ((double) state.cooldown / restoration);
		state.lastUseTime = level.getGameTime();
		updateCooldownState(stack, state);
	}

	public static void updateDuringCycle(ItemStack stack, Level level, CooldownState state) {
		if (!(stack.getItem() instanceof EnergyBased energyBased)) {
			return;
		}
		double restoration = energyBased.getEnergyRestorationPerTick();
		double cooldown = (double) state.cooldown;	
		double diff = (double) (level.getGameTime() - state.lastUseTime);
		state.lastUseTime = (long) (cooldown - ((cooldown - diff) / restoration));
		updateCooldownState(stack, state);
	}

	public static void consumeAnyEnergy(ItemStack stack, int amount, Level level) {
		CooldownState state = readCooldownState(stack);
		if (state == null) {
			return;
		}	
		if (state.currentEnergy == state.maxEnergy) {
			state.currentEnergy = Math.max(0, state.currentEnergy - amount);
      System.out.println("Consume: " + state.currentEnergy);
			updateAtCycleStart(stack, level, state);
		} else {
			state.currentEnergy = Math.max(0, state.currentEnergy - amount);
			updateCooldownState(stack, state);
		}
	}

	public static void regainAnyEnergy(ItemStack stack, int amount, Level level) {
		CooldownState state = readCooldownState(stack);
		if (state == null) {
			return;
		}
		state.currentEnergy = Math.min(state.maxEnergy, state.currentEnergy + amount);
		updateAtCycleStart(stack, level, state);	
	}

	public static boolean hasCooldownState(ItemStack stack) {
		CompoundTag root = stack.getTag();
		if (root == null || !root.contains("CooldownSystem")) {
			return false;
		}
		return true;
	}

	public static void tryRegainAnyEnergy(ItemStack stack, int amount, Level level) {
		CooldownState state = readCooldownState(stack);
		if (state == null) {
			return;
		}
		if (state.currentEnergy < state.maxEnergy && level.getGameTime() - state.lastUseTime >= state.cooldown) {
			System.out.println("tryRegain: " + state.currentEnergy);
			regainAnyEnergy(stack, amount, level);
		}
	}

	public static boolean hasPositiveEnergy(ItemStack stack) {
		CooldownState state = readCooldownState(stack);
		if (state == null) {
			return false;
		}
		return (state.currentEnergy > 0);
	}

	public static int roundToNearestSixteen(ItemStack stack, Level level) {
		CooldownState state = readCooldownState(stack);
		if (state == null) {
			return -1;
		}
		long TimePassed = level.getGameTime() - state.lastUseTime;
		return Math.min(Mth.floor(16.0F * (float) TimePassed / (float) (state.cooldown)), 16);
	} 
}
