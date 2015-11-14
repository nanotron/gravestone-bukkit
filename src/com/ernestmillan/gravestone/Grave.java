package com.ernestmillan.gravestone;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.ernestmillan.gravestone.ConfigAccessor;


/**
 * The grave.
 */
public class Grave {
	
	Player player;
	public static Map<Player, Boolean> chest_a_created = new HashMap<Player, Boolean>();
	public static Map<Player, Boolean> chest_b_created = new HashMap<Player, Boolean>();
	public static Map<Player, Boolean> no_grave_built = new HashMap<Player, Boolean>();
	public static Map<Player, Boolean> grave_cleaned = new HashMap<Player, Boolean>();
	public static Map<Player, String> pre_grave_blocks = new HashMap<Player, String>();
	public static Map<Player, String> new_grave_blocks = new HashMap<Player, String>();
	private Gravestone plugin;
	
	public Grave(Gravestone plugin) {
		this.plugin = plugin;
	}

	@SuppressWarnings("deprecation")
	public void construct(LivingEntity player) {		
		Player player_player = (Player) player;
		World player_world = player.getWorld();
		
		Grave.pre_grave_blocks.put(player_player, "");
		Grave.new_grave_blocks.put(player_player, "");
		Grave.no_grave_built.put(player_player, false);
		Grave.grave_cleaned.put(player_player, false);
		
		// Check permissions.
		if(!player_player.hasPermission("gravestone.grave")) {
			Grave.no_grave_built.put(player_player, true);
			GraveUtil.log(player_player.getName()+" does not have permission. No grave built.");
			return;
		}
		// White-list
		if (GraveConfig.enable_white_list && !GraveConfig.isUserInList("white", player_player, plugin)) {
			Grave.no_grave_built.put(player_player, true);
			GraveUtil.log(player_player.getName()+" is not white-listed. No grave built.");
			return;
		}
		// Blacklist Override
		if (!GraveConfig.enable_white_list && GraveConfig.isUserInList("black", player_player, plugin)) {
			Grave.no_grave_built.put(player_player, true);
			GraveUtil.log(player_player.getName()+" is black-listed. No grave built.");
			return;
		}
		
		
		/* Proceed with build if everything is allowed. */
		Location players_location = player.getLocation();
		Material graveMaterial = Material.matchMaterial(GraveConfig.fancy_grave_material); 
		Material sideDecor = Material.matchMaterial(GraveConfig.fancy_grave_side_decor);
		Block players_block = players_location.getBlock();
		Block block_beneath_player = players_block.getRelative(0, -1, 0);
				
		// Check if block may be built over.
		if(GraveUtil.mayBuild(plugin, player_player, players_block)) {
			cleanUpUsersPriorGrave(player_player);
			
			storeBlockInfo(player_player, players_block, Grave.pre_grave_blocks);
			
			// Build grave stone.
			try {
				players_block.setType(Material.matchMaterial(GraveConfig.grave_stone_material));
			} catch(Exception e) {
				players_block.setType(Material.SMOOTH_STAIRS);
			}
			
			storeBlockInfo(player_player, players_block, Grave.new_grave_blocks);
		}

		// Create the sign and position it.
		Block graveSignBlock = players_block.getRelative(1, 0, 0);
		if(GraveUtil.mayBuild(plugin, player_player, graveSignBlock)) {
			storeBlockInfo(player_player, graveSignBlock, Grave.pre_grave_blocks);
			
			graveSignBlock.setType(Material.WALL_SIGN);
			graveSignBlock.setData((byte) 5, true);
		
			// Set the sign's text.
			// Block testForSignBlock = players_block.getRelative(1, 0, 0);
			Sign blocksign = (Sign) graveSignBlock.getState();

			blocksign.setLine(0, "[" + GraveConfig.epitaph_line_one_text + "]");
			blocksign.setLine(1, player_player.getName());
			if(GraveConfig.display_date_time_of_death) {
				blocksign.setLine(2, GraveUtil.getDateTime());
			}
			blocksign.setLine(3, "Age: "+GraveUtil.convertTicks(player_player.getTicksLived()));
			blocksign.update();
			
			// Apply lightning effect to grave for added pizzazz
			if(GraveConfig.enable_grave_lightning_effect) {
				player_world.strikeLightningEffect(graveSignBlock.getLocation());
			}
			
			storeBlockInfo(player_player, graveSignBlock, Grave.new_grave_blocks);
		}
		
		if(GraveConfig.enable_fancy_grave) {
			// Lay dirt.
			determineDirt(player_player, block_beneath_player, block_beneath_player, graveMaterial);
			determineDirt(player_player, block_beneath_player, block_beneath_player.getRelative(1, 0, 0), graveMaterial);
			determineDirt(player_player, block_beneath_player, block_beneath_player.getRelative(2, 0, 0), graveMaterial);
			determineDirt(player_player, block_beneath_player, block_beneath_player.getRelative(0, 0, 1), graveMaterial);
			determineDirt(player_player, block_beneath_player, block_beneath_player.getRelative(0, 0, -1), graveMaterial);
			
			// Air above dirt
			Block air_above_dirt = block_beneath_player.getRelative(2, 1, 0);
			storeBlockInfo(player_player, air_above_dirt, Grave.pre_grave_blocks);
			air_above_dirt.setType(Material.AIR);
			storeBlockInfo(player_player, air_above_dirt, Grave.new_grave_blocks);
			
			// Set dirt beneath grave if air
			if (block_beneath_player.getRelative(2, -1, 0).getType() == Material.AIR) {
				determineDirt(player_player, block_beneath_player, block_beneath_player.getRelative(2, -1, 0), graveMaterial);
			}
			
			if(GraveConfig.enable_fancy_grave_side_decor) {
				// Place Side Decor/Red Roses
				Block side_decor_block_a = block_beneath_player.getRelative(0, 1, 1);
				if(GraveUtil.mayBuild(plugin, player_player, side_decor_block_a)) {
					storeBlockInfo(player_player, side_decor_block_a, Grave.pre_grave_blocks);
					side_decor_block_a.setType(sideDecor);
					storeBlockInfo(player_player, side_decor_block_a, Grave.new_grave_blocks);
				}
				Block side_decor_block_b = block_beneath_player.getRelative(0, 1, -1);
				if(GraveUtil.mayBuild(plugin, player_player, side_decor_block_b)) {
					storeBlockInfo(player_player, side_decor_block_b, Grave.pre_grave_blocks);
					side_decor_block_b.setType(sideDecor);
					storeBlockInfo(player_player, side_decor_block_b, Grave.new_grave_blocks);
				}
			}
		}

		if(GraveConfig.create_buried_inventory_chest) {
			// Place Chests
			Block chest_a = block_beneath_player.getRelative(1, -1, 0);
			Block chest_b = block_beneath_player.getRelative(2, -1, 0);
			Grave.chest_a_created.put(player_player, false);
			Grave.chest_b_created.put(player_player, false);
			
			if(GraveUtil.mayBuild(plugin, player_player, chest_a)) {
				storeBlockInfo(player_player, chest_a, Grave.pre_grave_blocks);
				chest_a.setType(Material.CHEST);
				Grave.chest_a_created.put(player_player, true);
				storeBlockInfo(player_player, chest_a, Grave.new_grave_blocks);
			}
			if(GraveUtil.mayBuild(plugin, player_player, chest_b)) {
				storeBlockInfo(player_player, chest_b, Grave.pre_grave_blocks);
				chest_b.setType(Material.CHEST);
				Grave.chest_b_created.put(player_player, true);
				storeBlockInfo(player_player, chest_b, Grave.new_grave_blocks);
			}
			
			// Put dirt behind chest if air
			if (block_beneath_player.getRelative(-1, 0, 0).getType() == Material.AIR) {
				determineDirt(player_player, block_beneath_player, block_beneath_player.getRelative(0, 0, 0), graveMaterial);
			}
			
			// If chests have been created.
			if(Grave.chest_a_created.get(player_player) && Grave.chest_b_created.get(player_player)) {
				Chest graveChestA = (Chest) chest_a.getState();
				putInventoryInChests(player_player, graveChestA);
			}
		}
		
		if(Grave.grave_cleaned.get(player)) {
			saveToUserLog(player_player, "pre_grave_blocks", Grave.pre_grave_blocks.get(player_player));
			saveToUserLog(player_player, "new_grave_blocks", Grave.new_grave_blocks.get(player_player));
		}
		saveToUserLog(player_player, "death_time", System.currentTimeMillis());
	}

