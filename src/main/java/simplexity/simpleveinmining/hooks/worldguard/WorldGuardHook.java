package simplexity.simpleveinmining.hooks.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import simplexity.simpleveinmining.SimpleVeinMining;
import simplexity.simpleveinmining.config.LocaleHandler;

import java.util.Objects;

public class WorldGuardHook {

    private static WorldGuardHook instance;

    private WorldGuardHook() {
    }

    public static WorldGuardHook getInstance() {
        if (instance == null) instance = new WorldGuardHook();
        return instance;
    }

    private StateFlag veinMiningFlag;

    public void registerWorldGuardFlag(Logger logger) {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            StateFlag flag = new StateFlag("vein-mining", true);
            registry.register(flag);
            veinMiningFlag = flag;
        } catch (FlagConflictException e) {
            Flag<?> existing = registry.get("vein-mining");
            if (existing instanceof StateFlag) {
                veinMiningFlag = (StateFlag) existing;
            } else {
                logger.warn("Failed to register 'vein-mining' flag with WorldGuard: {}", e.getMessage(), e);
                SimpleVeinMining.getInstance().setWorldGuardEnabled(false);
            }
        }
    }

    public boolean canBreakBlockInRegion(@NotNull Player player, @NotNull Location blockLocation) {
        try {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            World world = BukkitAdapter.adapt(blockLocation.getWorld());
            BlockVector3 location = BlockVector3.at(blockLocation.getX(), blockLocation.getY(), blockLocation.getZ());
            ApplicableRegionSet regions = Objects.requireNonNull(container.get(world)).getApplicableRegions(location);
            if (!regions.testState(null, WorldGuardHook.getInstance().getVeinMiningFlag())) {
                return false;
            }
        } catch (Exception e) {
            SimpleVeinMining.getInstance().getSLF4JLogger().warn("Error checking WorldGuard regions: {}", e.getMessage(), e);
            player.sendRichMessage(LocaleHandler.getInstance().getIssueWithProtectionCheck());
            return false;
        }
        return true;
    }

    public StateFlag getVeinMiningFlag() {
        return veinMiningFlag;
    }
}
