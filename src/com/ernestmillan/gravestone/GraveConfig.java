package com.ernestmillan.gravestone;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;



/**
 * Initializes configuration related values and settings.
 * @author Ernest Millan
 */
public class GraveConfig {
	private JavaPlugin plugin;
	public static String epitaph_line_one_text, fancy_grave_material, fancy_grave_material_type, fancy_grave_side_decor, grave_stone_material;
	public static boolean only_build_grave_on_natural_material, enable_fancy_grave_side_decor, allow_grave_in_normal_world, allow_grave_in_nether, allow_grave_in_end, 
	create_buried_inventory_chest, display_date_time_of_death, display_player_name, enable_fancy_grave, enable_white_list, allow_unlimited_graves_per_player, 
	enable_grave_lightning_effect, only_build_grave_if_player_has_inventory;
	
	public GraveConfig(JavaPlugin plugin) {
		this.plugin = plugin;
		plugin.getConfig().options().copyDefaults(true);
		plugin.saveConfig();
	}
	
	public void process() {
		// Strings
		epitaph_line_one_text = "R.I.P.";
		
		fancy_grave_material = fancy_grave_material_type = plugin.getConfig().getString("fancy-grave-material", "dirt");
		/* Special exception for Podzol */
		if(fancy_grave_material.equalsIgnoreCase("podzol")) {
			fancy_grave_material = "dirt";
			fancy_grave_material_type = "podzol";
		} 
		
		fancy_grave_side_decor = plugin.getConfig().getString("fancy-grave-side-decor", "red rose");
		grave_stone_material = plugin.getConfig().getString("grave-stone-material", "smooth stairs");
				
		// Booleans
		enable_white_list = plugin.getConfig().getBoolean("enable-white-list", false);
		only_build_grave_on_natural_material = plugin.getConfig().getBoolean("only-build-grave-on-natural-material", true);
		create_buried_inventory_chest = plugin.getConfig().getBoolean("create-buried-inventory-chest", true);
		display_date_time_of_death = plugin.getConfig().getBoolean("display-date-time-of-death", true);
		enable_fancy_grave = plugin.getConfig().getBoolean("enable-fancy-grave", true);
		enable_fancy_grave_side_decor = plugin.getConfig().getBoolean("enable-fancy-grave-side-decor", true);
		allow_unlimited_graves_per_player = plugin.getConfig().getBoolean("allow-unlimited-graves-per-player", true);
		enable_grave_lightning_effect = plugin.getConfig().getBoolean("enable-grave-lightning-effect", true);
		only_build_grave_if_player_has_inventory = plugin.getConfig().getBoolean("only-build-grave-if-player-has-inventory", true);
		
		allow_grave_in_normal_world = plugin.getConfig().getBoolean("allow-grave-in-normal-world", true);
		allow_grave_in_nether = plugin.getConfig().getBoolean("allow-grave-in-nether", true);
		allow_grave_in_end = plugin.getConfig().getBoolean("allow-grave-in-end", true);
		
		createCustomConfigs();
	}	
	
	private void createCustomConfigs() {
		GraveCustomConfig white_list = new GraveCustomConfig(plugin, "white-list.txt");
		GraveCustomConfig black_list = new GraveCustomConfig(plugin, "black-list.txt");
		
		white_list.generateFile();
		black_list.generateFile();
	}
	
	public static Boolean isUserInList(String type, Player player, Plugin plugin) {
		try {
			String config_file_path = plugin.getDataFolder()+"/"+type+"-list.txt";
			File config_file = new File(config_file_path);
		    
		    if(config_file.exists()) {
		    	// Read File
				@SuppressWarnings("resource")
				BufferedReader config_content = new BufferedReader(new FileReader(config_file));
		    	String name;
		    	while ((name = config_content.readLine()) != null) {
		    		if(player.getName().equalsIgnoreCase(name)) {
		    			return true;
		    		}
		    	}
		    	config_content.close();
		    } else {
		    	GraveUtil.log("Unable to read "+config_file_path+". Please fix, or delete it to regenerate.");
		    }
		} catch (IOException e) {
			GraveUtil.log("Error encountered: "+e);
			return false;
		}
		return false;
	}
}
