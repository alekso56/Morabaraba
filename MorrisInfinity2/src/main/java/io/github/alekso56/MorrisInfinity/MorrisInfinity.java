package io.github.alekso56.MorrisInfinity;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.alekso56.MorrisInfinity.Board.piece;
import io.github.alekso56.MorrisInfinity.player.MoveScore;

public class MorrisInfinity extends JavaPlugin {
	
	public static MorrisInfinity instance;
	public static Game game;
	public static int depth;
	
	 public FileConfiguration config = getConfig();
	 
	 @Override
	 public void onEnable() {
		 instance = this;
		 config.options().copyDefaults(true);
		 saveConfig();
		 Commands commands = new Commands();
		 getServer().getPluginManager().registerEvents(new events(), this);
		 getCommand("morris").setExecutor(commands);
		 getCommand("morris").setTabCompleter(commands);
		 Location savedLocation = loadLocation("origin");
	        if (savedLocation != null) {
	            getLogger().info("Location loaded: " + savedLocation.toString());
	            Board.gameOrigin = savedLocation;
	        }
	 }

	public static void StartPVP(Player player, Player vs){
		game = new Game(player,vs);
	}
	
	public static void StartPVAI(Player player, String difficulty) {
		game = new Game(player);
		depth = setDifficulty(difficulty);
	}
	
	public static void doBlackTurnAI() {
		if(game.getState().getGameStage()!=5) {
			if(game.getState().getTurn()=="white") {
				return;
			}
			game.getComputer().setCopyState(game.getState().saveGameState());
			MoveScore bestMove = game.getComputer().minimax("black", depth, -1000000, 1000000);
			if(!game.getComputer().makeMove(bestMove, "black")) {
				game.switchTurn(true);					
			}else {
				game.switchTurn(false);
				piece move = Board.piece.values()[bestMove.index];
				game.displayMessage(ChatColor.GOLD+"Computer: "+move.name());
				Location blockToUpdate = new Location(Board.gameOrigin.getWorld(), move.getX()+ 0.5, 1, move.getY()+ 0.5);
				blockToUpdate = blockToUpdate.add(Board.gameOrigin);
				blockToUpdate.getWorld().playSound(blockToUpdate, Sound.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 0.5f, 1f);
				blockToUpdate.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, blockToUpdate, move.ordinal());
				if(game.getState().getTurn()!="white") {
					doBlackTurnAI();
				}
			}
            
			
		}
		System.out.print("Game ended");
	}
	
	   public Location loadLocation(String path) {
	        FileConfiguration config = getConfig();
	        
	        // Retrieve location components from config
	        String worldName = config.getString(path + ".world");
	        if (worldName == null) return null;
	        World world = Bukkit.getWorld(worldName);
	        if (world == null) return null;

	        double x = config.getDouble(path + ".x");
	        double y = config.getDouble(path + ".y");
	        double z = config.getDouble(path + ".z");
	        float yaw = (float) config.getDouble(path + ".yaw");
	        float pitch = (float) config.getDouble(path + ".pitch");
	        
	        // Create and return the location object
	        return new Location(world, x, y, z, yaw, pitch);
	    }
	   public void saveLocation(String path, Location location) {
	        
	        // Saving location components to config
	        config.set(path + ".world", location.getWorld().getName());
	        config.set(path + ".x", location.getX());
	        config.set(path + ".y", location.getY());
	        config.set(path + ".z", location.getZ());
	        config.set(path + ".yaw", location.getYaw());
	        config.set(path + ".pitch", location.getPitch());
	        
	        // Save the config file
	        saveConfig();
	    }
	private static int setDifficulty(String difficulty) {

		if(difficulty=="easy") 
			return 4;
		else if(difficulty=="hard")
			return 8;
		else //"medium"
			return 6;
		
	}


	    static void handleBlockClick(PlayerInteractEvent event, Player player) {
	        Block clickedBlock = event.getClickedBlock();
	        if (clickedBlock == null) return;
	        if(game.getComputer() != null && game.getState().getTurn().equals("black")) {
	        	doBlackTurnAI();
	        	game.getBoard().repaintPieces();
	        	return;
	        }
	        if(game.getOpponent() != null && game.getState().getTurn().equals("black") &&  game.getOpponent().getUniqueId().equals(player.getUniqueId())) {
	        	game.getBoard().checkBlock(clickedBlock.getLocation());
	        	return;
	        }
            if(game.getPlayer() != null && game.getState().getTurn().equals("white") &&  game.getPlayer().getUniqueId().equals(player.getUniqueId())) {
	        	game.getBoard().checkBlock(clickedBlock.getLocation());
	        	if(game.getState().getTurn().equals("black") && game.getComputer() != null) {
	        		doBlackTurnAI();
	        		game.getBoard().repaintPieces();
	        	}
	        	return;
	        }
	    }

}
