package simplexity.simpleveinmining;

import com.destroystokyo.paper.MaterialSetTag;
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
import org.bukkit.persistence.PersistentDataType;

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
        player.sendMessage("RUNNING");
        if (ConfigHandler.getInstance().isBlacklist() && blockSet.contains(blockMaterial)) return;
        if (!ConfigHandler.getInstance().isBlacklist() && !blockSet.contains(blockMaterial)) return;
        if (!player.hasPermission("veinmining.mining")) return;
        boolean toggleEnabled = player.getPersistentDataContainer().getOrDefault(VeinMiningToggle.toggleKey, PersistentDataType.BOOLEAN, true);
        if (!toggleEnabled) return;
        if (!blockBroken.isPreferredTool(heldItem) && ConfigHandler.getInstance().isRequireProperTool()) return;
        if (player.getGameMode().equals(GameMode.CREATIVE) && !ConfigHandler.getInstance().isWorksInCreative()) return;
        if (player.isSneaking()) return;
        int maxSearch = checkItemDurability(heldItem);
        if (maxSearch < 5) {
            player.sendRichMessage(LocaleHandler.getInstance().getAlmostBroken());
            return;
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
            Set<Material> singleMaterial = new HashSet<>();
            singleMaterial.add(materialOfBrokenBlock);
            return singleMaterial;
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
            damageAmount = calculateDamageWithUnbreaking(damageAmount, unbreakingEnchantLevel);
        }
        player.getInventory().getItemInMainHand().damage(damageAmount, player);
    }
    
    private int checkItemDurability(ItemStack heldItem) {
        int maxSearch;
        int maxConfiguredSearch = ConfigHandler.getInstance().getMaxBlocksToBreak();
        if (!(ConfigHandler.getInstance().isDamageTool() &&
                ConfigHandler.getInstance().isPreventBreakingTool() &&
                (heldItem.getItemMeta() instanceof Damageable damageableItem) &&
                MaterialSetTag.ITEMS_TOOLS.isTagged(heldItem.getType()))) {
            return maxConfiguredSearch;
        }
        int maxDurability = heldItem.getType().getMaxDurability();
        int currentDamage = damageableItem.getDamage();
        maxSearch = maxDurability - currentDamage - 5;
        return Math.min(maxSearch, maxConfiguredSearch);
    }
    
    private int calculateDamageWithUnbreaking(int damageAmount, int unbreakingAmount) {
        double unbreakingDamageReduction = 0.2;
        for (int i = 0; i < unbreakingAmount; i++) {
            damageAmount = (int) (damageAmount * (1 - unbreakingDamageReduction));
        }
        return damageAmount;
    }
    
}