	public void putInventoryInChests(Player player, Chest graveChestA) {
		PlayerInventory players_inventory = player.getInventory();
		ItemStack[] items = players_inventory.getContents();

		for (ItemStack item : items) {
			if (item != null) {
				graveChestA.getInventory().addItem(item);
			}
		}

		// Item in hand
		// graveChestA.getInventory().addItem(player.getItemInHand());

		// Armor
		graveChestA.getInventory().addItem(player.getEquipment().getArmorContents());

		// Clear everything
		players_inventory.clear();
		player.getEquipment().clear();
	}
	
	@SuppressWarnings("deprecation")
	private void determineDirt(Player player, Block block_beneath_player, Block block_to_alter, Material graveMaterial) {
		if(GraveUtil.mayBuild(plugin, player, block_to_alter)) {
			storeBlockInfo(player, block_to_alter, Grave.pre_grave_blocks);
			block_to_alter.setType(graveMaterial);

			if(GraveConfig.fancy_grave_material_type.equals("podzol")) {
				block_to_alter.setData((byte) 2);
			}
			storeBlockInfo(player, block_to_alter, Grave.new_grave_blocks);
		}
	}
	
	private void storeBlockInfo(Player player, Block block, Map<Player, String> map_name) {
		Location bloc = block.getLocation();
		// block material type, x, y, z, worldname
		map_name.put(player, map_name.get(player)+block.getType()+","+bloc.getBlockX()+","+bloc.getBlockY()+","+bloc.getBlockZ()+","+bloc.getWorld().getName()+";");
		map_name.put(player, map_name.get(player));
	}
	
