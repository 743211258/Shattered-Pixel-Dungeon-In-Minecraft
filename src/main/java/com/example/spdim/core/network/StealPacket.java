package com.example.spdim.core.network;

import net.minecraft.world.item.ItemStack;
import com.example.spdim.core.artifact.MasterThievesArmband;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class StealPacket {

	public StealPacket() {
		// Does not contain any additional information.
	}

	public static void encode(StealPacket msg, FriendlyByteBuf buf) {
		// Empty since there is no data.
	}

	public static StealPacket decode(FriendlyByteBuf buf) {
		return new StealPacket();
	}

	public static void handle(StealPacket msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer player = ctx.get().getSender();
			if (player == null) {
				return;
			}
			ItemStack stack = player.getOffhandItem();
			if (!stack.isEmpty() && stack.getItem() instanceof MasterThievesArmband item && item.isApplicable(stack, player.level())) {
				item.stealServerSide(stack, player.serverLevel(), player);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}

