package com.player.gigahertz.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Сервер → Клиент: ответ с уровнем сигнала и режимом. */
public class SignalResponsePacket {

    private final int     signalLevel; // 0–4
    private final boolean towerMode;

    public SignalResponsePacket(int signalLevel, boolean towerMode) {
        this.signalLevel = signalLevel;
        this.towerMode   = towerMode;
    }

    public static void encode(SignalResponsePacket packet, FriendlyByteBuf buf) {
        buf.writeByte(packet.signalLevel);
        buf.writeBoolean(packet.towerMode);
    }

    public static SignalResponsePacket decode(FriendlyByteBuf buf) {
        return new SignalResponsePacket(buf.readByte(), buf.readBoolean());
    }

    public static void handle(SignalResponsePacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();

        ctx.enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                    // Безопасно: этот пакет приходит только на клиент
                    if (Minecraft.getInstance().screen instanceof
                            com.player.gigahertz.screen.WalkieTalkieScreen screen) {
                        screen.updateSignal(packet.signalLevel, packet.towerMode);
                    }
                })
        );

        ctx.setPacketHandled(true);
    }
}