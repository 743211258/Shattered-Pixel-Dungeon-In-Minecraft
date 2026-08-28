package com.example.spdim.core.artifact;

import com.example.spdim.core.functions.Functions;
import com.example.spdim.core.mechanic.Summon;
import com.example.spdim.core.mechanic.Invincible;
import com.example.spdim.core.mechanic.TargetLock;
import com.example.spdim.core.mechanic.Taunt;
import com.example.spdim.core.Artifact;
import com.example.spdim.core.network.DriedRoseSummonPacket;
import com.example.spdim.core.network.DriedRoseControlPacket;
import com.example.spdim.core.network.DriedRoseTauntPacket;
import com.example.spdim.core.network.DriedRoseTeleportPacket;
import com.example.spdim.core.network.MyModNetwork;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.MinecraftServer;

import net.minecraft.nbt.CompoundTag;

import net.minecraft.network.chat.Component;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Wolf;

import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

public class DriedRose extends Artifact {

	public enum STATE {
		IDLE,
		USING,
		COOLDOWN
	}

	protected final int COOLDOWN = 2400;
	protected final int CONTROL_RADIUS = 100;
	protected final int TELEPORT_RADIUS = 200;

	public DriedRose(Properties properties) {
		super(properties);
	}

	// The artifact is only applicable when it is in IDLE state.
	@Override
	public boolean isApplicable(ItemStack stack, Level world) {
		STATE current = getState(stack);
		if (current == null) {
			return false;
		}
		return current == STATE.IDLE;
	}

	public STATE getState(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		if (tag == null || !tag.contains("State")) {
			return null;
		}
		return STATE.valueOf(tag.getString("State"));
	}

	// During every tick, initialize the NBT if needed,
	// Reconcile if the state is async with the NBTs.
	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(stack, world, entity, slot, selected);
		stack.setHoverName(Component.translatable("item.spdim.dried_rose"));

		if (world.isClientSide()) {
			return;
		}

		initializeNBT(stack, world);

		CompoundTag tag = stack.getTag();
		if (tag == null) {
			return;
		}
		if (!(world instanceof ServerLevel serverLevel)) {
			return;
		}

