package simplexity.simpleveinmining;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class ConfigHandler {
    private ConfigHandler(){}
    private static ConfigHandler instance;
    public static ConfigHandler getInstance(){
        if (instance == null) instance = new ConfigHandler();
        return instance;
    }
    private final Set<Material> blockList = new HashSet<>();
    private final HashMap<String, Set<Material>> groupList = new HashMap<>();
    private int maxBlocksToScan;
    private final Logger logger = SimpleVeinMining.getInstance().getLogger();
    private boolean isBlacklist, onlySameType, worksInCreative, runEffects, dropXP, damageTool, preventBreakingTool, respectUnbreakingEnchant, requireProperTool;
    
    public void loadConfigValues(){
        SimpleVeinMining.getInstance().reloadConfig();
        FileConfiguration config = SimpleVeinMining.getInstance().getConfig();
        blockList.clear();
        ArrayList<Material> configuredMaterials = loadConfiguredMaterials(config);
        blockList.addAll(configuredMaterials);
        isBlacklist = config.getBoolean("allowed-blocks-is-blacklist", false);
        onlySameType = config.getBoolean("same-type", true);
        if (onlySameType) {
            HashMap<String, Set<Material>> configuredGroups = loadConfiguredGroups(config);
            if (configuredGroups.isEmpty()) {
                onlySameType = false;
            } else {
                groupList.clear();
                groupList.putAll(configuredGroups);
            }
        }
        worksInCreative = config.getBoolean("works-in-creative", false);
        runEffects = config.getBoolean("run-effects", false);
        dropXP = config.getBoolean("drop-xp", true);
        damageTool = config.getBoolean("damage-tool.enabled", true);
        preventBreakingTool = config.getBoolean("damage-tool.prevent-breaking", true);
        respectUnbreakingEnchant = config.getBoolean("damage-tool.respect-unbreaking-enchant", true);
        requireProperTool = config.getBoolean("require-proper-tool", true);
        maxBlocksToScan = config.getInt("max-blocks-to-scan", 216);
        
    }
    private ArrayList<Material> loadConfiguredMaterials(FileConfiguration config){
        List<String> materialStrings = config.getStringList("allowed-blocks");
        return checkMaterials(materialStrings, "allowed-blocks");
    }
    private HashMap<String, Set<Material>> loadConfiguredGroups(FileConfiguration config){
        ConfigurationSection veinTypes = config.getConfigurationSection("types");
        HashMap<String, Set<Material>> configuredGroups = new HashMap<>();
        if (veinTypes == null) {
            logger.severe("Issue loading vein types, please check your config!");
            return configuredGroups;
        }
        Set<String> typeKeys = veinTypes.getKeys(false);
        for (String key : typeKeys) {
            List<String> materialStrings = veinTypes.getStringList(key);
            Set<Material> materialSet = new HashSet<>(checkMaterials(materialStrings, ("types." + key)));
            if (materialSet.isEmpty()) {
                logger.warning("types." + key + " is empty, and will not be added to the types configuration. Please check your config file if you believe this is incorrect");
                continue;
            }
            configuredGroups.put(key, materialSet);
        }
        if (configuredGroups.isEmpty()) {
            logger.warning("No block types were configured. The plugin will not use group functionality until this is resolved.");
        }
        return configuredGroups;
    
    }
    
    private ArrayList<Material> checkMaterials(List<String> materialStrings, String configSectionForError){
        ArrayList<Material> configuredMaterials = new ArrayList<>();
        for (String string : materialStrings) {
            Material matFromString = Material.matchMaterial(string);
            if (matFromString == null) {
                logger.warning(string + " is not a valid material. Please check your configuration under " + configSectionForError);
                continue;
            }
            configuredMaterials.add(matFromString);
        }
        return configuredMaterials;
    }
    public Set<Material> getBlockList(){
        return blockList;
    }
    
    public boolean isBlacklist() {
        return isBlacklist;
    }
    
    public boolean isOnlySameType() {
        return onlySameType;
    }
    
    public HashMap<String, Set<Material>> getGroupList(){
        return groupList;
    }
    
    public boolean isDropXP() {
        return dropXP;
    }
    
    public boolean isDamageTool() {
        return damageTool;
    }
    
    public boolean isPreventBreakingTool() {
        return preventBreakingTool;
    }
    
    public boolean isRequireProperTool() {
        return requireProperTool;
    }
    
    public int getMaxBlocksToScan() {
        return maxBlocksToScan;
    }
    
    public boolean isRunEffects() {
        return runEffects;
    }
    
    public boolean isWorksInCreative() {
        return worksInCreative;
    }
    
    public boolean isRespectUnbreakingEnchant() {
        return respectUnbreakingEnchant;
    }
}
