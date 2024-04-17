package simplexity.simpleveinmining;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;
import simplexity.simpleveinmining.commands.ReloadCommand;
import simplexity.simpleveinmining.commands.VeinMiningToggle;
import simplexity.simpleveinmining.config.ConfigHandler;
import simplexity.simpleveinmining.config.LocaleHandler;

import java.util.Objects;
import java.util.logging.Logger;

public final class SimpleVeinMining extends JavaPlugin {

    private static SimpleVeinMining instance;
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private boolean yardWatchEnabled = false;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        ConfigHandler.getInstance().loadConfigValues();
        LocaleHandler.getInstance().loadLocale();
        if ((this.getServer().getPluginManager().getPlugin("YardWatch") == null) && !ConfigHandler.getInstance().isIgnoreProtections()) {
            String prefix = "[SimpleVeinMining] ";
            Logger logger = instance.getLogger();
            logger.severe(prefix + "YardWatch plugin was not found. ");
            logger.severe(prefix + "YardWatch is required for claims to be respected. ");
            logger.severe(prefix + "Without it, players will be able to vein-mine blocks inside claims they should not be able to access.");
            logger.severe(prefix + "If you do not use protection plugins or would like claims to be ignored, please set 'ignore-protections' to true in config.yml");
        } else {
            yardWatchEnabled = true;
        }
        registerListeners();
        registerCommands();
    }

    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents(new MiningListener(), this);
    }

    private void registerCommands() {
        Objects.requireNonNull(this.getCommand("vmreload")).setExecutor(new ReloadCommand());
        Objects.requireNonNull(this.getCommand("vmtoggle")).setExecutor(new VeinMiningToggle());
    }

    public static SimpleVeinMining getInstance() {
        return instance;
    }

    public static MiniMessage getMiniMessage() {
        return miniMessage;
    }

    public boolean isYardWatchEnabled() {
        return yardWatchEnabled;
    }
}
