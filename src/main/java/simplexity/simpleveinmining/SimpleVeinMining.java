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
    private boolean hasYardWatch = false;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        ConfigHandler.getInstance().loadConfigValues();
        LocaleHandler.getInstance().loadLocale();
        if ((this.getServer().getPluginManager().getPlugin("YardWatch") == null) && !ConfigHandler.getInstance().isIgnoreProtections()){
            String prefix = "[Simple Vein Mining] ";
            this.getServer().getLogger().severe(prefix + "YardWatch plugin was not found. ");
            this.getServer().getLogger().severe(prefix + "YardWatch is required for claims to be respected. ");
            this.getServer().getLogger().severe(prefix + "Players will be able to vein-mine blocks inside claims they cannot access without the YardWatch plugin. ");
            this.getServer().getLogger().severe(prefix + "If you do not use protection plugins or would like claims to be ignored, please set 'ignore-protections' to true in config.yml");
        } else {
            hasYardWatch = true;
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

    public boolean isHasYardWatch() {
        return hasYardWatch;
    }
}
