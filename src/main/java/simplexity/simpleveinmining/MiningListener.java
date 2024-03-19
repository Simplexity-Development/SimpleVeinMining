package simplexity.simpleveinmining;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MiningListener implements Listener {
    private final Set<Material> blockSet = ConfigHandler.getInstance().getBlockList();
    private final boolean isBlacklist = ConfigHandler.getInstance().isBlacklist();
    private final boolean onlySameType = ConfigHandler.getInstance().isOnlySameType();
    private final HashMap<String, Set<Material>> configuredTypes = ConfigHandler.getInstance().getGroupList();
    @EventHandler
    public void onBlockBreak(BlockBreakEvent blockBreakEvent){
        Block blockBroken = blockBreakEvent.getBlock();
        Material blockMaterial = blockBroken.getType();
        Location blockLocation = blockBroken.getLocation().toBlockLocation();
        Player player = blockBreakEvent.getPlayer();
        if (isBlacklist && blockSet.contains(blockMaterial)) return;
        if (!isBlacklist && !blockSet.contains(blockMaterial)) return;
        if (!player.hasPermission("veinmining.mining")) return;
        Set<Material> blocksToCheck = new HashSet<>();
        if (!onlySameType) {
            blocksToCheck.addAll(blockSet);
        } else {
            blocksToCheck.addAll(findGroup(blockMaterial));
        }
        Set<Location> blocksToBreak = CheckBlock.getInstance().getBlockList(blocksToCheck, blockLocation, 216);
        breakBlocks(blocksToBreak, player);
    }
    
    private Set<Material> findGroup(Material materialOfBrokenBlock){
        String key = null;
        for (String loopKey : configuredTypes.keySet()) {
            Set<Material> materialSet = configuredTypes.get(loopKey);
            if (materialSet.contains(materialOfBrokenBlock)) {
                key = loopKey;
                break;
            }
        }
        if (key == null) {
            return new HashSet<>();
        }
        return configuredTypes.get(key);
    }
    
    private void breakBlocks(Set<Location> locations, Player player) {
        ItemStack itemToUse = player.getInventory().getItemInMainHand();
        for (Location location : locations) {
            location.getBlock().breakNaturally(itemToUse);
        }
    }
    
    

}
