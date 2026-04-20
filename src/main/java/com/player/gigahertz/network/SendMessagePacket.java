package com.player.gigahertz.network;

import com.player.gigahertz.registry.ModBlocks;
import com.player.gigahertz.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class SendMessagePacket {

    private static final int MAX_LENGTH   = 256;
    private static final int DIRECT_RANGE = 32;   // Direct Mode: без башни
    private static final int TOWER_SEARCH = 64;   // Радиус поиска башни вокруг отправителя
    private static final int TOWER_BROADCAST = 64; // Радиус вещания башни
    // Ограничение по высоте при поиске башни (±32 блока по Y — разумно для любой постройки)
    private static final int TOWER_SEARCH_Y = 32;

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
            var level = sender.serverLevel();
            BlockPos senderPos = sender.blockPosition();

            // Ищем ближайшую башню 1G в радиусе TOWER_SEARCH блоков от отправителя
            Optional<BlockPos> tower = findNearestTower(level, senderPos);

            List<ServerPlayer> recipients;
            String modeTag;

            if (tower.isPresent()) {
                // Режим ретрансляции: вещаем от башни на TOWER_BROADCAST блоков
                BlockPos towerPos = tower.get();
                recipients = level.players().stream()
                        .filter(p -> p.blockPosition().distSqr(towerPos) <= (long) TOWER_BROADCAST * TOWER_BROADCAST)
                        .toList();
                modeTag = "§8[§21G§8][§aTower§8]";
            } else {
                // Direct Mode: 32 блока от отправителя
                recipients = level.players().stream()
                        .filter(p -> p.distanceToSqr(sender) <= (double) DIRECT_RANGE * DIRECT_RANGE)
                        .toList();
                modeTag = "§8[§21G§8][§7Direct§8]";
            }

            Component broadcast = Component.literal(
                    modeTag + " §f" + sender.getName().getString() + "§8: §7" + finalMsg
            );

            for (ServerPlayer player : recipients) {
                player.sendSystemMessage(broadcast);
                level.playSound(
                        null,
                        player.blockPosition(),
                        ModSounds.RADIO_STATIC.get(),
                        SoundSource.PLAYERS,
                        0.5f,
                        0.85f + level.random.nextFloat() * 0.3f
                );
            }
        });

        ctx.setPacketHandled(true);
    }

    /**
     * Ищет ближайший блок AntennaTower1G в кубическом регионе вокруг center.
     * Y-диапазон ограничен ±TOWER_SEARCH_Y для производительности.
     * Горизонтальный радиус — TOWER_SEARCH блоков (по сфере, не кубу).
     */
    private static Optional<BlockPos> findNearestTower(
            net.minecraft.server.level.ServerLevel level, BlockPos center) {

        BlockPos best = null;
        double bestDist = Double.MAX_VALUE;
        double maxDistSq = (double) TOWER_SEARCH * TOWER_SEARCH;

        // Итерируем только загруженные чанки — башня в незагруженном чанке не работает (исторически верно: связь требует активной вышки)
        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-TOWER_SEARCH, -TOWER_SEARCH_Y, -TOWER_SEARCH),
                center.offset( TOWER_SEARCH,  TOWER_SEARCH_Y,  TOWER_SEARCH))) {

            if (!level.isLoaded(pos)) continue;

            BlockState state = level.getBlockState(pos);
            if (!state.is(ModBlocks.ANTENNA_TOWER_1G.get())) continue;

            double dist = pos.distSqr(center);
            if (dist <= maxDistSq && dist < bestDist) {
                bestDist = dist;
                best = pos.immutable();
            }
        }

        return Optional.ofNullable(best);
    }
}