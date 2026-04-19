package com.player.gigahertz.network;

import com.player.gigahertz.registry.ModSounds;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class SendMessagePacket {

    private static final int MAX_LENGTH = 256;
    private static final double RANGE = 32.0;

    private final String message;

    public SendMessagePacket(String message) {
        this.message = message;
    }

    public static void encode(SendMessagePacket packet, FriendlyByteBuf buf) {
        buf.writeUtf(packet.message, MAX_LENGTH);
    }

    public static SendMessagePacket decode(FriendlyByteBuf buf) {
        return new SendMessagePacket(buf.readUtf(MAX_LENGTH));
    }

    public static void handle(SendMessagePacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();

        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();
            if (sender == null) return;

            String msg = packet.message.strip();
            if (msg.isEmpty()) return;

            // Clamp message length as a server-side safety measure
            if (msg.length() > MAX_LENGTH) {
                msg = msg.substring(0, MAX_LENGTH);
            }

            final String finalMsg = msg;

            // Broadcast to all players within range on the same level
            List<ServerPlayer> nearby = sender.serverLevel()
                    .players()
                    .stream()
                    .filter(p -> p.distanceToSqr(sender) <= RANGE * RANGE)
                    .toList();

            Component broadcast = Component.literal(
                    "§8[§21G§8] §f" + sender.getName().getString() + "§8: §7" + finalMsg
            );

            for (ServerPlayer player : nearby) {
                player.sendSystemMessage(broadcast);

                // Play radio static at the receiving player's position
                player.serverLevel().playSound(
                        null,                        // null = play for everyone
                        player.blockPosition(),
                        ModSounds.RADIO_STATIC.get(),
                        SoundSource.PLAYERS,
                        0.6f,
                        0.9f + player.level().random.nextFloat() * 0.2f  // slight pitch variation
                );
            }
        });

        ctx.setPacketHandled(true);
    }
}