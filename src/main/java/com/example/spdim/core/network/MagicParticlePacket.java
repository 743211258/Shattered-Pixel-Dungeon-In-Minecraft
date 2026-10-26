package com.example.spdim.core.network;

import com.example.spdim.core.renderer.MagicParticleRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MagicParticlePacket {

	private final Vec3 origin;
	private final Vec3 forward;
	private final float[][] colors;
	private final float[] weights;

	public MagicParticlePacket(
			Vec3 origin,
			Vec3 forward,
			float[][] colors,
			float[] weights
	) {
		this.origin = origin;
		this.forward = forward;
		this.colors = colors;
		this.weights = weights;
	}

	public static void encode(MagicParticlePacket msg, FriendlyByteBuf buf) {
		buf.writeDouble(msg.origin.x);
		buf.writeDouble(msg.origin.y);
		buf.writeDouble(msg.origin.z);

		buf.writeDouble(msg.forward.x);
		buf.writeDouble(msg.forward.y);
		buf.writeDouble(msg.forward.z);

		buf.writeVarInt(msg.colors.length);

		for (int i = 0; i < msg.colors.length; i++) {
			buf.writeFloat(msg.colors[i][0]);
			buf.writeFloat(msg.colors[i][1]);
			buf.writeFloat(msg.colors[i][2]);
			buf.writeFloat(msg.weights[i]);
		}
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

		int colorCount = buf.readVarInt();

		float[][] colors = new float[colorCount][3];
		float[] weights = new float[colorCount];

		for (int i = 0; i < colorCount; i++) {
			colors[i][0] = buf.readFloat();
			colors[i][1] = buf.readFloat();
			colors[i][2] = buf.readFloat();
			weights[i] = buf.readFloat();
		}

		return new MagicParticlePacket(origin, forward, colors, weights);
	}

	public static void handle(
			MagicParticlePacket msg,
			Supplier<NetworkEvent.Context> ctx
	) {
		ctx.get().enqueueWork(() -> {
			MagicParticleRenderer.render(
					msg.origin,
					msg.forward,
					msg.colors,
					msg.weights
			);
		});

		ctx.get().setPacketHandled(true);
	}
}
