package com.bluemarien.everythingplugin;

import com.bluemarien.everythingplugin.backend.WarpDatabase;
import com.bluemarien.everythingplugin.commands.*;
import com.bluemarien.everythingplugin.backend.XpBankDatabase;

import net.milkbowl.vault.permission.Permission;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This class represents the EverythingPlugin plugin running on a Spigot server. The plugin's
 * description file is named "plugin.yml".
 *
 * @author Anthony Farina
 * @version 2020.06.29
 */
public final class EverythingPlugin extends JavaPlugin {

    /**
     * Declare plugin's logger, description file "plugin.yml", and folder path.
     */
    private static Logger logger = null;
    private PluginDescriptionFile pdFile = null;
    private static final String pluginFolderPath = "./plugins/EverythingPlugin";

    /**
     * Declare xp bank fields.
     */
    private static XpBankDatabase xpBankDB = null;
    private static final String xpBankDBName = "XPBankDatabase.db";

    /**
     * Declare warp fields.
     */
    private static WarpDatabase warpDB = null;
    private static final String warpDBName = "warps.yml";

    /**
     * Declare a reference to the permissions manager for the plugin.
     */
    private static Permission perms = null;


    /**
     * Properly enable the plugin.
     */
    @Override
    public void onEnable() {
        // Load plugin description file (plugin.yml) and initialize the logger.
        pdFile = getDescription();
        logger = getLogger();

        // Load the plugin's commands.
        loadCommands();

        // Check if Vault is installed on the server.
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            logger.info("Vault not found! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Check if there is a valid permissions plugin installed.
        if (!setupPermissions()) {
            logger.info("Could not get a valid permissions plugin from Vault! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Check if the EverythingPlugin directory exists in the "plugins" directory.
        if (!Files.isDirectory(Paths.get(pluginFolderPath))) {
            // Try to make the EverythingPlugin directory in the "plugins" directory.
            try {
                Files.createDirectory(Paths.get(pluginFolderPath));
            }
            // Something went wrong creating the EverythingPlugin directory.
            catch (IOException e) {
                logger.info("An error occurred while creating the EverythingPlugin directory! " +
                        "Disabling plugin...");
                logger.info(e.getMessage());
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
        }

        // Connect to or create a new XP bank database.
        xpBankDB = new XpBankDatabase();

        // Connect to or create a new warp database.
        warpDB = new WarpDatabase();

        // We enabled the plugin successfully.
        logger.info(pdFile.getName() + " v" + pdFile.getVersion() + " has been successfully " +
                "enabled!");
    }

    /**
     * Properly disable the plugin.
     */
    @Override
    public void onDisable() {
        // Close the XP bank database properly.
        if (xpBankDB != null) {
            xpBankDB.closeXPBankDatabase();
        }

        // We disabled the plugin successfully.
        logger.info(pdFile.getName() + " v" + pdFile.getVersion() + " has been successfully " +
                "disabled!");
    }

    /**
     * Returns the permission API object to check players for permissions.
     *
     * @return The permission object associated with permissions for the plugin.
     */
    public static Permission getPermissions() {
        return perms;
    }

    /**
     * Returns the xp bank database object.
     *
     * @return The xp bank database object.
     */
    public static XpBankDatabase getXpBankDatabase() {
        return xpBankDB;
    }

    /**
     * Returns this plugin's logger for output to the server console.
     *
     * @return This plugin's logger object.
     */
    public static Logger getEPLogger() {
        return logger;
    }

    /**
     * Returns the plugin's folder path in the "plugins" directory.
     *
     * @return The plugin's folder path in the "plugins" directory.
     */
    public static String getPluginFolderPath() {
        return pluginFolderPath;
    }

    /**
     * Returns the name of the xp bank database.
     *
     * @return The name of the xp bank database.
     */
    public static String getXpBankDBName() {
        return xpBankDBName;
    }

    /**
     * Returns the name of the warp database.
     *
     * @return The name of the warp database.
     */
    public static String getWarpDBName() { return warpDBName; }

    /**
     * Register the plugin's commands with Spigot. Don't forget to add them to the plugin.yml file
     * after adding them here!
     */
    private void loadCommands() {
        // Set the executor for each command in the plugin description file.
        logger.info("Loading commands...");

        getCommand("heal").setExecutor(new Heal());
        getCommand("feed").setExecutor(new Feed());
        getCommand("xpshare").setExecutor(new Xpshare());
        getCommand("xpbank").setExecutor(new Xpbank());
        getCommand("warp").setExecutor(new Warp());

        logger.info("Commands loaded successfully!");
    }

    /**
     * Sets up the permissions manager from Vault.
     *
     * @return True if there is a valid permissions manager, false otherwise.
     */
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp =
                getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    /**
     * Loads the
     */
    private void loadConfiguration() {
        //See "Creating you're defaults"
        getConfig().options().copyDefaults(true); // NOTE: You do not have to use "plugin." if the class extends the java plugin
        //Save the config whenever you manipulate it
        saveConfig();
    }
}
