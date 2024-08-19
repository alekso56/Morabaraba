package io.github.alekso56.MorrisInfinity;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class events implements Listener {
	  @EventHandler
	    public void onPlayerInteract(PlayerInteractEvent event) {
		  if (event.getHand() == EquipmentSlot.OFF_HAND) return;
	        Player player = event.getPlayer();
	        UUID playerUUID = player.getUniqueId();

	        // Check if the player is in a game
	        if (MorrisInfinity.game != null && (MorrisInfinity.game.getPlayer() != null && MorrisInfinity.game.getPlayer().getUniqueId().equals(playerUUID) 
	        		|| MorrisInfinity.game.getOpponent() != null && MorrisInfinity.game.getOpponent().getUniqueId().equals(playerUUID))) {
	        	Action action = event.getAction();

		        if (action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK) {
		        	MorrisInfinity.handleBlockClick(event, player);
		        }
	        }
	        
	    }
}
