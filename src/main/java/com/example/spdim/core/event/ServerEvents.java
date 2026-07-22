package com.example.spdim.core.event;

import com.example.spdim.core.mechanic.RegenerationDisabled;
import com.example.spdim.core.mechanic.Rooted;
import com.example.spdim.core.mechanic.TickFreeze;
import com.example.spdim.core.mechanic.Untargetable;
import com.example.spdim.core.mechanic.TargetLock;
import com.example.spdim.core.mechanic.Summon;
import com.example.spdim.core.mechanic.Taunt;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.example.spdim.core.mechanic.MixinReference;

@Mod.EventBusSubscriber(modid = "spdim", bus = Mod.EventBusSubscriber.Bus.FORGE)

public class ServerEvents {
    // Run all tick function for every tick.
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        TickFreeze.tick();
        Untargetable.tick();
        RegenerationDisabled.tick();
        Rooted.tick();
				TargetLock.tick();
				Summon.tick();
        Taunt.tick();
    }
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }
        CompoundTag data = player.getPersistentData();

        if (data.getBoolean("hasEncyclopedia")) {
            return;
        } else {
            data.putBoolean("hasEncyclopedia", true);
        }
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        CompoundTag tag = book.getOrCreateTag();
        tag.putString("title", "Encyclopedia");
        tag.putString("author", "spdim");
        player.getInventory().add(book);
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            return;
        }

        CompoundTag data = event.getEntity().getPersistentData();
				MixinReference.renderReference.remove(event.getEntity().getUUID());
        data.remove("totalDamage");
        data.remove("ViscosityTick");
    }
}
