package com.player.gigahertz.network;

import com.player.gigahertz.Gigahertz;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class ModNetwork {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Gigahertz.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int id = 0;

    public static void register() {
        // 0: C→S отправка сообщения
        CHANNEL.registerMessage(id++,
                SendMessagePacket.class,
                SendMessagePacket::encode,
                SendMessagePacket::decode,
                SendMessagePacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));

        // 1: C→S запрос сигнала
        CHANNEL.registerMessage(id++,
                RequestSignalPacket.class,
                RequestSignalPacket::encode,
                RequestSignalPacket::decode,
                RequestSignalPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));

        // 2: S→C ответ с уровнем сигнала
        CHANNEL.registerMessage(id++,
                SignalResponsePacket.class,
                SignalResponsePacket::encode,
                SignalResponsePacket::decode,
                SignalResponsePacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }
}