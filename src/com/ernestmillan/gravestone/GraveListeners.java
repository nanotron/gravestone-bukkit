package com.ernestmillan.gravestone;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
//import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * Global listeners.
 * @author Ernest Millan
 */
public class GraveListeners implements Listener {
	
	private Grave playerGrave;

	public GraveListeners(Grave playerGrave) {
		this.playerGrave = playerGrave;
	}

	@EventHandler
	public void onPlayersDeath(EntityDeathEvent event) {
		if(event.getEntity() instanceof Player){
			playerGrave.construct(event.getEntity());
			
			if(!Grave.no_grave_built.get(event.getEntity()) && GraveConfig.create_buried_inventory_chest 
					&& Grave.chest_a_created.get(event.getEntity()) && Grave.chest_b_created.get(event.getEntity())) {
				event.getDrops().clear();
			}
		}
	}
	
	/*
	@EventHandler
	public void onPlayerBreak(BlockBreakEvent event){	
		if(event.getPlayer() instanceof Player){
			Boolean isGravestoneItem = GraveUtil.isGraveItem(event.getBlock());
			Boolean isGravestoneOwner = GraveUtil.isGraveOwner(event.getPlayer());
			
			if(isGravestoneItem && !isGravestoneOwner) {
				GraveUtil.log("Breaker is grave owner!");
				event.setCancelled(true);
			}
		}
	}
	*/
	
}
