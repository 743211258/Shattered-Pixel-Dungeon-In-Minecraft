package com.example.spdim.core.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

import com.example.spdim.core.artifact.TimekeepersHourglass;
import com.example.spdim.SPDIM;

public class FreezeOthersPacket {

    public FreezeOthersPacket() {
        // Does not contain any additional information.
    }

    public static void encode(FreezeOthersPacket msg, FriendlyByteBuf buf) {
        // Empty since there is no data.
    }

    public static FreezeOthersPacket decode(FriendlyByteBuf buf) {
        return new FreezeOthersPacket();
    }

    public static void handle(FreezeOthersPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) {
                return;
            }
            SPDIM.LOGGER.debug("Received FreezeOthersPacket from {}", player.getGameProfile().getName());

            var stack = player.getOffhandItem();
            // Check if the player's offhand is holding timekeeper's hourglass
            if (!stack.isEmpty() && stack.getItem() instanceof TimekeepersHourglass item) {
                item.FreezeOthersServerSide(player, player.serverLevel());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
