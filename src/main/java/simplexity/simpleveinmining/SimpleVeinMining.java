package simplexity.simpleveinmining;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;
import simplexity.simpleveinmining.commands.ReloadCommand;
import simplexity.simpleveinmining.commands.VeinMiningToggle;
import simplexity.simpleveinmining.config.ConfigHandler;
import simplexity.simpleveinmining.config.LocaleHandler;

public final class SimpleVeinMining extends JavaPlugin {
    
    private static SimpleVeinMining instance;
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private boolean hasGP = false;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        ConfigHandler.getInstance().loadConfigValues();
        LocaleHandler.getInstance().loadLocale();
        try {
            Class.forName("me.ryanhamshire.GriefPrevention.GriefPrevention");
            hasGP = true;
        } catch (ClassNotFoundException e) {
            hasGP = false;
        }
        this.getServer().getPluginManager().registerEvents(new MiningListener(), this);
        this.getCommand("vmreload").setExecutor(new ReloadCommand());
        this.getCommand("vmtoggle").setExecutor(new VeinMiningToggle());
    }
    
    public static SimpleVeinMining getInstance() {
        return instance;
    }
    
    public static MiniMessage getMiniMessage() {
        return miniMessage;
    }

    public boolean isHasGP() {
        return hasGP;
    }
}
