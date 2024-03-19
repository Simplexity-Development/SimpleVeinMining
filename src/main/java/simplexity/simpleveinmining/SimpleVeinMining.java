package simplexity.simpleveinmining;

import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleVeinMining extends JavaPlugin {
    private static SimpleVeinMining instance;
    
    @Override
    public void onEnable() {
        instance = this;

    }
    public static SimpleVeinMining getInstance() {
        return instance;
    }
    
}
