package simplexity.simpleveinmining;

import me.youhavetrouble.yardwatch.Protection;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
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
        checkForYardWatch();
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

    private void checkForYardWatch(){
        boolean classExists = true;
        try {
            Class.forName("me.youhavetrouble.yardwatch.Protection");
        } catch (ClassNotFoundException e) {
            classExists = false;
        }
        if (!classExists && !ConfigHandler.getInstance().isIgnoreProtections()) {
            yellAtServerOwner();
        }
        if (classExists) {
            yardWatchEnabled = true;
        }
    }

    private void yellAtServerOwner() {
        Logger logger = instance.getLogger();
        logger.severe("YardWatch API was not found");
        logger.warning("For a protection plugin to integrate properly, it needs to implement YardWatch API");
        logger.warning("If you have a protection plugin that does not implement that API, please check if the YardWatch plugin has a temporary implementation to cover that plugin");
        logger.warning("If it does, you can download the YardWatch plugin for integration until that plugin adds support for YardWatch");
        logger.warning("https://github.com/YouHaveTrouble/YardWatch");
        logger.warning("If you do not have protection plugins, or do not want to integrate SimpleVeinMining with protection plugins, please set 'ignore-protections' in config.yml to 'true'");
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
