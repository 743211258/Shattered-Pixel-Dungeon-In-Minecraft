package com.example.spdim.core.enchantment;

import net.minecraft.world.item.enchantment.Enchantment;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class Viscosity extends Enchantment {

	// Viscosity can only be enchanted on chest armor.
	public Viscosity() {
		super(
			Rarity.UNCOMMON,
			EnchantmentCategory.ARMOR_CHEST,
			new EquipmentSlot[] {
				EquipmentSlot.CHEST
			}
		);
	}

	@Override
	public Enchantment.Rarity getRarity() {
		return Rarity.UNCOMMON;
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public int getMinLevel()
	{
		return 1;
	}

	@Override
	public int getMinCost(int level) {
		return 0;
	}

	@Override
	public int getMaxCost(int level) {
		return 200;
	}

	@Override
	public int getDamageProtection(int level, DamageSource source) {
		return 0;
	}

	@Override
	protected boolean checkCompatibility(Enchantment other) {
		return true;
	}

	@Override
	public boolean canEnchant(ItemStack stack) {
		if (stack.getItem() instanceof ArmorItem armor) {
			return armor.getEquipmentSlot() == EquipmentSlot.CHEST;
		}
		return false;
	}

	@Override
	public boolean isTreasureOnly() {
		return false;
	}

	@Override
	public boolean isCurse() {
		return false;
	}

	@Override
	public boolean isTradeable() {
		return true;
	}

	@Override
	public boolean isDiscoverable() {
		return true;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) {
		return canEnchant(stack);
	}

	@Override
	public boolean isAllowedOnBooks() {
		return true;
	}
}
