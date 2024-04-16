package simplexity.simpleveinmining.hooks;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GriefPreventionHook {

    public static boolean canBreakBlock(Player player, Location location) {
        return GriefPrevention.instance.allowBreak(player, location.getBlock(), location) == null;
    }
}
