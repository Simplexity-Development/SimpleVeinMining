package simplexity.simpleveinmining.listeners;

import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import simplexity.simpleveinmining.logic.CheckBlock;
import simplexity.simpleveinmining.SimpleVeinMining;
import simplexity.simpleveinmining.config.ConfigHandler;
import simplexity.simpleveinmining.config.LocaleHandler;
import simplexity.simpleveinmining.hooks.coreprotect.CoreProtectHook;
import simplexity.simpleveinmining.hooks.coreprotect.LogBrokenBlocks;
import simplexity.simpleveinmining.logic.ValidityChecks;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


@SuppressWarnings("UnstableApiUsage")
public class MiningListener implements Listener {

    // Block Breaking -> First Block Broken in the Vein
    ConcurrentHashMap<Location, Location> veinMinedBlocks = new ConcurrentHashMap<>();

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent blockBreakEvent) {
        Block blockBroken = blockBreakEvent.getBlock();
        Material blockMaterial = blockBroken.getType();
        Location blockLocation = blockBroken.getLocation().toBlockLocation();

        // Do not handle on this EventHandler
        if (veinMinedBlocks.containsKey(blockLocation)) return;
        Player player = blockBreakEvent.getPlayer();
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        Set<Material> blockSet = ConfigHandler.getInstance().getBlockList();

        if (!ValidityChecks.playerCanUseVeinMiner(player)) return;
        if (!ValidityChecks.blockIsValidToVeinMine(blockBroken, blockSet)) return;
        if (!ValidityChecks.toolIsValidForVeinMiner(heldItem, blockBroken)) return;

        Set<Material> blocksToCheck = new HashSet<>();
        if (!ConfigHandler.getInstance().isOnlySameType()) {
            blocksToCheck.addAll(blockSet);
        } else {
            blocksToCheck.addAll(findGroup(blockMaterial));
        }
        Bukkit.getScheduler().runTaskAsynchronously(
                SimpleVeinMining.getInstance(),
                () -> {
                    Set<Location> blocksToBreak = CheckBlock.getBlockList(player, blocksToCheck, blockLocation, ConfigHandler.getInstance().getMaxBlocksToBreak());
                    blocksToBreak.forEach(location -> veinMinedBlocks.put(location, blockLocation));
                    Bukkit.getScheduler().runTask(
                            SimpleVeinMining.getInstance(),
                            () -> breakBlocks(blocksToBreak, player)
                    );
                }
        );
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreakDropRedirect(BlockBreakEvent event) {
        Location broken = event.getBlock().getLocation();
        Location redirectTo = veinMinedBlocks.remove(broken);
        if (event.isCancelled()) return;
        if (redirectTo == null) return;
        if (!ConfigHandler.getInstance().isDropAtMinedLocation()) return;

        event.setDropItems(false);
        int xp = event.getExpToDrop();
        event.setExpToDrop(0);

        Player player = event.getPlayer();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

        Collection<ItemStack> drops = event.getBlock().getDrops(itemInMainHand, player);

        redirectTo = redirectTo.clone().add(0.5, 0.5, 0.5);
        for (ItemStack item : drops) {
            event.getBlock().getWorld().dropItemNaturally(redirectTo, item);
        }
        if (xp == 0) return;
        ExperienceOrb orb = redirectTo.getWorld().spawn(redirectTo, ExperienceOrb.class);
        orb.setExperience(xp);
    }

    /**
     * Match the blocks that are considered a "group".<br/>
     * ie: Coal Group -&gt COAL_ORE, DEEPSLATE_COAL_ORE
     *
     * @param materialOfBrokenBlock Type of block to find group for
     * @return The group of materials that the input material belongs to
     */
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
        boolean minimumDurabilityReached = false;
        for (Location location : locations) {
            if (CoreProtectHook.getInstance().getCoreProtect() != null)
                LogBrokenBlocks.logBrokenBlock(player, location);

            if (ConfigHandler.getInstance().isPreventBreakingTool() && getRemainingDurability(player.getInventory().getItemInMainHand()) <= 5) {
                veinMinedBlocks.remove(location);
                minimumDurabilityReached = true;
            } else player.breakBlock(location.getBlock());
        }
        if (minimumDurabilityReached) {
            player.sendRichMessage(LocaleHandler.getInstance().getAlmostBroken());
        }
    }

    private int getRemainingDurability(ItemStack heldItem) {
        Integer damage = heldItem.getData(DataComponentTypes.DAMAGE);
        Integer maxDamage = heldItem.getData(DataComponentTypes.MAX_DAMAGE);
        if (damage == null || maxDamage == null) return Integer.MIN_VALUE;

        return maxDamage - damage;
    }


}
