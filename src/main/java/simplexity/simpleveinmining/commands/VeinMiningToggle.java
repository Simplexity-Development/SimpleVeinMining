package simplexity.simpleveinmining.commands;

import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import simplexity.simpleveinmining.SimpleVeinMining;
import simplexity.simpleveinmining.config.LocaleHandler;

public class VeinMiningToggle implements CommandExecutor {
    
    public static final NamespacedKey toggleKey = new NamespacedKey(SimpleVeinMining.getInstance(), "toggle");
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendRichMessage(LocaleHandler.getInstance().getOnlyPlayer());
            return false;
        }
        PersistentDataContainer playerPDC = player.getPersistentDataContainer();
        boolean currentSetting = playerPDC.getOrDefault(toggleKey, PersistentDataType.BOOLEAN, true);
        playerPDC.set(toggleKey, PersistentDataType.BOOLEAN, !currentSetting);
        if (!currentSetting) {
            player.sendRichMessage(LocaleHandler.getInstance().getToggleEnabled());
        } else {
            player.sendRichMessage(LocaleHandler.getInstance().getToggleDisabled());
        }
        return true;
    }
    
}
