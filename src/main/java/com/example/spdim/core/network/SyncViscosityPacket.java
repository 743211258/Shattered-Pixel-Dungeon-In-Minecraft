// The following code was completely AI-generated. A review is required.

package com.example.spdim.core.network;

import com.example.spdim.core.data_structure.ViscosityRenderData;
import com.example.spdim.core.mechanic.MixinReference;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class SyncViscosityPacket {

    private final Map<UUID, ViscosityRenderData> renderReference;
    private final Map<UUID, ViscosityRenderData> totalDamageReference;

    public SyncViscosityPacket(
            Map<UUID, ViscosityRenderData> renderReference,
            Map<UUID, ViscosityRenderData> totalDamageReference) {

        this.renderReference = new HashMap<>(renderReference);
        this.totalDamageReference = new HashMap<>(totalDamageReference);
    }

    public static void encode(SyncViscosityPacket msg, FriendlyByteBuf buf) {

        buf.writeInt(msg.renderReference.size());

        for (var e : msg.renderReference.entrySet()) {

            buf.writeUUID(e.getKey());

            ViscosityRenderData r = e.getValue();

            buf.writeFloat(r.healthMin);
            buf.writeFloat(r.healthMax);
            buf.writeFloat(r.absorptionMin);
            buf.writeFloat(r.absorptionMax);
        }

        buf.writeInt(msg.totalDamageReference.size());

        for (var e : msg.totalDamageReference.entrySet()) {

            buf.writeUUID(e.getKey());

            ViscosityRenderData r = e.getValue();

            buf.writeFloat(r.healthMin);
            buf.writeFloat(r.healthMax);
            buf.writeFloat(r.absorptionMin);
            buf.writeFloat(r.absorptionMax);
        }
    }

    public static SyncViscosityPacket decode(FriendlyByteBuf buf) {

        Map<UUID, ViscosityRenderData> render = new HashMap<>();

        int size = buf.readInt();

        for (int i = 0; i < size; i++) {

            UUID uuid = buf.readUUID();

            render.put(uuid,
                    new ViscosityRenderData(
                            buf.readFloat(),
                            buf.readFloat(),
                            buf.readFloat(),
                            buf.readFloat()));
        }

        Map<UUID, ViscosityRenderData> total = new HashMap<>();

        size = buf.readInt();

        for (int i = 0; i < size; i++) {

            UUID uuid = buf.readUUID();

            total.put(uuid,
                    new ViscosityRenderData(
                            buf.readFloat(),
                            buf.readFloat(),
                            buf.readFloat(),
                            buf.readFloat()));
        }

        return new SyncViscosityPacket(render, total);
    }

    public static void handle(SyncViscosityPacket msg,
                              Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            MixinReference.renderReference.clear();
            MixinReference.renderReference.putAll(msg.renderReference);

            MixinReference.totalDamageRenderReference.clear();
            MixinReference.totalDamageRenderReference.putAll(msg.totalDamageReference);

        });

        ctx.get().setPacketHandled(true);
    }
}
