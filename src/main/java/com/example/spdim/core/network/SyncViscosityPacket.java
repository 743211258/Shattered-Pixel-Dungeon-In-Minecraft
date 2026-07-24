// The following code was completely AI-generated. A review is required.

package com.example.spdim.core.network;

import com.example.spdim.core.data_structure.ViscosityRender;
import com.example.spdim.core.data_structure.ViscosityTotalDamageRender;
import com.example.spdim.core.mechanic.MixinReference;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class SyncViscosityPacket {

    private final Map<UUID, ViscosityRender> renderReference;
    private final Map<UUID, ViscosityTotalDamageRender> totalDamageReference;

    public SyncViscosityPacket(
            Map<UUID, ViscosityRender> renderReference,
            Map<UUID, ViscosityTotalDamageRender> totalDamageReference) {

        this.renderReference = new HashMap<>(renderReference);
        this.totalDamageReference = new HashMap<>(totalDamageReference);
    }

    public static void encode(SyncViscosityPacket msg, FriendlyByteBuf buf) {

        buf.writeInt(msg.renderReference.size());

        for (var e : msg.renderReference.entrySet()) {

            buf.writeUUID(e.getKey());

            ViscosityRender r = e.getValue();

            buf.writeFloat(r.healthMin);
            buf.writeFloat(r.healthMax);
            buf.writeFloat(r.absorptionMin);
            buf.writeFloat(r.absorptionMax);
        }

        buf.writeInt(msg.totalDamageReference.size());

        for (var e : msg.totalDamageReference.entrySet()) {

            buf.writeUUID(e.getKey());

            ViscosityTotalDamageRender r = e.getValue();

            buf.writeFloat(r.healthMin);
            buf.writeFloat(r.healthMax);
            buf.writeFloat(r.absorptionMin);
            buf.writeFloat(r.absorptionMax);
        }
    }

    public static SyncViscosityPacket decode(FriendlyByteBuf buf) {

        Map<UUID, ViscosityRender> render = new HashMap<>();

        int size = buf.readInt();

        for (int i = 0; i < size; i++) {

            UUID uuid = buf.readUUID();

            render.put(uuid,
                    new ViscosityRender(
                            buf.readFloat(),
                            buf.readFloat(),
                            buf.readFloat(),
                            buf.readFloat()));
        }

        Map<UUID, ViscosityTotalDamageRender> total = new HashMap<>();

        size = buf.readInt();

        for (int i = 0; i < size; i++) {

            UUID uuid = buf.readUUID();

            total.put(uuid,
                    new ViscosityTotalDamageRender(
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
