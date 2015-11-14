package com.ernestmillan.gravestone;

import java.io.File;
import java.io.IOException;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Custom configuration files.
 */
public class GraveCustomConfig {
	
	private JavaPlugin plugin;
	private String file_name;
	private File config_file;
	private String config_file_path;
	
	public GraveCustomConfig(JavaPlugin plugin, String file_name) {
		this.plugin = plugin;
		this.file_name = file_name;
	}
	
	public void generateFile() {
		try {
			config_file = getFile();
			
			if(!config_file.exists()) {
				// Write blank file
				File file = new File(config_file_path);
				file.createNewFile();
				GraveUtil.log("Generating: "+file_name);
			}
		} catch (IOException e) {
			GraveUtil.log("Error encountered: "+e);
		}
	}
	
	public File getFile() {
		config_file_path = plugin.getDataFolder()+"/"+file_name;
		config_file = new File(config_file_path);
		return config_file;
	}
	
}
