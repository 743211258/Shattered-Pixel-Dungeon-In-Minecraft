package com.example.spdim.core.event;

import com.example.spdim.core.mechanic.Rooted;
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
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraft.server.level.ServerPlayer;

import com.example.spdim.core.mechanic.MixinReference;
import com.example.spdim.core.network.MyModNetwork;
import com.example.spdim.core.network.SyncViscosityPacket;

@Mod.EventBusSubscriber(modid = "spdim", bus = Mod.EventBusSubscriber.Bus.FORGE)

public class ServerEvents {
    // Run all tick function for every tick.
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Rooted.tick();
				TargetLock.tick();
				Summon.tick();
        Taunt.tick();
				SyncViscosityPacket packet =
								new SyncViscosityPacket(
												MixinReference.renderReference,
												MixinReference.totalDamageRenderReference);

				for (ServerPlayer player :
								ServerLifecycleHooks.getCurrentServer()
												.getPlayerList()
												.getPlayers()) {

						MyModNetwork.CHANNEL.send(
										PacketDistributor.PLAYER.with(() -> player),
										packet
						);
				}
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
