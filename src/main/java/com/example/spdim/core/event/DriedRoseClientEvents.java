package com.example.spdim.core.event;

import com.example.spdim.core.artifact.DriedRose;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = "spdim", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)

public class DriedRoseClientEvents {
	public static final KeyMapping summon = new KeyMapping(
		"key.mymod.DriedRoseSummon",
		GLFW.GLFW_KEY_Z,
		"key.categories.gameplay"
	);

	public static final KeyMapping control = new KeyMapping(
		"key.mymod.DriedRoseControl",
		GLFW.GLFW_KEY_X,
		"key.categories.gameplay"
	);

	public static final KeyMapping taunt = new KeyMapping(
		"key.mymod.DriedRoseTaunt",
		GLFW.GLFW_KEY_1,
		"key.categories.gameplay"
	);

	public static final KeyMapping teleport = new KeyMapping(
		"key.mymod.DriedRoseTeleport",
		GLFW.GLFW_KEY_C,
		"key.categories.gameplay"
	);

	@SubscribeEvent
	public static void onKeyInput(InputEvent.Key event) {
		if (event.getAction() != GLFW.GLFW_PRESS) {
			 return;
		}
		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		if (player == null) {
			return;
		}
		if (summon.consumeClick()) {
			ItemStack stack = player.getOffhandItem();
			if (stack.getItem() instanceof DriedRose item) {
				item.summonClientSide(stack, player.level(), player);
			}
		}
		if (control.consumeClick()) {
			ItemStack stack = player.getOffhandItem();
			if (stack.getItem() instanceof DriedRose item) {
				item.controlClientSide(stack, player.level(), player);
			}
		}
		if (taunt.consumeClick()) {
			ItemStack stack = player.getOffhandItem();
			if (stack.getItem() instanceof DriedRose item) {
				item.tauntClientSide(stack, player.level(), player);
			}
		}		
		if (teleport.consumeClick()) {
			ItemStack stack = player.getOffhandItem();
			if (stack.getItem() instanceof DriedRose item) {
				item.teleportClientSide(stack, player.level(), player);
			}
		}

	}
}