		if (!(entity instanceof ServerPlayer serverPlayer)) {
			return;
		}
		if (getState(stack) == STATE.USING && tag.contains("SummonedUUID")) {
    	MinecraftServer server = serverLevel.getServer();
			Entity summoned = null;
			for (ServerLevel level : server.getAllLevels()) {
				summoned = level.getEntity(tag.getUUID("SummonedUUID"));
				if (summoned != null) {
					break;
				}
			}
			boolean isNull = summoned == null;
	    boolean isAlive = summoned != null && summoned.isAlive();
    	boolean isRemoved = summoned != null && summoned.isRemoved();

    	System.out.println("summoned == null: " + isNull);
    	System.out.println("summoned.isAlive(): " + isAlive);
    	System.out.println("summoned.isRemoved(): " + isRemoved);

			if (summoned == null || !summoned.isAlive() || summoned.isRemoved()) { 
				onSummonedDeath(stack, world);
			}
		}
		if (getState(stack) == STATE.COOLDOWN && serverLevel.getGameTime() - tag.getLong("StartChargingTime") > COOLDOWN) {
			cooldownFinish(stack);
		}
		reconciliation(tag, stack, serverLevel, serverPlayer);
	}

	// The NBT is actually not fully initialized.
	// NBT SummonedUUID, which stores the UUID of the summoned, will be added to the artifact upon summoning and deleted upon death.
	private void initializeNBT(ItemStack stack, Level world) {
		CompoundTag tag = stack.getOrCreateTag();
		if (!tag.contains("StartChargingTime")) {
			tag.putLong("StartChargingTime", world.getGameTime() - COOLDOWN);
		}
		if (!tag.contains("State")) {
			tag.putString("State", "IDLE");
		}
	}

	// Reconcile if the state is async with the NBTs.
	private void reconciliation(CompoundTag tag, ItemStack stack, ServerLevel serverLevel, ServerPlayer serverPlayer) {
		String state = tag.getString("State");
		switch (state) {
			case "IDLE" -> reconcileIdle(tag);
			case "USING" -> reconcileUsing(tag, stack, serverLevel, serverPlayer);
			case "COOLDOWN" -> reconcileCooldown(tag, stack, serverLevel);
		}
	}

	// Reconcile the IDLE state.
	private void reconcileIdle(CompoundTag tag) {
		if (tag.contains("SummonedUUID")) {
			tag.remove("SummonedUUID");
		}
	}
	
	//Reconcile the USING state.
	private void reconcileUsing(CompoundTag tag, ItemStack stack, ServerLevel serverLevel, ServerPlayer serverPlayer) {
		if (!tag.contains("SummonedUUID")) {
			summonServerSide(stack, serverLevel, serverPlayer);
		}
	}

	// Reconcile the COOLDOWN state.
	private void reconcileCooldown(CompoundTag tag, ItemStack stack, ServerLevel serverLevel) {
		long startTime = tag.getLong("StartChargingTime");
		long now = serverLevel.getGameTime();
		if (tag.contains("SummonedUUID")) {
			tag.remove("SummonedUUID");
		}
		if (now - startTime >= COOLDOWN) {
			cooldownFinish(stack);
		}
	}

	// IDLE to USING client
	public void summonClientSide(ItemStack stack, Level world, Player player) {
		if (!world.isClientSide()) {
			return;
		}
		if (isApplicable(stack, world)) {
			MyModNetwork.CHANNEL.sendToServer(new DriedRoseSummonPacket());
		}
	}

	// IDLE to USING server
	public void summonServerSide(ItemStack stack, ServerLevel level, ServerPlayer player) {
		System.out.println("From IDLE to USING");
		CompoundTag tag = stack.getTag();
		if (tag == null) {
			return;
		}
		Entity newEntity = Summon.summon(level, EntityType.WOLF, player, (entity) -> {
			if (entity instanceof Wolf wolf) {
				wolf.tame(player);
				wolf.setOwnerUUID(player.getUUID());
				wolf.addEffect(new MobEffectInstance(
					MobEffects.DAMAGE_BOOST,
					Integer.MAX_VALUE,
					3
				));
				wolf.addEffect(new MobEffectInstance(
					MobEffects.DAMAGE_RESISTANCE,
					Integer.MAX_VALUE,
					3
				));
				wolf.addEffect(new MobEffectInstance(
					MobEffects.MOVEMENT_SPEED,
					Integer.MAX_VALUE,
					1
				));
				wolf.addEffect(new MobEffectInstance(
					MobEffects.REGENERATION,
					Integer.MAX_VALUE,
					1
				));
			}
		});
		if (newEntity == null) {
			return;
		}
		tag.putUUID("SummonedUUID", newEntity.getUUID());
		tag.putString("State", "USING");
		if (newEntity instanceof LivingEntity livingEntity) {
			Taunt.taunt(livingEntity);
		}
	}

	public void	controlClientSide(ItemStack stack, Level world, Player player) {
		if (!world.isClientSide()) {
			return;
		}
		if (getState(stack) == STATE.USING) {
			MyModNetwork.CHANNEL.sendToServer(new DriedRoseControlPacket());
		}
	}

	public void controlServerSide(ItemStack stack, ServerLevel level, ServerPlayer player) {
		System.out.println("Received");
		Vec3 start = player.getEyePosition();
		Vec3 look = player.getLookAngle();
		Vec3 end = start.add(look.scale(CONTROL_RADIUS));
		AABB box = player.getBoundingBox().expandTowards(look.scale(CONTROL_RADIUS)).inflate(1.0D);
		CompoundTag tag = stack.getTag();
		if (tag == null || !tag.contains("SummonedUUID")) {
			return;
		}	
		Entity entity = Functions.findEntity(level.getServer(), tag.getUUID("SummonedUUID"));
		EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(
			level,
			player,
			start,
  		end,
			box,
			e -> e != entity && e instanceof LivingEntity livingEntity && !Invincible.isInvincible(livingEntity) && livingEntity != player && livingEntity.isAlive()
		);
		if (hitResult != null && hitResult.getEntity() instanceof LivingEntity livingEntity) {	
			if (entity instanceof Wolf wolf) {
				System.out.println("OOO");
				wolf.setTarget(livingEntity);
				TargetLock.lockTarget(wolf, livingEntity);
			}
		}
	}

	public void tauntClientSide(ItemStack stack, Level world, Player player) {
		if (!world.isClientSide()) {
			return;
		}
		if (getState(stack) == STATE.USING) {
			MyModNetwork.CHANNEL.sendToServer(new DriedRoseTauntPacket());
		}
	
	}

	public void tauntServerSide(ItemStack stack, ServerLevel level, ServerPlayer player) {
		CompoundTag tag = stack.getTag();
		if (tag == null || !tag.contains("SummonedUUID")) {
			return;
		}
		Entity entity = Functions.findEntity(level.getServer(), tag.getUUID("SummonedUUID"));
		System.out.println(entity);
		if (entity instanceof LivingEntity livingEntity) {
			Taunt.control(livingEntity);
		}
	}

	public void	teleportClientSide(ItemStack stack, Level world, Player player) {
		if (!world.isClientSide()) {
			return;
		}
		if (getState(stack) == STATE.USING) {
			MyModNetwork.CHANNEL.sendToServer(new DriedRoseTeleportPacket());
		}
	}

	public void teleportServerSide(ItemStack stack, ServerLevel level, ServerPlayer player) {
		CompoundTag tag = stack.getTag();
		if (tag == null || !tag.contains("SummonedUUID")) {
			return;
		}
		Vec3 start = player.getEyePosition();
		Vec3 look = player.getLookAngle();
		Vec3 end = start.add(look.scale(TELEPORT_RADIUS));
		// Search for blocks
		BlockHitResult blockHit = level.clip(new ClipContext(
			start,
			end,
			ClipContext.Block.COLLIDER,
			ClipContext.Fluid.ANY,
			player
		));
		if (blockHit.getType() != HitResult.Type.MISS) {
			Vec3 location = blockHit.getLocation();
			BlockPos blockPos = getBlockAbove((Level) level, location, (Player) player);
			Entity entity = Functions.findEntity(level.getServer(), tag.getUUID("SummonedUUID"));
			if (entity == null) {
				return;
			}
			ServerLevel targetLevel = player.serverLevel();
			if (blockPos == null) {
				entity.teleportTo(
					targetLevel,
					location.x,
					location.y + 0.5,
					location.z,
					new HashSet<>(),
					entity.getYRot(),
					entity.getXRot()
				);
			} else {
				entity.teleportTo(
					targetLevel,
					blockPos.getX(),
					blockPos.getY() + 0.5,
					blockPos.getZ(),
					new HashSet<>(),
					entity.getYRot(),
					entity.getXRot()
				);
			}
		}
	}

	// USING to COOLDOWN
	private void onSummonedDeath(ItemStack stack, Level world) {
		System.out.println("From USING to COOLDOWN");
		CompoundTag tag = stack.getTag();
		if (tag == null) {
			return;
		}
		if (!tag.contains("SummonedUUID")) {
			return;
		}
		tag.remove("SummonedUUID");
		tag.putLong("StartChargingTime", world.getGameTime());
		tag.putString("State", "COOLDOWN");
	}

	// COOLDOWN to IDLE
	private void cooldownFinish(ItemStack stack) {
		System.out.println("From COOLDOWN to IDLE");
		CompoundTag tag = stack.getTag();
		if (tag == null) {
			return;
		}
		tag.putString("State", "IDLE");
	}

    @Nullable
    private BlockPos getBlockAbove(Level level, Vec3 pos, Player player) {
        // Calculate the starting point
        Vec3 start = new Vec3(pos.x, Mth.floor(level.getMaxBuildHeight()) + 1, pos.z);

        Vec3 end = new Vec3(pos.x, Mth.floor(pos.y + 1), pos.z);

        // Use clipContext to find the highest block above the player.
        BlockHitResult hit = level.clip(new ClipContext(
                start,
                end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                player
        ));

        if (hit.getType() == HitResult.Type.BLOCK) {
            return hit.getBlockPos();
        }
        // Return the upmost collidable block above the starting point.
        return null;
    }

}
