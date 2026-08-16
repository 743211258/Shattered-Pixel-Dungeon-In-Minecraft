package com.example.spdim.core.artifact;

import com.example.spdim.ExampleMod;
import com.example.spdim.core.Artifact;
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

    protected final int EFFECT_DURATION = 3600;

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
        float newHealth = health - 19.0F;
        if (newHealth < 0) {
            newHealth = 0;
        }

        // Apply new health points to the player
        player.setHealth(newHealth);

        player.hurtMarked = true;
        player.setAbsorptionAmount(player.getAbsorptionAmount() + 200.0F);
        // Used to deliberately trigger the hurt animation.
        player.hurt(player.damageSources().fellOutOfWorld(), 10F);
        // Apply movement speed, damage boost, jump boost, night vision, absorption, damage resistance to the player for one minute.
        MobEffectInstance swiftness = new MobEffectInstance(MobEffects.MOVEMENT_SPEED, EFFECT_DURATION, 1, false, true);
        MobEffectInstance strength = new MobEffectInstance(MobEffects.DAMAGE_BOOST, EFFECT_DURATION, 1, false, true);
        MobEffectInstance jump = new MobEffectInstance(MobEffects.JUMP, EFFECT_DURATION, 1, false, true);
        MobEffectInstance nightVision = new MobEffectInstance(MobEffects.NIGHT_VISION, EFFECT_DURATION, 0, false, true);
        MobEffectInstance absorption = new MobEffectInstance(MobEffects.ABSORPTION, EFFECT_DURATION, 3, false, true);
        MobEffectInstance resistance = new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, EFFECT_DURATION, 3, false, true);
        MobEffectInstance freeze = new MobEffectInstance(ModEffects.FREEZE.get(), 200);
        MobEffectInstance regenDisabled = new MobEffectInstance(ModEffects.REGEN_DISABLED.get(), 6000);
        player.addEffect(swiftness);
        player.addEffect(strength);
        player.addEffect(jump);
        player.addEffect(nightVision);
        player.addEffect(absorption);
        player.addEffect(resistance);
        // Freeze the player for 10 seconds.
        player.addEffect(freeze);
        // Disable regeneration for 5 minutes.
        player.addEffect(regenDisabled);
    }
}
