package com.example.spdim.core.mixin;

import com.example.spdim.core.registry.ModEffects;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import org.objectweb.asm.Opcodes;

@Mixin(Entity.class)
public abstract class CollisionFreezeServerMixin {

	@Unique
	private Vec3 spdim$preCollisionMovement;

	@Inject(
		method = "move",
		at = @At("HEAD")
	)
	private void spdim$beforeMove(
		MoverType type,
		Vec3 movement,
		CallbackInfo ci
	) {
		spdim$preCollisionMovement = null;
	}

	@WrapOperation(
		method = "move",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Entity;collide(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"
		)
	)
	private Vec3 spdim$captureMovement(
		Entity instance,
		Vec3 movement,
		Operation<Vec3> original
	) {
		if (instance instanceof LivingEntity
			&& !(instance instanceof Player)
			&& !instance.level().isClientSide) {

			spdim$preCollisionMovement = movement;
		}

		return original.call(instance, movement);
	}

	@Inject(
		method = "move",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/world/entity/Entity;horizontalCollision:Z",
			opcode = Opcodes.PUTFIELD,
			ordinal = 0,
			shift = At.Shift.AFTER
		)
	)
	private void spdim$afterCollision(
		MoverType type,
		Vec3 movement,
		CallbackInfo ci
	) {
		Entity entity = (Entity) (Object) this;

		if (!(entity instanceof LivingEntity livingEntity)) return;
		if (entity instanceof Player) return;
		if (entity.level().isClientSide) return;

		Vec3 preCollision = spdim$preCollisionMovement;
		spdim$preCollisionMovement = null;

		if (!livingEntity.horizontalCollision || preCollision == null) return;

		double speed = preCollision.length();

		if (speed > 3.0D) {
			int duration = (int) ((speed / 2.0D) * 20.0D);

			livingEntity.addEffect(
				new MobEffectInstance(
					ModEffects.FREEZE.get(),
					duration
				)
			);
		}
	}
}
