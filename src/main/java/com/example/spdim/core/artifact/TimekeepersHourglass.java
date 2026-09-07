package com.example.spdim.core.artifact;

import com.example.spdim.core.Artifact;
import com.example.spdim.core.registry.ModEffects;
import com.example.spdim.core.mechanic.CooldownSystem;
import com.example.spdim.core.mechanic.Invincible;
import com.example.spdim.core.network.FreezeOthersPacket;
import com.example.spdim.core.network.FreezeSelfPacket;
import com.example.spdim.core.network.MyModNetwork;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public class TimekeepersHourglass extends Artifact {
    protected final int maxCooldown = 900;

    public TimekeepersHourglass(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isApplicable(ItemStack stack, Level world) {
        return CooldownSystem.hasPositiveEnergy(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        stack.setHoverName(Component.translatable("item.spdim.timekeepers_hourglass"));

        if (world.isClientSide) {
            return;
        }
        long now = world.getGameTime();
        CooldownSystem.createCooldownState(stack, 1, 1, maxCooldown, now);
        CooldownSystem.tryRegainAnyEnergy(stack, 1, world);
    }

    public void FreezeOthersClient(Level world, Player player, ItemStack stack) {
        if (world.isClientSide) {
            MyModNetwork.CHANNEL.sendToServer(new FreezeOthersPacket());
        }
    }

    public void FreezeOthersServerSide(ServerPlayer player, ServerLevel serverLevel) {
        ItemStack stack = player.getOffhandItem();
        if (!isApplicable(stack, player.level())) {
            return;
        }

        Vec3 start = player.getEyePosition(1.0F);
        Vec3 look = player.getLookAngle();
        double range = 50.0;
        Vec3 end = start.add(look.scale(range));

        var entityHit = ProjectileUtil.getEntityHitResult(
                player.level(),
                player,
                start,
                end,
                player.getBoundingBox().expandTowards(look.scale(range)).inflate(1.0),
                e -> e instanceof LivingEntity && e != player
        );
        if (entityHit != null && entityHit.getEntity() instanceof LivingEntity target && !Invincible.isInvincible(target)) {
            target.addEffect(new MobEffectInstance(ModEffects.FREEZE.get(), 100));
        } else {
            return;
        }
        CooldownSystem.consumeAnyEnergy(stack, 1, serverLevel);
    }

    public void FreezeMyselfClient(Level world, Player player, ItemStack stack) {
        if (world.isClientSide) {
            MyModNetwork.CHANNEL.sendToServer(new FreezeSelfPacket());
            return;
        }
    }

    public void FreezeMyselfServerSide(ServerPlayer player, ServerLevel serverLevel) {
        ItemStack stack = player.getOffhandItem();
        if (!isApplicable(stack, player.level())) {
            return;
        }
        CooldownSystem.consumeAnyEnergy(stack, 1, serverLevel);
        player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 200, 0, false, false)); 
        player.addEffect(new MobEffectInstance(ModEffects.INVINCIBLE.get(), 200));
    }
}
