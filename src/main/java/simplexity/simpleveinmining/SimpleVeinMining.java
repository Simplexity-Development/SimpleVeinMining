package simplexity.simpleveinmining;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleVeinMining extends JavaPlugin {
    
    private static SimpleVeinMining instance;
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    
    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        ConfigHandler.getInstance().loadConfigValues();
        LocaleHandler.getInstance().loadLocale();
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
    
}
