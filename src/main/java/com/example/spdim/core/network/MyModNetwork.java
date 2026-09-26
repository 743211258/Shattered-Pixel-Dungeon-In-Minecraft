package com.example.spdim.core.network;

import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.network.NetworkDirection;
import net.minecraft.resources.ResourceLocation;

import com.example.spdim.core.Macro;

import java.util.Optional;

public class MyModNetwork {
    public static SimpleChannel CHANNEL;

    // Register all customized network packets.
    public static void register() {
        CHANNEL = NetworkRegistry.ChannelBuilder
                .named(ResourceLocation.fromNamespaceAndPath("spdim", "main"))
                .networkProtocolVersion(() -> Macro.MYMODNETWORK_PROTOCOL_VERSION)
                .clientAcceptedVersions(Macro.MYMODNETWORK_PROTOCOL_VERSION::equals)
                .serverAcceptedVersions(Macro.MYMODNETWORK_PROTOCOL_VERSION::equals)
                .simpleChannel();

        int id = 0;

        CHANNEL.registerMessage(id++, FreezeOthersPacket.class,
                FreezeOthersPacket::encode,
                FreezeOthersPacket::decode,
                FreezeOthersPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );

        CHANNEL.registerMessage(id++, FreezeSelfPacket.class,
                FreezeSelfPacket::encode,
                FreezeSelfPacket::decode,
                FreezeSelfPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );

        CHANNEL.registerMessage(id++, ChaliceOfBloodOnUsePacket.class,
                ChaliceOfBloodOnUsePacket::encode,
                ChaliceOfBloodOnUsePacket::decode,
                ChaliceOfBloodOnUsePacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );

        CHANNEL.registerMessage(id++, DriedRoseSummonPacket.class,
                DriedRoseSummonPacket::encode,
                DriedRoseSummonPacket::decode,
                DriedRoseSummonPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
        CHANNEL.registerMessage(id++, DriedRoseControlPacket.class,
                DriedRoseControlPacket::encode,
                DriedRoseControlPacket::decode,
                DriedRoseControlPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
        CHANNEL.registerMessage(id++, DriedRoseTauntPacket.class,
                DriedRoseTauntPacket::encode,
                DriedRoseTauntPacket::decode,
                DriedRoseTauntPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
        CHANNEL.registerMessage(id++, DriedRoseTeleportPacket.class,
                DriedRoseTeleportPacket::encode,
                DriedRoseTeleportPacket::decode,
                DriedRoseTeleportPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
        CHANNEL.registerMessage(id++, StealPacket.class,
                StealPacket::encode,
                StealPacket::decode,
                StealPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
        CHANNEL.registerMessage(id++, SyncViscosityPacket.class,
                SyncViscosityPacket::encode,
                SyncViscosityPacket::decode,
                SyncViscosityPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        ); 
    }
}

