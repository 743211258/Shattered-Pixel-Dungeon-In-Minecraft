package com.example.spdim.core.wand.energyWand;

import com.example.spdim.core.wand.EnergyWand;
import com.example.spdim.core.mechanic.CooldownSystem;
import com.example.spdim.core.mechanic.Summon;
import com.example.spdim.core.projectile.BlastWave;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import com.example.spdim.SPDIM;
import com.example.spdim.core.Macro;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;


public class WandOfBlastWave extends EnergyWand{

	public WandOfBlastWave(Properties properties, int maxEnergy, int energyCost, int cooldown, Component name) {
		super(properties, maxEnergy, energyCost, cooldown, name);
	}

	@Override
	protected void cast(ServerLevel world, Player player, ItemStack stack) {
		// Cast happens only if the wand is charged
		if (CooldownSystem.hasPositiveEnergy(stack)) {
			// Generate a blast wave entity at eye level
			BlastWave wave = (BlastWave) Summon.summon(world, SPDIM.BLAST_WAVE.get(), player, entity -> {
				BlastWave blastWave = (BlastWave) entity;

				// Set the initial position, direction, and explosion radius
				Vec3 spawnPos = player.getEyePosition(1.0F);
				blastWave.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
				blastWave.setExplodeRadius(Macro.WAND_OF_BLAST_WAVE_EXPLODE_RADIUS);

				// Set the owner for mob AI (Mobs will attack the caster)
				blastWave.setOwner(player);

				// Set the speed
				blastWave.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, Macro.WAND_OF_BLAST_WAVE_SPEED, 0.0F);
				blastWave.hasImpulse = true;
			});

			// Additional condition to prevent bugs.
			CooldownSystem.consumeAnyEnergy(stack, 1, world);
		}
	}
}
