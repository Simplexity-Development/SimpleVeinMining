package simplexity.simpleveinmining.logic;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import simplexity.simpleveinmining.SimpleVeinMining;
import simplexity.simpleveinmining.commands.VeinMiningToggle;
import simplexity.simpleveinmining.config.ConfigHandler;

import java.util.List;
import java.util.Set;

@SuppressWarnings({"RedundantIfStatement", "UnstableApiUsage"})
public class ValidityChecks {

    public static boolean playerCanUseVeinMiner(Player player) {
        if (!player.hasPermission("veinmining.mining")) return false;
        if (!player.getPersistentDataContainer().getOrDefault(VeinMiningToggle.toggleKey, PersistentDataType.BOOLEAN, true))
            return false;
        if (ConfigHandler.getInstance().doesCrouchPreventVeinMining() && player.isSneaking()) return false;
        if (player.getGameMode().equals(GameMode.CREATIVE) && !ConfigHandler.getInstance().isWorksInCreative())
            return false;
        return true;
    }

    public static boolean toolIsValidForVeinMiner(ItemStack itemUsed, Block blockBroken){
        if (ConfigHandler.getInstance().requiresItemModel() && !hasRequiredItemModel(itemUsed)) return false;
        if (ConfigHandler.getInstance().isRequireLore() && !hasRequiredLore(itemUsed)) return false;
        if (!blockBroken.isPreferredTool(itemUsed) && ConfigHandler.getInstance().isRequireProperTool()) return false;
        return true;
    }

    public static boolean blockIsValidToVeinMine(Block blockBroken, Set<Material> blockSet){
        Material blockMaterial = blockBroken.getType();
        if (ConfigHandler.getInstance().isBlacklist() && blockSet.contains(blockMaterial)) return false;
        if (!ConfigHandler.getInstance().isBlacklist() && !blockSet.contains(blockMaterial)) return false;
        return true;
    }

    private static boolean hasRequiredLore(ItemStack item) {
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

    private static boolean hasRequiredItemModel(ItemStack item) {
        Set<Key> allowedItemModels = ConfigHandler.getInstance().getRequiredItemModels();
        if (allowedItemModels.isEmpty()) return true;
        Key modelKey = item.getData(DataComponentTypes.ITEM_MODEL);
        return allowedItemModels.contains(modelKey);
    }
}
