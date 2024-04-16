package simplexity.simpleveinmining.hooks.coreprotect;

import net.coreprotect.CoreProtectAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

public class LogBrokenBlocks {

    public static void logBrokenBlock(Player player, Location location) {
        CoreProtectAPI coreProtectAPI = CoreProtectHook.getInstance().getCoreProtect();
        Block block = location.getBlock();
        Material type = block.getType();
        BlockData blockData = block.getBlockData();
        coreProtectAPI.logRemoval(player.getName(), location, type, blockData);
    }
}
