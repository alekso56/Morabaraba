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
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.alekso56.MorrisInfinity.Board.piece;
import io.github.alekso56.MorrisInfinity.player.MoveScore;

public class MorrisInfinity extends JavaPlugin {
	
	public static MorrisInfinity instance;
	private static Game game;
	public static int depth;
	public static boolean computerBusy = false;
	
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
		setGame(new Game(player,vs));
	}
	
	public static void StartPVAI(Player player, String difficulty) {
		setGame(new Game(player));
		depth = setDifficulty(difficulty);
	}
	
	public static void doBlackTurnAI() {
		if (getGame().getState().getGameStage() != 5) {
			if (getGame().getState().getTurn() == "white" || computerBusy) {
				return;
			}
			getGame().getComputer().setCopyState(getGame().getState().saveGameState());
			computerBusy = true;
			Bukkit.getScheduler().runTaskAsynchronously(instance, new Runnable() {

				@Override
				public void run() {
					MoveScore bestMove = getGame().getComputer().minimax("black", depth, -1000000, 1000000);
					Bukkit.getScheduler().runTask(instance, new Runnable() {

						@Override
						public void run() {
							computerBusy = false;
							piece move = Board.piece.values()[bestMove.index];
							Location blockToUpdate = new Location(Board.gameOrigin.getWorld(), move.getX()+ 0.5, 1, move.getY()+ 0.5);
							blockToUpdate = blockToUpdate.add(Board.gameOrigin);
							
							if(getGame().getState().getGameStage() == 3 || getGame().getState().getGameStage() == 1) {
								getGame().displayMessage(ChatColor.GOLD+"Computer: sel/place: "+move.name());
								blockToUpdate.getWorld().playSound(blockToUpdate, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 0.5f, 1f);
								final Location world = blockToUpdate;
						        new BukkitRunnable() {
						            int i = 0;
						         
						            @Override
						            public void run() {
						                if (i == 4) {
						                    cancel();
						                    return;
						                }
						             
						                world.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, world, move.ordinal());
						                i++;
						            }
						        }.runTaskTimer(instance, 0L, 20L);
							}else if(getGame().getState().getGameStage() == 4) {
								getGame().displayMessage(ChatColor.GOLD+"Computer: remove: "+move.name());
								blockToUpdate.getWorld().playSound(blockToUpdate, Sound.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 0.5f, 1f);
								final Location world = blockToUpdate;
						        new BukkitRunnable() {
						            int i = 0;
						         
						            @Override
						            public void run() {
						                if (i == 4) {
						                    cancel();
						                    return;
						                }
						             
						                world.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, world, move.ordinal());
						                i++;
						            }
						        }.runTaskTimer(instance, 0L, 20L);
								
							}else {
								blockToUpdate.getWorld().playSound(blockToUpdate, Sound.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 0.5f, 1f);
								final Location world = blockToUpdate;
						        new BukkitRunnable() {
						            int i = 0;
						         
						            @Override
						            public void run() {
						                if (i == 4) {
						                    cancel();
						                    return;
						                }
						             
						                world.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, world, move.ordinal());
						                i++;
						            }
						        }.runTaskTimer(instance, 0L, 20L);
							}
							boolean computerNeedsAdditionalTurn = getGame().getComputer().makeMove(bestMove, "black");
							if(getGame()== null) return;
							if(!computerNeedsAdditionalTurn) {
								getGame().switchTurn(true);		
								getGame().getBoard().repaintPieces();
							}else {
								getGame().switchTurn(false);
								if(getGame().getState().getTurn()!="white") {
									doBlackTurnAI();
								}
							}
						}});
				}
			});
		}

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
	        if (clickedBlock == null || getGame() == null) return;
	        if(getGame().getComputer() != null && getGame().getState().getTurn().equals("black")) {
	        	doBlackTurnAI();
	        	return;
	        }
	        if(getGame().getOpponent() != null && getGame().getState().getTurn().equals("black") &&  getGame().getOpponent().getUniqueId().equals(player.getUniqueId())) {
	        	getGame().getBoard().checkBlock(clickedBlock.getLocation());
	        	return;
	        }
            if(getGame().getPlayer() != null && getGame().getState().getTurn().equals("white") &&  getGame().getPlayer().getUniqueId().equals(player.getUniqueId())) {
	        	getGame().getBoard().checkBlock(clickedBlock.getLocation());
	        	if(getGame() != null && getGame().getState().getTurn().equals("black") && getGame().getComputer() != null) {
	        		doBlackTurnAI();
	        	}
	        	return;
	        }
	    }

		public static Game getGame() {
			return game;
		}

		public static void setGame(Game game) {
			for (Entity entity : Board.gameOrigin.getWorld().getNearbyEntities(Board.gameOrigin, 10, 10, 10)) {
    			if (entity instanceof BlockDisplay || entity instanceof ItemDisplay) {
    				entity.remove();
    			}
    		}
			if(MorrisInfinity.game != null)MorrisInfinity.getGame().getBoard().pool.clear();
			MorrisInfinity.game = game;
			
		}

}
