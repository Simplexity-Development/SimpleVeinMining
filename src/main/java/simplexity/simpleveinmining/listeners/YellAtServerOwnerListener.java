package simplexity.simpleveinmining.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import simplexity.simpleveinmining.SimpleVeinMining;
import simplexity.simpleveinmining.config.ConfigHandler;

public class YellAtServerOwnerListener implements Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    public void onServerLoadEvent(ServerLoadEvent serverLoadEvent) {
        if (!SimpleVeinMining.getInstance().hasYardWatchProvider() && !ConfigHandler.getInstance().isIgnoreProtections()) {
            SimpleVeinMining.getInstance().getSLF4JLogger().warn("You currently do not have YardWatch. Some protection functionality may not work as expected.");
        }
    }
}
