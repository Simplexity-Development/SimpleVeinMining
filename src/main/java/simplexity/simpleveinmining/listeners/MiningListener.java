package simplexity.simpleveinmining.listeners;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import simplexity.simpleveinmining.CheckBlock;
import simplexity.simpleveinmining.SimpleVeinMining;
import simplexity.simpleveinmining.commands.VeinMiningToggle;
import simplexity.simpleveinmining.config.ConfigHandler;
import simplexity.simpleveinmining.config.LocaleHandler;
import simplexity.simpleveinmining.hooks.coreprotect.CoreProtectHook;
import simplexity.simpleveinmining.hooks.coreprotect.LogBrokenBlocks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


@SuppressWarnings("UnstableApiUsage")
public class MiningListener implements Listener {

    Set<Location> veinMinedBlocks = ConcurrentHashMap.newKeySet();

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)

    public void onBlockBreak(BlockBreakEvent blockBreakEvent) {
        Block blockBroken = blockBreakEvent.getBlock();
        Material blockMaterial = blockBroken.getType();
        Location blockLocation = blockBroken.getLocation().toBlockLocation();

        if (veinMinedBlocks.contains(blockLocation)) {
            veinMinedBlocks.remove(blockLocation);
            return;
        }

        Player player = blockBreakEvent.getPlayer();

        boolean toggleEnabled = player.getPersistentDataContainer().getOrDefault(VeinMiningToggle.toggleKey, PersistentDataType.BOOLEAN, true);
        if (!toggleEnabled) return;

        ItemStack heldItem = player.getInventory().getItemInMainHand();
        Set<Material> blockSet = ConfigHandler.getInstance().getBlockList();

        if (ConfigHandler.getInstance().isBlacklist() && blockSet.contains(blockMaterial)) return;
        if (!ConfigHandler.getInstance().isBlacklist() && !blockSet.contains(blockMaterial)) return;
        if (!player.hasPermission("veinmining.mining")) return;
        if (ConfigHandler.getInstance().requiresItemModel() && !hasRequiredItemModel(heldItem)) return;
        if (ConfigHandler.getInstance().doesCrouchPreventVeinMining() && player.isSneaking()) return;
        if (!blockBroken.isPreferredTool(heldItem) && ConfigHandler.getInstance().isRequireProperTool()) return;
        if (player.getGameMode().equals(GameMode.CREATIVE) && !ConfigHandler.getInstance().isWorksInCreative()) return;
        if (ConfigHandler.getInstance().isRequireLore() && !hasRequiredLore(heldItem)) return;
        if (player.isSneaking()) return;
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
                    veinMinedBlocks.addAll(blocksToBreak);
                    Bukkit.getScheduler().runTask(
                            SimpleVeinMining.getInstance(),
                            () -> breakBlocks(blocksToBreak,player)
                    );
                }
        );
    }

    /**
     * Match the blocks that are considered a "group".<br/>
     * ie: Coal Group -&gt COAL_ORE, DEEPSLATE_COAL_ORE
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
            if (CoreProtectHook.getInstance().getCoreProtect() != null) LogBrokenBlocks.logBrokenBlock(player, location);

            if (ConfigHandler.getInstance().isPreventBreakingTool() && getRemainingDurability(player.getInventory().getItemInMainHand()) <= 5) {
                veinMinedBlocks.remove(location);
                minimumDurabilityReached = true;
            }
            else player.breakBlock(location.getBlock());
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

    private boolean hasRequiredLore(ItemStack item) {
        Component loreComponent = SimpleVeinMining.getMiniMessage().deserialize(ConfigHandler.getInstance().getLoreString());
        loreComponent = loreComponent.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
        List<Component> lore = item.lore();
        if (lore == null || lore.isEmpty()) {
            return false;
        }
        for (Component component : lore) {
            if (component.equals(loreComponent)) return true;
            List<Component> childComponents = component.children();
            if (childComponents.isEmpty()) continue;
            for (Component childComponent : childComponents) {
                if (childComponent.equals(loreComponent)) return true;
            }
        }
        return false;
    }

    private boolean hasRequiredItemModel(ItemStack item) {
        Set<Key> allowedItemModels = ConfigHandler.getInstance().getRequiredItemModels();
        if (allowedItemModels.isEmpty()) return true;
        Key modelKey = item.getData(DataComponentTypes.ITEM_MODEL);
        return allowedItemModels.contains(modelKey);
    }
}
