package com.example.spdim.core.network;

import com.example.spdim.core.mechanic.Taunt;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncTauntPacket {

	private final UUID summonedEntity;
	private final float taunt;
	private final boolean isOn;

	public SyncTauntPacket(
			UUID summonedEntity,
			float taunt,
			boolean isOn) {

		this.summonedEntity = summonedEntity;
		this.taunt = taunt;
		this.isOn = isOn;
	}

	public static void encode(SyncTauntPacket msg, FriendlyByteBuf buf) {
		buf.writeUUID(msg.summonedEntity);
		buf.writeFloat(msg.taunt);
		buf.writeBoolean(msg.isOn);
	}

	public static SyncTauntPacket decode(FriendlyByteBuf buf) {
		return new SyncTauntPacket(
			buf.readUUID(),
			buf.readFloat(),
			buf.readBoolean()
		);
	}

	public static void handle(
			SyncTauntPacket msg,
			Supplier<NetworkEvent.Context> ctx) {

		ctx.get().enqueueWork(() -> {
			Taunt.setTaunt(msg.summonedEntity, msg.taunt);
			Taunt.setIsOn(msg.summonedEntity, msg.isOn);
		});

		ctx.get().setPacketHandled(true);
	}
}
