package com.example.spdim.core.wand;

import com.example.spdim.core.Wand;

import net.minecraft.nbt.CompoundTag;

import net.minecraft.network.chat.Component;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import com.example.spdim.core.interfaces.EnergyBased;
import com.example.spdim.core.mechanic.CooldownSystem;
// Energy wand class
public abstract class EnergyWand extends Wand implements EnergyBased{
    protected final int maxEnergy;
    protected final int energyCost;
    protected final int maxCooldown;

    protected Component name;

    public EnergyWand(Properties properties, int maxEnergy, int energyCost, int maxCooldown, Component name) {
        super(properties);
        this.maxEnergy = maxEnergy;
        this.energyCost = energyCost;
        this.maxCooldown = maxCooldown;
        this.name = name;
    }

    @Override
    public double getEnergyRestorationPerTick() {
        return 1;
    }

    @Override
    public int getConsumptionAmount() {
        return 1;
    }

    @Override
    public int getRestorationAmount() {
        return 1;
    }

    // Data are based on NBT (except name) instead of class variables
    public Component getName(ItemStack stack) {
        return name;
    }

    public void setName(Component name) {
        this.name = name;
    }

    // Plus one energy every time the cooldown finishes.
    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (world.isClientSide) {
            return;
        }
        long now = world.getGameTime();
        CooldownSystem.createCooldownState(stack, maxEnergy, maxEnergy, maxCooldown, now);
        CooldownSystem.tryRegainAnyEnergy(stack, 1, world);
    }

    protected abstract void cast(Level world, Player player, ItemStack stack);
}
