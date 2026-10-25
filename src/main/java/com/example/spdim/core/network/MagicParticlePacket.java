package com.example.spdim.core.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import com.example.spdim.core.renderer.MagicParticleRenderer;

public class MagicParticlePacket {

	private final Vec3 origin;
	private final Vec3 forward;

	public MagicParticlePacket(Vec3 origin, Vec3 forward) {
		this.origin = origin;
		this.forward = forward;
	}

	public static void encode(MagicParticlePacket msg, FriendlyByteBuf buf) {
		buf.writeDouble(msg.origin.x);
		buf.writeDouble(msg.origin.y);
		buf.writeDouble(msg.origin.z);

		buf.writeDouble(msg.forward.x);
		buf.writeDouble(msg.forward.y);
		buf.writeDouble(msg.forward.z);
	}

	public static MagicParticlePacket decode(FriendlyByteBuf buf) {
		Vec3 origin = new Vec3(
				buf.readDouble(),
				buf.readDouble(),
				buf.readDouble()
		);

		Vec3 forward = new Vec3(
				buf.readDouble(),
				buf.readDouble(),
				buf.readDouble()
		);

		return new MagicParticlePacket(origin, forward);
	}

	public static void handle(MagicParticlePacket msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			MagicParticleRenderer.render(msg.origin, msg.forward);
		});
		ctx.get().setPacketHandled(true);
	}
}
