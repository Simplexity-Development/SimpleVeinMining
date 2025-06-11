package simplexity.simpleveinmining;

import me.youhavetrouble.yardwatch.Protection;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;
import simplexity.simpleveinmining.commands.ReloadCommand;
import simplexity.simpleveinmining.commands.VeinMiningToggle;
import simplexity.simpleveinmining.config.ConfigHandler;
import simplexity.simpleveinmining.config.LocaleHandler;
import simplexity.simpleveinmining.hooks.worldguard.WorldGuardHook;
import simplexity.simpleveinmining.listeners.MiningListener;
import simplexity.simpleveinmining.listeners.YellAtServerOwnerListener;

import java.util.Objects;

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
    }

    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents(new MiningListener(), this);
        this.getServer().getPluginManager().registerEvents(new YellAtServerOwnerListener(), this);
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
