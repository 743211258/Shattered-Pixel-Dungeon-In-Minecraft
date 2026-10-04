package com.example.spdim.core.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

import java.util.function.Supplier;

public class BlastWaveSyncPacket {

	private final Vec3 position;

	public BlastWaveSyncPacket(Vec3 position) {
		this.position = position;
	}

	public Vec3 getPosition() {
		return position;
	}

	public static void encode(BlastWaveSyncPacket msg, FriendlyByteBuf buf) {
		buf.writeDouble(msg.position.x);
		buf.writeDouble(msg.position.y);
		buf.writeDouble(msg.position.z);
	}

	public static BlastWaveSyncPacket decode(FriendlyByteBuf buf) {
		double x = buf.readDouble();
		double y = buf.readDouble();
		double z = buf.readDouble();

		return new BlastWaveSyncPacket(new Vec3(x, y, z));
	}

	public static void handle(BlastWaveSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Vec3 pushForce = msg.getPosition();
			// Client-side handling.

			Minecraft mc = Minecraft.getInstance();
			LocalPlayer player = mc.player;
			if (player == null) {
				return;
			}
			player.setDeltaMovement(pushForce);
		});

		ctx.get().setPacketHandled(true);
	}
}
