package com.example.spdim.core.artifact;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.projectile.ProjectileUtil;

import com.example.spdim.core.Artifact;
import com.example.spdim.core.mechanic.CooldownSystem;
import com.example.spdim.core.mechanic.Invincible;
import com.example.spdim.core.network.MyModNetwork;
import com.example.spdim.core.network.StealPacket;

public class MasterThievesArmband extends Artifact {

	protected final int ARMBAND_COOLDOWN = 6000;
	protected final int MAX_CONTROL_RANGE = 10;
	protected final EquipmentSlot[] DISARM_SLOTS = {
		EquipmentSlot.MAINHAND,
		EquipmentSlot.OFFHAND,
		EquipmentSlot.HEAD,
		EquipmentSlot.CHEST,
		EquipmentSlot.LEGS,
		EquipmentSlot.FEET
	};

	public MasterThievesArmband(Properties properties) {
		super(properties);
	}

	@Override
	public boolean isApplicable(ItemStack stack, Level world) {
		return CooldownSystem.hasPositiveEnergy(stack);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(stack, world, entity, slot, selected);
		stack.setHoverName(Component.translatable("item.spdim.master_thieves_armband"));

		if (world.isClientSide) {
			return;
		}
		long now = world.getGameTime();
		CooldownSystem.createCooldownState(stack, 1, 1, ARMBAND_COOLDOWN, now);
		CooldownSystem.tryRegainAnyEnergy(stack, 1, world);
	}

	public void stealClientSide(ItemStack stack, Level level, Player player) {
		if (!level.isClientSide) {
			return;
		}
		if (isApplicable(stack, level)) {
			MyModNetwork.CHANNEL.sendToServer(new StealPacket());
		}
	}

	public void stealServerSide(ItemStack stack, ServerLevel level, ServerPlayer player) {
		Vec3 start = player.getEyePosition(1.0F);
		Vec3 look = player.getLookAngle();
		Vec3 end = start.add(look.scale(MAX_CONTROL_RANGE));

    EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(
			level,
			player,
			start,
			end,
			player.getBoundingBox().expandTowards(look.scale(MAX_CONTROL_RANGE)).inflate(1.0),
			e -> e != player && e instanceof LivingEntity livingEntity && !Invincible.isInvincible(livingEntity)
		);
		if (hitResult != null && hitResult.getEntity() instanceof LivingEntity livingEntity) {
			for (EquipmentSlot slot : DISARM_SLOTS) {
				ItemStack equipmentStack = livingEntity.getItemBySlot(slot);
				if (equipmentStack.isEmpty()) {
					continue;
				}
				livingEntity.setItemSlot(slot, ItemStack.EMPTY);
				boolean success = player.getInventory().add(equipmentStack);
				if (!success) {
					ItemEntity item = new ItemEntity(
						level,
						player.getX(),
						player.getY(),
						player.getZ(),
						equipmentStack
					);
					level.addFreshEntity(item);
				}
			}
		} else {
			return;
		}
		CooldownSystem.consumeAnyEnergy(stack, 1, level);
	}
}
