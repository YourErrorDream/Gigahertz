package com.player.gigahertz.network;

import com.player.gigahertz.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class TowerUtils {

    public static final int DIRECT_RANGE    = 32;
    public static final int TOWER_SEARCH    = 64;
    public static final int TOWER_BROADCAST = 64;
    public static final int TOWER_SEARCH_Y  = 32;

    /** Ищет ближайшую башню 1G в радиусе TOWER_SEARCH блоков от center. */
    public static Optional<BlockPos> findNearestTower(ServerLevel level, BlockPos center) {
        BlockPos best = null;
        double bestDist = Double.MAX_VALUE;
        double maxDistSq = (double) TOWER_SEARCH * TOWER_SEARCH;

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

    /**
     * Возвращает уровень сигнала 0–4.
     * Tower mode: 4/3/2/1 в зависимости от дистанции до башни.
     * Direct mode: всегда 2 (аналог, ограниченная дальность).
     */
    public static int getSignalLevel(ServerLevel level, BlockPos senderPos) {
        Optional<BlockPos> tower = findNearestTower(level, senderPos);
        if (tower.isPresent()) {
            double dist = Math.sqrt(tower.get().distSqr(senderPos));
            if (dist <= 16) return 4;
            if (dist <= 32) return 3;
            if (dist <= 48) return 2;
            return 1;
        }
        // Direct Mode — слабый аналоговый сигнал
        return 1;
    }
}