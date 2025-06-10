package simplexity.simpleveinmining;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import me.youhavetrouble.yardwatch.Protection;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;
import simplexity.simpleveinmining.commands.ReloadCommand;
import simplexity.simpleveinmining.commands.VeinMiningToggle;
import simplexity.simpleveinmining.config.ConfigHandler;
import simplexity.simpleveinmining.config.LocaleHandler;
import simplexity.simpleveinmining.listeners.MiningListener;
import simplexity.simpleveinmining.listeners.YellAtServerOwnerListener;

import java.util.Objects;

public final class SimpleVeinMining extends JavaPlugin {

    private static SimpleVeinMining instance;
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private StateFlag veinMiningFlag;
    private boolean isWorldGuardEnabled = false;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
        ConfigHandler.getInstance().loadConfigValues();
        LocaleHandler.getInstance().loadLocale();
        registerWorldGuardFlag();
        registerListeners();
        registerCommands();
    }

    private void registerWorldGuardFlag() {
        if (getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            try {
                FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
                StateFlag existingFlag = (StateFlag) registry.get("vein-mining");
                if (existingFlag == null) {
                    veinMiningFlag = new StateFlag("vein-mining", true);
                    registry.register(veinMiningFlag);
                    getLogger().info("Successfully registered 'vein-mining' flag with WorldGuard.");
                } else {
                    veinMiningFlag = existingFlag;
                    getLogger().info("Using existing 'vein-mining' flag from WorldGuard.");
                }
                isWorldGuardEnabled = true;
            } catch (Exception e) {
                getLogger().warning("Failed to register 'vein-mining' flag with WorldGuard: " + e.getMessage());
                isWorldGuardEnabled = false;
            }
        } else {
            getLogger().warning("WorldGuard not found. Vein mining will not respect WorldGuard regions.");
            isWorldGuardEnabled = false;
        }
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

    public boolean isWorldGuardEnabled() {
        return isWorldGuardEnabled;
    }

    public StateFlag getVeinMiningFlag() {
        return veinMiningFlag;
    }
}