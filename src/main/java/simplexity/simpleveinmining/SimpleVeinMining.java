package simplexity.simpleveinmining;

import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleVeinMining extends JavaPlugin {
    private static SimpleVeinMining instance;
    
    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        ConfigHandler.getInstance().loadConfigValues();
        this.getServer().getPluginManager().registerEvents(new MiningListener(), this);
        this.getCommand("svmreload").setExecutor(new ReloadCommand());
    }
    public static SimpleVeinMining getInstance() {
        return instance;
    }
    
}
