package simplexity.simpleveinmining;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MiningListener implements Listener {
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent blockBreakEvent) {
        Block blockBroken = blockBreakEvent.getBlock();
        Material blockMaterial = blockBroken.getType();
        Location blockLocation = blockBroken.getLocation().toBlockLocation();
        Player player = blockBreakEvent.getPlayer();
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        Set<Material> blockSet = ConfigHandler.getInstance().getBlockList();
        if (ConfigHandler.getInstance().isBlacklist() && blockSet.contains(blockMaterial)) return;
        if (!ConfigHandler.getInstance().isBlacklist() && !blockSet.contains(blockMaterial)) return;
        if (!player.hasPermission("veinmining.mining")) return;
        if (!blockBroken.isPreferredTool(heldItem) && ConfigHandler.getInstance().isRequireProperTool()) return;
        if (player.getGameMode().equals(GameMode.CREATIVE) && !ConfigHandler.getInstance().isWorksInCreative()) return;
        if (player.isSneaking()) return;
        int maxSearch = checkItemDurability(heldItem);
        if (maxSearch < 2) {
            player.sendMessage("PLACEHOLDER ERROR - NOT ENOUGH DURABILITY FOR VEIN MINING");
        }
        Set<Material> blocksToCheck = new HashSet<>();
        if (!ConfigHandler.getInstance().isOnlySameType()) {
            blocksToCheck.addAll(blockSet);
        } else {
            blocksToCheck.addAll(findGroup(blockMaterial));
        }
        Set<Location> blocksToBreak = CheckBlock.getInstance().getBlockList(blocksToCheck, blockLocation, maxSearch);
        breakBlocks(blocksToBreak, player);
    }
    
    private Set<Material> findGroup(Material materialOfBrokenBlock) {
        HashMap<String, Set<Material>> groupList = ConfigHandler.getInstance().getGroupList();
        String key = null;
        for (String loopKey : groupList.keySet()) {
            Set<Material> materialSet = groupList.get(loopKey);
            if (materialSet.contains(materialOfBrokenBlock)) {
                key = loopKey;
                break;
            }
        }
        if (key == null) {
            return new HashSet<>();
        }
        return groupList.get(key);
    }
    
    private void breakBlocks(Set<Location> locations, Player player) {
        ItemStack itemToUse = player.getInventory().getItemInMainHand();
        int damageAmount = 0;
        int unbreakingEnchantLevel = itemToUse.getEnchantmentLevel(Enchantment.DURABILITY);
        for (Location location : locations) {
            location.getBlock().breakNaturally(itemToUse, ConfigHandler.getInstance().isRunEffects(), ConfigHandler.getInstance().isDropXP());
            if (ConfigHandler.getInstance().isDamageTool()) {
                damageAmount = damageAmount + 1;
            }
        }
        if (ConfigHandler.getInstance().isRespectUnbreakingEnchant()) {
            damageAmount = damageAmount / unbreakingEnchantLevel;
        }
        player.getInventory().getItemInMainHand().damage(damageAmount, player);
    }
    
    private int checkItemDurability(ItemStack heldItem) {
        int maxSearch;
        int maxConfiguredSearch = ConfigHandler.getInstance().getMaxBlocksToScan();
        if (ConfigHandler.getInstance().isDamageTool() && ConfigHandler.getInstance().isPreventBreakingTool() && (heldItem instanceof Damageable damageableItem)) {
            int maxDurability = heldItem.getType().getMaxDurability();
            int currentDamage = damageableItem.getDamage();
            int currentDurability = maxDurability - currentDamage;
            maxSearch = currentDurability - 5;
            if (maxSearch > maxConfiguredSearch) {
                return maxConfiguredSearch;
            }
            return maxSearch;
        }
        return maxConfiguredSearch;
    }
    
}
