package com.bluemarien.everythingplugin.backend;

import com.bluemarien.everythingplugin.EverythingPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


/**
 * This class represents a warp database that this plugin uses to store data about the warps on the
 * server. It uses the YAML configuration object from the Spigot API to handle database
 * functionality.
 *
 * @author Anthony Farina
 * @version 2020.07.12
 */
public class WarpDatabase {

    /**
     * Declare and initialize warp database fields.
     */
    private final String warpDatabasePath =
            EverythingPlugin.getPluginFolderPath() + "/" + EverythingPlugin.getWarpDBName();
    private FileConfiguration warpDatabase = null;
    private File warpDatabaseFile = null;

    /**
     * Loads an existing warp database or, if necessary, creates a new database if one doesn't
     * exist.
     */
    public WarpDatabase() {
        // Make the file object to reference the warp database.
        warpDatabaseFile = new File(warpDatabasePath);

        // Check if the warp database exists.
        if (!warpDatabaseExists()) {
            // Create and initialize a new warp database.
            createWarpDatabase();
        }

        // Load the warp database.
        warpDatabase = YamlConfiguration.loadConfiguration(warpDatabaseFile);
    }

    /**
     * Inserts a new warp location with the provided warp name and location into the warp database.
     *
     * @param warp     The location of the warp to save.
     * @param warpName The name of the warp.
     */
    public void insertWarp(Location warp, String warpName) {
        // Insert the warp location into the warp database labeled with the provided warp name.
        warpDatabase.set("warps." + warpName + ".world", warp.getWorld().toString());
        warpDatabase.set("warps." + warpName + ".x", warp.getX());
        warpDatabase.set("warps." + warpName + ".y", warp.getY());
        warpDatabase.set("warps." + warpName + ".z", warp.getZ());
        warpDatabase.set("warps." + warpName + ".yaw", warp.getYaw());
        warpDatabase.set("warps." + warpName + ".pitch", warp.getPitch());

        // Save the warp to the database.
        saveWarpDatabase();
    }

    /**
     * Returns the location of a stored warp, if it exists in the database.
     *
     * @param warpName The name of the warp to return the location for.
     *
     * @return The location of a stored warp or null if the warp doesn't exist in the database.
     */
    public Location getWarp(String warpName) {
        // Check if the warp exists in the database.
        if (warpDatabase.get("warps." + warpName) == null) {
            // Return null since the warp doesn't exist in the database.
            return null;
        }

        // Return the location of the warp.
        return new Location(
                Bukkit.getWorld(warpDatabase.getString("warps." + warpName + ".world")),
                warpDatabase.getDouble("warps." + warpName + ".x"),
                warpDatabase.getDouble("warps." + warpName + ".y"),
                warpDatabase.getDouble("warps." + warpName + ".z"),
                Float.parseFloat(warpDatabase.getString("warps." + warpName + ".yaw")),
                Float.parseFloat(warpDatabase.getString("warps." + warpName + ".pitch")));
    }

    /**
     * Removes a warp from the warp database. Returns true if the warp was removed successfully, or
     * false if the warp doesn't exist.
     *
     * @param warpName The warp to remove from the database.
     *
     * @return True if the warp was removed successfully, false if the warp doesn't exist in the
     * database.
     */
    public boolean removeWarp(String warpName) {
        // Check if the warp exists in the database.
        if (warpDatabase.get("warps." + warpName) == null) {
            return false;
        }

        // Delete the warp from the warp database.
        warpDatabase.set("warps." + warpName, null);
        saveWarpDatabase();
        return true;
    }

    /**
     * Returns a set of Strings containing all the warp names stored in the database.
     *
     * @param section The section of the YAML file to list.
     *
     * @return A set of Strings containing all the warp names stored in the database.
     */
    public Set<String> listWarps(String section) {
        return warpDatabase.getConfigurationSection(section).getKeys(false);
    }

    /**
     * Saves the warp database.
     *
     * @return True if the warp database was saved successfully, false otherwise.
     */
    public boolean saveWarpDatabase() {
        // Try to save the warp database.
        try {
            warpDatabase.save(warpDatabaseFile);
        }
        // An error occurred while trying to save the warp database.
        catch (IOException e) {
            EverythingPlugin.getEPLogger().info(ChatColor.RED + "An error occurred saving the " +
                    "warp database file!");
            return false;
        }

        // The warp database was saved successfully.
        return true;
    }

    /**
     * Reloads the warp database.
     *
     * @return True if the warp database was reloaded successfully, false otherwise.
     */
    public boolean reloadWarpDatabase() {
        // Try to reload the warp database.
        try {
            warpDatabase = YamlConfiguration.loadConfiguration(warpDatabaseFile);
        }
        // An error occurred while trying to reload the warp database.
        catch (IllegalArgumentException e) {
            EverythingPlugin.getEPLogger().info(ChatColor.RED + "An error occurred reloading the " +
                    "warp database file!");
            return false;
        }

        // The warp database was reloaded successfully.
        return true;
    }

    /**
     * Creates a new warp database.
     *
     * @return True if a new warp database was created successfully, false otherwise.
     */
    private boolean createWarpDatabase() {
        // Try to create a new warp database file.
        try {
            warpDatabaseFile.createNewFile();
        }
        // An error occurred while creating a new warp database file.
        catch (IOException e) {
            EverythingPlugin.getEPLogger().info(ChatColor.RED + "An error occurred creating the " +
                    "warp database!");
            return false;
        }

        // A new warp database was created successfully.
        return true;
    }

    /**
     * Checks if a warp database already exists.
     *
     * @return True if the warp database already exists, false otherwise.
     */
    private boolean warpDatabaseExists() {
        return Files.exists(Paths.get(warpDatabasePath));
    }
}
