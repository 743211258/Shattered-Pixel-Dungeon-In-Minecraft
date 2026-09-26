package com.example.spdim.core.artifact;

import com.example.spdim.SPDIM;
import com.example.spdim.core.Artifact;
import com.example.spdim.core.Macro;
import com.example.spdim.core.mechanic.CooldownSystem;
import com.example.spdim.core.registry.ModEffects;
import com.example.spdim.core.network.ChaliceOfBloodOnUsePacket;
import com.example.spdim.core.network.MyModNetwork;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;

public class ChaliceOfBlood extends Artifact {

    public ChaliceOfBlood(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isApplicable(ItemStack stack, Level world) {
        return true;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        stack.setHoverName(Component.translatable("item.spdim.chalice_of_blood"));
        long now = world.getGameTime();
        CooldownSystem.createCooldownState(stack, 1, 1, 100, now);
    }

    public void onUseClientSide(Level world, Player player, ItemStack stack) {
        if (world.isClientSide) {
            MyModNetwork.CHANNEL.sendToServer(new ChaliceOfBloodOnUsePacket());
        }
    }
    public void onUseServerSide(ServerPlayer player) {
        // Get current health points
        float health = player.getHealth();

        // Calculate new health points
        float newHealth = health - Macro.CHALICE_OF_BLOOD_ON_USE_DAMAGE;
        if (newHealth < 0) {
            newHealth = 0;
        }

        // Apply new health points to the player
        player.setHealth(newHealth);

        player.hurtMarked = true;
        player.setAbsorptionAmount(player.getAbsorptionAmount() + Macro.CHALICE_OF_BLOOD_TEMPORARY_ABSORPTION);
        // Used to deliberately trigger the hurt animation.
        player.hurt(player.damageSources().fellOutOfWorld(),Macro.CHALICE_OF_BLOOD_DAMAGE_ENSUING_AFTER_ABSORPTION);
        // Apply movement speed, damage boost, jump boost, night vision, absorption, damage resistance to the player for one minute.
        MobEffectInstance swiftness = new MobEffectInstance(MobEffects.MOVEMENT_SPEED, Macro.CHALICE_OF_BLOOD_SWITFNESS_EFFECT_DURATION, 1, false, true);
        MobEffectInstance strength = new MobEffectInstance(MobEffects.DAMAGE_BOOST, Macro.CHALICE_OF_BLOOD_STRENGTH_EFFECT_DURATION, 1, false, true);
        MobEffectInstance jump = new MobEffectInstance(MobEffects.JUMP, Macro.CHALICE_OF_BLOOD_JUMP_EFFECT_DURATION, 1, false, true);
        MobEffectInstance nightVision = new MobEffectInstance(MobEffects.NIGHT_VISION, Macro.CHALICE_OF_BLOOD_NIGHT_VISION_EFFECT_DURATION, 0, false, true);
        MobEffectInstance absorption = new MobEffectInstance(MobEffects.ABSORPTION, Macro.CHALICE_OF_BLOOD_ABSORPTION_EFFECT_DURATION, 3, false, true);
        MobEffectInstance resistance = new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, Macro.CHALICE_OF_BLOOD_RESISTANCE_EFFECT_DURATION, 3, false, true);
        MobEffectInstance freeze = new MobEffectInstance(ModEffects.FREEZE.get(), Macro.CHALICE_OF_BLOOD_FREEZE_EFFECT_DURATION);
        MobEffectInstance noHealing = new MobEffectInstance(ModEffects.REGEN_DISABLED.get(), Macro.CHALICE_OF_BLOOD_NO_HEALING_EFFECT_DURATION);
        player.addEffect(swiftness);
        player.addEffect(strength);
        player.addEffect(jump);
        player.addEffect(nightVision);
        player.addEffect(absorption);
        player.addEffect(resistance);
        // Freeze the player for 10 seconds.
        player.addEffect(freeze);
        // Disable regeneration for 5 minutes.
        player.addEffect(noHealing);
    }
}
