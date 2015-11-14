package com.ernestmillan.gravestone;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GraveCommands implements CommandExecutor {
	//private Gravestone plugin;
	Player player;
	Grave playerGrave;
	
	public GraveCommands(Gravestone plugin, Grave playerGrave) {
		//this.plugin = plugin;
		this.playerGrave = playerGrave;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender instanceof Player) {
			player = (Player)sender;
		}	
		
		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("info")) {
				sender.sendMessage(ChatColor.WHITE + "Gravestone: " + ChatColor.GRAY + "Constructs an inscribed gravestone, with a buried inventory chest, wherever a player dies.");
			} 
			return true;
		} 
		
		return false;
	}
	
}