	private void saveToUserLog(Player player, String key, Object value) {
		// Create log of player's most recent death and the original blocks which were changed.
		ConfigAccessor users_config = new ConfigAccessor(plugin, get_UsersConfigFilePath(player));
		users_config.getConfig().set(key, value);
		users_config.saveConfig();
	}
	
	private void cleanUpUsersPriorGrave(Player player) {
		// pre_grave_blocks - Original block prior to building of grave.
		// new_grave_blocks - New blocks after grave has been built. The grave blocks.
		
		if(!GraveConfig.allow_unlimited_graves_per_player) {
			ConfigAccessor users_config = new ConfigAccessor(plugin, get_UsersConfigFilePath(player));

			if(users_config.getConfig().get("pre_grave_blocks") != null) {
				String pre_grave_blocks = users_config.getConfig().get("pre_grave_blocks").toString();
				String new_grave_blocks = users_config.getConfig().get("new_grave_blocks").toString();

				if(users_config.getConfig().get("pre_grave_blocks") != null) {
					String[] list_of_old_blocks = pre_grave_blocks.split(";");
					String[] list_of_new_blocks = new_grave_blocks.split(";");

					// Put back the original blocks.
					for(int idx = 0; idx < list_of_old_blocks.length; idx++) {
						String[] old_block_details = list_of_old_blocks[idx].split(",");
						String[] new_block_details = list_of_new_blocks[idx].split(",");
						String old_block_type = old_block_details[0];
						String old_block_world_name = old_block_details[4];
						int old_block_x = Integer.parseInt(old_block_details[1]);
						int old_block_y = Integer.parseInt(old_block_details[2]);
						int old_block_z = Integer.parseInt(old_block_details[3]);

						// Only restore the block if its material matches that of new.
						Block old_block = plugin.getServer().getWorld(old_block_world_name).getBlockAt(old_block_x, old_block_y, old_block_z);
						if(old_block.getType().name().equals(new_block_details[0]) ) {
							old_block.setType(Material.getMaterial(old_block_type));
						}
					}
				}
			}
			
			Grave.grave_cleaned.put(player, true);
		}
	}
	
	private String get_UsersConfigFilePath(Player player) {
		return "users/"+player.getName()+".yml";
	}
}
