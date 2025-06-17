package simplexity.simpleveinmining.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.NamespacedKey;

import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import simplexity.simpleveinmining.SimpleVeinMining;
import simplexity.simpleveinmining.config.Constants;
import simplexity.simpleveinmining.config.LocaleHandler;

@SuppressWarnings("UnstableApiUsage")
public class VeinMiningToggle {

    public static final NamespacedKey toggleKey = new NamespacedKey(SimpleVeinMining.getInstance(), "toggle");

    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("vmtoggle")
                .requires(css -> css.getSender().hasPermission(Constants.TOGGLE_PERMISSION) && css.getSender() instanceof Player)
                .executes(ctx -> {
                    Player player = (Player) ctx.getSource().getSender();
                    PersistentDataContainer playerPDC = player.getPersistentDataContainer();
                    boolean currentSetting = playerPDC.getOrDefault(toggleKey, PersistentDataType.BOOLEAN, true);
                    playerPDC.set(toggleKey, PersistentDataType.BOOLEAN, !currentSetting);
                    if (!currentSetting) {
                        player.sendRichMessage(LocaleHandler.getInstance().getToggleEnabled());
                    } else {
                        player.sendRichMessage(LocaleHandler.getInstance().getToggleDisabled());
                    }
                    return Command.SINGLE_SUCCESS;
                }).build();

    }

}
