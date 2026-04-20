package com.player.gigahertz.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.Optional;
import java.util.function.Supplier;

/** Клиент → Сервер: запрос текущего уровня сигнала (при открытии GUI рации). */
public class RequestSignalPacket {

    public RequestSignalPacket() {}

    public static void encode(RequestSignalPacket packet, FriendlyByteBuf buf) {
        // нет данных
    }

    public static RequestSignalPacket decode(FriendlyByteBuf buf) {
        return new RequestSignalPacket();
    }

    public static void handle(RequestSignalPacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();

        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();
            if (sender == null) return;

            var level = sender.serverLevel();
            BlockPos pos = sender.blockPosition();

            int signalLevel = TowerUtils.getSignalLevel(level, pos);
            Optional<BlockPos> tower = TowerUtils.findNearestTower(level, pos);
            boolean towerMode = tower.isPresent();

            // Отправляем ответ обратно этому конкретному клиенту
            ModNetwork.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> sender),
                    new SignalResponsePacket(signalLevel, towerMode)
            );
        });

        ctx.setPacketHandled(true);
    }
}