package com.example.spdim.core.event;

import com.example.spdim.core.mechanic.Invincible;
import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = "spdim", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TimekeepersHourglassServerEvents {

    @SubscribeEvent
    public static void onLivingBeenAttacked(LivingHurtEvent event) {
        // Diable any damage if the target is untargetable.
        if (Invincible.isInvincible(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onMobAttack(TickEvent.PlayerTickEvent event) {
        // Happens only at the end phase.
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        // Get the player.
        Player player = event.player;
        // Nothing need to be done if the player is not untargetable.
        if (!(player.level() instanceof ServerLevel) || !Invincible.isInvincible(player)) {
            return;
        }
        //
        double radius = 32.0;
        AABB area = player.getBoundingBox().inflate(radius);
        List<Mob> mobsNearby = player.level().getEntitiesOfClass(Mob.class, area);

        // Remove mobs' attack intentions if they are within the radius
        // No need to check for inscribed sphere since most mobs has an attacking radius less than 32
        for (Mob mob : mobsNearby) {
            if (mob.getTarget() instanceof Player target && Invincible.isInvincible(target)) {
                mob.setTarget(null);
            }
        }
    }
    @SubscribeEvent
    public static void onMobChangeTarget(LivingChangeTargetEvent event) {
        LivingEntity newTarget = event.getNewTarget();

        // Prevent mobs from choosing untargetable players.
        if (newTarget instanceof Player player && Invincible.isInvincible(player)) {
            event.setNewTarget(null);
        }
    }
}
