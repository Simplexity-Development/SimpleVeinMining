package simplexity.simpleveinmining;

import com.destroystokyo.paper.MaterialSetTag;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataType;
import simplexity.simpleveinmining.commands.VeinMiningToggle;
import simplexity.simpleveinmining.config.ConfigHandler;
import simplexity.simpleveinmining.config.LocaleHandler;
import simplexity.simpleveinmining.hooks.GriefPreventionHook;
import simplexity.simpleveinmining.hooks.coreprotect.CoreProtectHook;
import simplexity.simpleveinmining.hooks.coreprotect.LogBrokenBlocks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MiningListener implements Listener {
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
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
        boolean claimedBlocksInList = false;
        int unbreakingEnchantLevel = itemToUse.getEnchantmentLevel(Enchantment.DURABILITY);
        for (Location location : locations) {
            if (SimpleVeinMining.getInstance().isHasGP() && !GriefPreventionHook.canBreakBlock(player, location)) {
                claimedBlocksInList = true;
                continue;
            }
            if (CoreProtectHook.getInstance().getCoreProtect() != null) LogBrokenBlocks.logBrokenBlock(player, location);
            location.getBlock().breakNaturally(itemToUse, ConfigHandler.getInstance().isRunEffects(), ConfigHandler.getInstance().isDropXP());
            if (ConfigHandler.getInstance().isDamageTool()) {
                damageAmount = damageAmount + 1;
            }
        }
        if (ConfigHandler.getInstance().isRespectUnbreakingEnchant()) {
            damageAmount = calculateDamageWithUnbreaking(damageAmount, unbreakingEnchantLevel);
        }
        player.getInventory().getItemInMainHand().damage(damageAmount, player);
        if (claimedBlocksInList) player.sendRichMessage(LocaleHandler.getInstance().getClaimedBlocks());
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
