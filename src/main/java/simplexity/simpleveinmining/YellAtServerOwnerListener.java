package simplexity.simpleveinmining;

import java.util.logging.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import simplexity.simpleveinmining.config.ConfigHandler;

public class YellAtServerOwnerListener implements Listener {
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onServerLoadEvent(ServerLoadEvent serverLoadEvent) {
        if (!SimpleVeinMining.getInstance().hasYardWatchProvider() && !ConfigHandler.getInstance().isIgnoreProtections()) {
            Logger logger = SimpleVeinMining.getInstance().getLogger();
            logger.severe("A plugin implementing the YardWatchAPI service was not found");
            logger.warning("For a protection plugin to integrate properly, it needs to implement YardWatchAPI");
            logger.warning("If you have a protection plugin that does not implement that API, please check if the YardWatch plugin has a temporary implementation to cover that plugin");
            logger.warning("If it does, you can download the YardWatch plugin for integration until that plugin adds support for YardWatch");
            logger.warning("https://github.com/YouHaveTrouble/YardWatch");
            logger.warning("If you do not have protection plugins, or do not want to integrate SimpleVeinMining with protection plugins, please set 'ignore-protections' in config.yml to 'true'");
        }
    }
}
