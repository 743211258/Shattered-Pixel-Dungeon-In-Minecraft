package com.example.spdim.core.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;

import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import com.example.spdim.core.registry.ModEffects;

public class CollisionFreezePacket {

	private final int duration;

	public CollisionFreezePacket(int duration) {
		this.duration = duration;
	}

	public static void encode(CollisionFreezePacket msg, FriendlyByteBuf buf) {
		buf.writeVarInt(msg.duration);
	}

	public static CollisionFreezePacket decode(FriendlyByteBuf buf) {
		return new CollisionFreezePacket(buf.readVarInt());
	}

	public static void handle(CollisionFreezePacket msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer player = ctx.get().getSender();

			if (player == null || msg.duration <= 0) {
				return;
			}

			player.addEffect(new MobEffectInstance(
				ModEffects.FREEZE.get(),
				msg.duration
			));
		});

		ctx.get().setPacketHandled(true);
	}
}

