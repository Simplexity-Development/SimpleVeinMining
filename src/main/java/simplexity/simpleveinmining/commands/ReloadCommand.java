package simplexity.simpleveinmining.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import simplexity.simpleveinmining.config.ConfigHandler;
import simplexity.simpleveinmining.config.Constants;
import simplexity.simpleveinmining.config.LocaleHandler;

@SuppressWarnings("UnstableApiUsage")
public class ReloadCommand {

    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("vmreload")
                .requires(css -> css.getSender().hasPermission(Constants.RELOAD_PERMISSION))
                .executes(ctx -> {
                    CommandSender sender = ctx.getSource().getSender();
                    ConfigHandler.getInstance().loadConfigValues();
                    LocaleHandler.getInstance().loadLocale();
                    sender.sendRichMessage(LocaleHandler.getInstance().getConfigReloaded());
                    return Command.SINGLE_SUCCESS;
                }).build();
    }
}
