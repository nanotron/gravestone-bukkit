package com.ernestmillan.gravestone;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;


/**
 * General functionality.
 * @author Ernest Millan
 */
public class GraveUtil {

	public static void log(String text) {
		Logger log = Logger.getLogger("Gravestone");
		log.info("[Gravestone] " + text);
	}

	public static String getDateTime() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yy.MM.dd HH:mm");
		return sdf.format(cal.getTime());
	}
	
	public static String convertTicks(int ticks) {
		String time;
		int seconds = ticks / 20;
		
		if(seconds < 60) {
			time = seconds+"s";
		} else {
			int minutes = seconds / 60;
			if(minutes < 60) {
				time = minutes+"m "+(seconds-(minutes*60))+"s";
			} else {
				int hours = minutes / 60;
				time = hours+"h "+(minutes-(hours*60))+"m "+(seconds-(minutes*60))+"s";
			}
		}
		return time;
	}
	
	public static boolean isGraveAllowedInWorld(Player player) {
		String world_type_name = player.getWorld().getEnvironment().name();	
				
		/* Test world type */
		if(world_type_name.equals("NETHER") && !GraveConfig.allow_grave_in_nether) {
			//GraveUtil.log("Graves are not allowed in the Nether.");
			return false;
		}
		if(world_type_name.equals("NORMAL") && !GraveConfig.allow_grave_in_normal_world) {
			//GraveUtil.log("Graves are not allowed in the overworld.");
			return false;
		}
		if(world_type_name.equals("THE_END") && !GraveConfig.allow_grave_in_end) {
			//GraveUtil.log("Graves are not allowed in the End.");
			return false;
		}
		
		return true;
	}
	
	private static boolean doesPlayerHaveItems(Player player) {
		int player_item_count = 0;
		int player_armor_count = 0;
		
		for (ItemStack item : player.getInventory().getContents()) {
			if (item != null) {
				player_item_count++;
			}
		}
		for (ItemStack armor : player.getEquipment().getArmorContents()) {
			if (!armor.getType().name().equals("AIR")) {
				player_armor_count++;
			}
		}
						
		if(GraveConfig.only_build_grave_if_player_has_inventory && (player_item_count == 0) && (player_armor_count == 0)) {
			return false;
		} 
		return true;
	}
	
	public static boolean mayBuild(Plugin plugin, Player player, Block block) {
		Material block_material = block.getType();
		
		// Only build a grave if player has an inventory.		
		if(!doesPlayerHaveItems(player)) {
			return false;
		}
		
		// World permissions.
		GraveUtil.isGraveAllowedInWorld(player);
				
		/* Handle WorldGuard */
		if(getWorldGuard(plugin) != null && !getWorldGuard(plugin).canBuild(player, block)) {
			return false;
		} 
				
		if(!GraveConfig.only_build_grave_on_natural_material) {
			return true;
		} else {
			// Materials which a grave will be allowed to build over.
			String[] permitted_materials = {"dirt","grass","long grass","flower","sapling","gravel","podzol","red rose","yellow flower",
					"stone","sand","air","netherrack","soul sand","enderstone","fire","water","cactus","log","dead bush",
					"stationary water","lava","stationary lava","leaves","mycel","clay","log","snow","vine"};
			
			for(String permitted_material: permitted_materials) {
				if(Material.matchMaterial(block_material.name()) == Material.matchMaterial(permitted_material)) {
					return true;
				}
			}
			
			return false;
		}
	}
	
	public static boolean isGraveItem(Block block) {
		
		return false;
	}
	
	public static boolean isGraveOwner(Player player) {
		
		return false;
	}
	
	
	/* 3rd-Party Plugin Support */
	
	private static WorldGuardPlugin getWorldGuard(Plugin gravestonePlugin) {
	    Plugin plugin = gravestonePlugin.getServer().getPluginManager().getPlugin("WorldGuard");
	 
	    // WorldGuard may not be loaded
	    if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
	        return null; 
	    }
	 
	    return (WorldGuardPlugin) plugin;
	}
	
}
