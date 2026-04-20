package com.player.gigahertz.network;

import com.player.gigahertz.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class SendMessagePacket {

    private static final int MAX_LENGTH = 256;

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
            if (msg.length() > MAX_LENGTH) msg = msg.substring(0, MAX_LENGTH);

            final String finalMsg = msg;
            ServerLevel level = sender.serverLevel();
            BlockPos senderPos = sender.blockPosition();

            Optional<BlockPos> tower = TowerUtils.findNearestTower(level, senderPos);

            List<ServerPlayer> recipients;
            String modeTag;

            if (tower.isPresent()) {
                BlockPos tp = tower.get();

                // === ЧАСТИЦЫ НАД БАШНЕЙ ===
                // Электрические искры — основной эффект передачи
                level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                        tp.getX() + 0.5, tp.getY() + 1.2, tp.getZ() + 0.5,
                        20, 0.45, 0.4, 0.45, 0.04);
                // Светящиеся частицы, поднимающиеся вверх — "сигнал уходит в эфир"
                level.sendParticles(ParticleTypes.END_ROD,
                        tp.getX() + 0.5, tp.getY() + 1.0, tp.getZ() + 0.5,
                        6, 0.2, 0.05, 0.2, 0.12);

                recipients = level.players().stream()
                        .filter(p -> p.blockPosition().distSqr(tp)
                                <= (double) TowerUtils.TOWER_BROADCAST * TowerUtils.TOWER_BROADCAST)
                        .toList();
                modeTag = "§8[§21G§8][§aTower§8]";
            } else {
                recipients = level.players().stream()
                        .filter(p -> p.distanceToSqr(sender)
                                <= (double) TowerUtils.DIRECT_RANGE * TowerUtils.DIRECT_RANGE)
                        .toList();
                modeTag = "§8[§21G§8][§7Direct§8]";
            }

            Component broadcast = Component.literal(
                    modeTag + " §f" + sender.getName().getString() + "§8: §7" + finalMsg
            );

            for (ServerPlayer player : recipients) {
                player.sendSystemMessage(broadcast);
                level.playSound(null,
                        player.blockPosition(),
                        ModSounds.RADIO_STATIC.get(),
                        SoundSource.PLAYERS,
                        0.5f,
                        0.85f + level.random.nextFloat() * 0.3f);
            }
        });

        ctx.setPacketHandled(true);
    }
}