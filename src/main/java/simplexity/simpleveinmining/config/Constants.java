package simplexity.simpleveinmining.config;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class Constants {
    public static Permission RELOAD_PERMISSION = new Permission("veinmining.reload", "Allows the user to reload the plugin", PermissionDefault.OP);
    public static Permission TOGGLE_PERMISSION = new Permission("veinmining.toggle", "Allows player to toggle vein mining off/on for themselves", PermissionDefault.OP);
    public static Permission MINING_PERMISSION = new Permission("veinmining.mining", "Allows the player to use the vein mining functionality", PermissionDefault.OP);
}
