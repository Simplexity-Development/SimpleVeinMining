package simplexity.simpleveinmining.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import simplexity.simpleveinmining.SimpleVeinMining;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class LocaleHandler {

    private static LocaleHandler instance;
    private final String fileName = "locale.yml";
    private final File localeFile = new File(SimpleVeinMining.getInstance().getDataFolder(), fileName);
    private final FileConfiguration localeConfig = new YamlConfiguration();
    private final Logger logger = SimpleVeinMining.getInstance().getLogger();
    private String almostBroken, toggleEnabled, toggleDisabled, configReloaded,
            worldguardRegionDisabled, issueWithProtectionCheck;

    private LocaleHandler() {
        if (!localeFile.exists()) {
            SimpleVeinMining.getInstance().saveResource(fileName, false);
        }
    }

    public static LocaleHandler getInstance() {
        if (instance == null) instance = new LocaleHandler();
        return instance;
    }

    public FileConfiguration getLocaleConfig() {
        return localeConfig;
    }

    public void loadLocale() {
        try {
            localeConfig.load(localeFile);
        } catch (IOException | InvalidConfigurationException e) {
            logger.severe("Issue loading locale.yml");
            e.printStackTrace();
        }
        almostBroken = localeConfig.getString("errors.not-enough-durability", "<dark_red>[<gold>!!</gold>]</dark_red> <yellow>Your tool is nearly broken, vein mining has been prevented.</yellow>");
        toggleEnabled = localeConfig.getString("messages.toggle.enabled", "<green>Vein mining has been enabled</green>");
        toggleDisabled = localeConfig.getString("messages.toggle.disabled", "<gray>Vein mining is now disabled</gray>");
        configReloaded = localeConfig.getString("messages.config-reloaded", "<gold>Simple Vein Mining Config has been reloaded</gold>");
        worldguardRegionDisabled = localeConfig.getString("errors.world-guard-region-disabled", "<red>Vein mining is disabled in this region!</red>");
        issueWithProtectionCheck = localeConfig.getString("errors.issue-with-protection-check", "<red>An issue occurred trying to check if you have the ability to break blocks here. Please tell an administrator if this continues to occur</red>");
    }

    public String getAlmostBroken() {
        return almostBroken;
    }

    public String getToggleEnabled() {
        return toggleEnabled;
    }

    public String getToggleDisabled() {
        return toggleDisabled;
    }

    public String getConfigReloaded() {
        return configReloaded;
    }

    public String getWorldguardRegionDisabled() {
        return worldguardRegionDisabled;
    }

    public String getIssueWithProtectionCheck() {
        return issueWithProtectionCheck;
    }
}
