package simplexity.simpleveinmining;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import simplexity.simpleveinmining.hooks.worldguard.WorldGuardHook;
import simplexity.simpleveinmining.hooks.yardwatch.YardWatchHook;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class CheckBlock {

    /**
     * Retrieves the list of valid blocks that someone can vein mine.
     * Checks against permissions
     * @param player Player performing the vein mining
     * @param targets Set of valid blocks that can be broken (ie: COAL_ORE, IRON_ORE)
     * @param start The original block broken by the player
     * @param max The maximum number of blocks in the vein
     * @return Set of locations that are valid to use Player::breakBlock on
     */
    public static @NotNull Set<Location> getBlockList(@NotNull Player player, @NotNull Set<Material> targets, @NotNull Location start, int max) {
        Queue<Location> queue = new LinkedList<>();
        Set<Location> visited = new HashSet<>();
        Set<Location> result = new HashSet<>();

        queue.add(start);
        visited.add(start);

        int[] offsets = {-1, 0, 1};

        while (!queue.isEmpty() && result.size() < max) {
            Location current = queue.poll();
            Material type = current.getBlock().getType();

            if (targets.contains(type) && canBreakBlock(player, current)) result.add(current);
            if (result.size() >= max) break;

            for (int dx : offsets) {
                for (int dy : offsets) {
                    for (int dz : offsets) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;

                        Location neighbor = current.clone().add(dx, dy, dz);
                        if (visited.contains(neighbor)) continue;

                        visited.add(neighbor);

                        if (targets.contains(neighbor.getBlock().getType())) queue.add(neighbor);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Is the player allowed to break that block based on plugins like WorldGuard?
     * @param player Player performing the vein mining
     * @param location Block location being checked for validity
     * @return True if it can be broken, false otherwise
     */
    @SuppressWarnings("RedundantIfStatement") // Attempts to simplify last nested if, but will break visible structure of method
    private static boolean canBreakBlock(@NotNull Player player, @NotNull Location location) {
        if (SimpleVeinMining.getInstance().isWorldGuardEnabled()) {
            if (!WorldGuardHook.getInstance().canBreakBlockInRegion(player, location)) return false;
        }
        if (SimpleVeinMining.getInstance().hasYardWatchProvider()) {
            if (!YardWatchHook.canBreakBlock(player, location.getBlock())) return false;
        }
        return true;
    }

}
