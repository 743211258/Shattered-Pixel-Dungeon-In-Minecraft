package com.example.spdim.core.network;

import net.minecraft.world.item.ItemStack;
import com.example.spdim.core.artifact.DriedRose;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class DriedRoseTauntPacket {

	public DriedRoseTauntPacket() {
		// Does not contain any additional information.
	}

	public static void encode(DriedRoseTauntPacket msg, FriendlyByteBuf buf) {
		// Empty since there is no data.
  }

	public static DriedRoseTauntPacket decode(FriendlyByteBuf buf) {
		return new DriedRoseTauntPacket();
	}

	public static void handle(DriedRoseTauntPacket msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer player = ctx.get().getSender();
			if (player == null) {
				return;
			}
			ItemStack stack = player.getOffhandItem();
			if (!stack.isEmpty() && stack.getItem() instanceof DriedRose item) {
				item.tauntServerSide(stack, player.serverLevel(), player);
			}
		});
	ctx.get().setPacketHandled(true);
	}
}

