package com.bluemarien.everythingplugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bluemarien.everythingplugin.EverythingPlugin;

/**
 * This class represents the xpbank command.
 * 
 * @author Anthony Farina
 * @version 2020.06.19
 */
public class Xpbank implements CommandExecutor {

	/**
	 * This method is run when a player runs the xpbank command.
	 * 
	 * @param sender       The entity running the command.
	 * @param command      The command object of this command located in plugin.yml.
	 * @param commandLabel The String that is succeeds the "/" symbol in the
	 *                     command.
	 * @param args         An array of arguments as Strings passed to the command.
	 *                     Does not include the command label.
	 * 
	 * @return Returns true if the command was run successfully, false otherwise.
	 */
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

		// Check that the command being run is "/xpbank".
		if (commandLabel.equals("xpbank")) {
			// Check if the entity running the command is a player.
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You must be a player to use that command!");
				return true;
			}

			Player commandPlayer = (Player) sender;

			// The player only typed "/xpbank".
			if (args.length == 0) {
				// Return the amount of levels stored in the bank.
				commandPlayer.sendMessage(ChatColor.GOLD + "XP Bank Balance: " + EverythingPlugin.expBankDB.get(commandPlayer));
				return true;
			}
			// The player typed "/xpbank (something)".
			else if (args.length == 1) {
				// Check the argument to the command.
				if (args[0].equals("create")) {
					// Create a new record in the database, if possible.
					EverythingPlugin.expBankDB.insert(commandPlayer);
					return true;
				}
				else if (args[0].equals("top10")) {
					// Show the top 10 highest xpbanks.
					// TODO
				}
			}
			// The player typed "/xpbank (something) (something)".
			else if (args.length == 2) {
				// Check if the player is trying to deposit levels into their bank.
				if (args[0].equals("deposit")) {
					// Check if the levels the player provided is an integer.
					int levelsToDeposit = 0;
					
					try {
						levelsToDeposit = Integer.parseInt(args[1]);
					}
					catch (NumberFormatException e) {
						commandPlayer.sendMessage(ChatColor.RED + "You must provide a positive integer level!");
						return true;
					}
					
					// Check if the player provided a positive integer level.
					if (levelsToDeposit < 1) {
						commandPlayer.sendMessage(ChatColor.RED + "You must provide a positive integer level!");
						return true;
					}
					
					// Check if the player is not trying to deposit more levels than they have.
					if (levelsToDeposit > commandPlayer.getExpToLevel()) {
						commandPlayer.sendMessage(ChatColor.RED + "You don't have that many levels to deposit!");
						return true;
					}
					
					// Deposit levels into player's xpbank.
					// TODO
				}
				// Check if the player is trying to withdrawal levels from their bank.
				else if (args[0].equals("withdrawal")) {
					// TODO
				}
			}
		}

		// The command was not handled.
		return false;
	}
}
