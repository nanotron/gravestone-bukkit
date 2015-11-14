/**
 * Gravestone Plugin for Bukkit 			  
 * by Ernest Millan, emillan    				  
 * Version 1.0.9				  
 * for bukkit-1.7.9-R0.2.jar	  
 */

package com.ernestmillan.gravestone;

import java.util.ArrayList;

import org.bukkit.plugin.java.JavaPlugin;

public class Gravestone extends JavaPlugin {
	public static Grave playerGrave;
	public static ArrayList<String> available_grave_options = new ArrayList<String>();
	
	@Override
	public void onEnable() {	
		GraveConfig graveConfig = new GraveConfig(this);
		graveConfig.process();
		
		Grave playerGrave = new Grave(this);
		
		// Events
		this.getServer().getPluginManager().registerEvents(new GraveListeners(playerGrave), this);
		
		// Commands
		GraveCommands playerGraveCommands = new GraveCommands(this, playerGrave);
		getCommand("gravestone").setExecutor(playerGraveCommands);
	}
	
	@Override
	public void onDisable() {
		GraveUtil.log("Gravestone: Disabled.");
	}

}
