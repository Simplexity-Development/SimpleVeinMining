package simplexity.simpleveinmining.hooks.yardwatch;

import me.youhavetrouble.yardwatch.Protection;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import simplexity.simpleveinmining.SimpleVeinMining;

import java.util.Collection;

public class YardWatchHook {
    public static boolean canBreakBlock(Player player, Block block){
        ServicesManager servicesManager = SimpleVeinMining.getInstance().getServer().getServicesManager();
        Collection<RegisteredServiceProvider<Protection>> protections = servicesManager.getRegistrations(Protection.class);
        for (RegisteredServiceProvider<Protection> protection : protections) {
            if (protection.getProvider().canBreakBlock(player, block.getState(true))) continue;
            return false;
        }
        return true;
    }
}
