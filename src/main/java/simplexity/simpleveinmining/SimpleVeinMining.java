package simplexity.simpleveinmining;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.youhavetrouble.yardwatch.Protection;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;
import simplexity.simpleveinmining.commands.ReloadCommand;
import simplexity.simpleveinmining.commands.VeinMiningToggle;
import simplexity.simpleveinmining.config.ConfigHandler;
import simplexity.simpleveinmining.config.Constants;
import simplexity.simpleveinmining.config.LocaleHandler;
import simplexity.simpleveinmining.hooks.worldguard.WorldGuardHook;
import simplexity.simpleveinmining.listeners.MiningListener;
import simplexity.simpleveinmining.listeners.YellAtServerOwnerListener;

@SuppressWarnings("UnstableApiUsage")
public final class SimpleVeinMining extends JavaPlugin {

    private static SimpleVeinMining instance;
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    private boolean isWorldGuardEnabled;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
        ConfigHandler.getInstance().loadConfigValues();
        LocaleHandler.getInstance().loadLocale();
        isWorldGuardEnabled = hasWorldGuard();
        if (isWorldGuardEnabled) WorldGuardHook.getInstance().registerWorldGuardFlag(getSLF4JLogger());
        registerListeners();
        registerCommands();
        registerPermissions();
    }

    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents(new MiningListener(), this);
        this.getServer().getPluginManager().registerEvents(new YellAtServerOwnerListener(), this);
    }

    private void registerCommands() {
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(ReloadCommand.createCommand());
            commands.registrar().register(VeinMiningToggle.createCommand());
        });
    }

    private void registerPermissions() {
        getServer().getPluginManager().addPermission(Constants.MINING_PERMISSION);
        getServer().getPluginManager().addPermission(Constants.TOGGLE_PERMISSION);
        getServer().getPluginManager().addPermission(Constants.RELOAD_PERMISSION);
    }

    public static SimpleVeinMining getInstance() {
        return instance;
    }

    public static MiniMessage getMiniMessage() {
        return miniMessage;
    }

    public boolean hasYardWatchProvider() {
        boolean classExists = false;
        try {
            Class.forName("me.youhavetrouble.yardwatch.Protection");
            classExists = true;
        } catch (ClassNotFoundException e) {
            // ignore
        }
        return classExists && this.getServer().getServicesManager().isProvidedFor(Protection.class);
    }

    public boolean hasWorldGuard() {
        boolean classExists = false;
        try {
            Class.forName("com.sk89q.worldguard.protection.flags.registry.FlagRegistry");
            classExists = true;

        } catch (ClassNotFoundException e) {
            getSLF4JLogger().warn("WorldGuard not found. Vein mining will not respect WorldGuard regions.");
        }
        return classExists;
    }

    public boolean isWorldGuardEnabled() {
        return isWorldGuardEnabled;
    }

    public void setWorldGuardEnabled(boolean enabled) {
        isWorldGuardEnabled = enabled;
    }
}
