package simplexity.simpleveinmining;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class CheckBlock {
    
    public Set<Location> getBlockListAsync(Set<Material> targets, Location start, int max) {
        SimpleVeinMining.getInstance().getServer().getAsyncScheduler().runNow(SimpleVeinMining.getInstance(), () -> getBlockList(targets, start, max))
    }
    public Set<Location> getBlockList(Set<Material> targets, Location start, int max) {
        Set<Location> valid_blocks = new HashSet<>();
        checkBlockRecursive(targets, valid_blocks, start, max);
        return valid_blocks;
    }
    
    
    
    public void checkBlockRecursive(Set<Material> targets, Set<Location> valid, Location current, int max) {
        valid.add(current);
        int[] positions = {-1, 0, 1};
        for (int x : positions) {
            for (int y : positions) {
                for (int z : positions) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    Location next = current.clone().add(x, y, z);
                    if (valid.size() < max && !valid.contains(next) && targets.contains(next.getBlock().getType())) {
                        checkBlockRecursive(targets, valid, next, max);
                    }
                }
            }
        }
    }
}
