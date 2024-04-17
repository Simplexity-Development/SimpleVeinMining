package simplexity.simpleveinmining;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class CheckBlock {

    private CheckBlock() {
    }

    private static CheckBlock instance;

    public static CheckBlock getInstance() {
        if (instance == null) instance = new CheckBlock();
        return instance;
    }

    public Set<Location> getBlockList(Set<Material> targets, Location start, int max) {
        Set<Location> valid_blocks = new HashSet<>();
        Queue<Location> next_blocks = new LinkedList<>();
        try {
            checkBlockRecursive(targets, valid_blocks, next_blocks, start, max);
        } catch (StackOverflowError e) {
            SimpleVeinMining.getInstance().getLogger().severe("SimpleVeinMining has tried to break so many blocks that it caused a StackOverflowError.");
            SimpleVeinMining.getInstance().getLogger().warning("SimpleVeinMining still managed to break as many blocks as possible, but you may want to lower the allowed block amount.");
        }
        return valid_blocks;
    }

    public void checkBlockRecursive(Set<Material> targets, Set<Location> valid, Queue<Location> next, Location current, int max) {
        valid.add(current);
        int[] positions = {-1, 0, 1};
        for (int x : positions) {
            for (int y : positions) {
                for (int z : positions) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    Location nextLocation = current.clone().add(x, y, z);
                    if (valid.size() < max && !valid.contains(nextLocation) && targets.contains(nextLocation.getBlock().getType())) {
                        next.add(nextLocation);
                        valid.add(nextLocation);
                    }
                }
            }
        }
        while (!next.isEmpty() && valid.size() < max) {
            checkBlockRecursive(targets, valid, next, next.poll(), max);
        }
    }
}
