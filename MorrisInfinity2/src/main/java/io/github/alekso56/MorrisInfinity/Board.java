package io.github.alekso56.MorrisInfinity;

import java.util.ArrayList;
import java.util.Vector;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import io.github.alekso56.MorrisInfinity.state.DisplayPoolEntry;

public class Board {

	private Game game;

	// constant x and y values for 2d array

	int numWhite = 0;
	int numBlack = 0;

	public static Location gameOrigin;
	
	public ArrayList<DisplayPoolEntry> pool;

	public Board(Game game) {
		this.game = game;
		this.pool = new ArrayList<DisplayPoolEntry>();
	}

	public void updatePieceSingle(int point) {
		if (game.getState().getBoardPieces()[point] == null && game.getState().getGameStage() == 1) {
			updateBlock(piece.values()[point].getX(), piece.values()[point].getY(), Material.AIR,true);
		} else if (game.getState().getBoardPieces()[point] != null && game.getState().getGameStage() == 2) {
			if (game.getState().getBoardPieces()[point] == "white") {
				updateBlock(piece.values()[point].getX(), piece.values()[point].getY(), Material.WHITE_CONCRETE,true);
			} else {
				updateBlock(piece.values()[point].getX(), piece.values()[point].getY(), Material.BLACK_CONCRETE,true);
			}
		}

	}
	private void createPieceAt(int x, int y, BlockData blockData) {
		Location blockToUpdate = new Location(gameOrigin.getWorld(), x, 1, y);
		if(blockData.getMaterial().name().contains("GREEN")) {
			blockToUpdate = new Location(gameOrigin.getWorld(), x, 1.5, y);
	    }
		blockToUpdate = blockToUpdate.add(gameOrigin);
        BlockDisplay  displayItem = (BlockDisplay) gameOrigin.getWorld().spawnEntity(blockToUpdate,EntityType.BLOCK_DISPLAY);
        displayItem.setBlock(blockData);
		displayItem.setBrightness(new Display.Brightness(15, 15));
		displayItem.setBillboard(Billboard.FIXED);
		displayItem.setTransformation(new Transformation(
    new Vector3f(-0.15f, 0f, -0.5f),
    new AxisAngle4f(0, 0, 0, 1),
    new Vector3f(0.5f, 0.5f, 0.5f),
    new AxisAngle4f(0, 0, 1, 0)
));
		pool.add(new DisplayPoolEntry(displayItem,x,y));
	}
	
	private void updateBlock(int x, int y, Material pieceColor) {
		updateBlock(x,y,pieceColor,false);
	}
	private void updateBlock(int x, int y, Material pieceColor,boolean clear) {
		Location blockToUpdate = new Location(gameOrigin.getWorld(), x, 1, y);
		if(pieceColor != null && pieceColor.name().contains("GREEN")) {
			blockToUpdate = new Location(gameOrigin.getWorld(), x, 1.5, y);
	    }
		blockToUpdate = blockToUpdate.add(gameOrigin);
		boolean isInPool = false;
		for (DisplayPoolEntry entity : pool) {
			if (entity.x == x && entity.y == y) {
				if(clear) {
				    entity.display.setBlock(Material.BARRIER.createBlockData());
				}else {
					entity.display.setBlock(pieceColor.createBlockData());
				}
				isInPool = true;
				break;
			}
		}
		if(!isInPool && !clear) {
			createPieceAt(x,y,pieceColor.createBlockData());
		}
	}

	

	public void checkBlock(Location loc) {
		// check which piece position has been clicked

		int piecePosition = checkMouseBoundaries(loc.getBlockX() - gameOrigin.getBlockX(),
				loc.getBlockZ() - gameOrigin.getBlockZ());

		String action = game.getState().boardMouseClick(piecePosition);
		
		if (action.equals("whitePlaced")) {
			loc.getWorld().playSound(loc, Sound.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 0.5f, 1f);
			 new BukkitRunnable() {
		            int i = 0;
		         
		            @Override
		            public void run() {
		                if (i == 4) {
		                    cancel();
		                    return;
		                }
		             
		                
		    			loc.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, loc, piecePosition);
		                i++;
		            }
		        }.runTaskTimer(MorrisInfinity.instance, 0L, 20L);
			repaintPieces();
			game.switchTurn(true);
			return;
		} else if (action.equals("whitePlacedMill")) {
			game.displayMessage(ChatColor.GOLD+"White has formed a mill!");
			game.displayMessage(ChatColor.GOLD+"White must remove a piece!");
			repaintPieces();
			return;
		} else if (action.equals("blackPlaced")) {
			 loc.getWorld().playSound(loc, Sound.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 0.5f, 1f);
			 new BukkitRunnable() {
		            int i = 0;
		         
		            @Override
		            public void run() {
		                if (i == 4) {
		                    cancel();
		                    return;
		                }
		             
		               
		    			loc.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, loc, piecePosition);
		                i++;
		            }
		        }.runTaskTimer(MorrisInfinity.instance, 0L, 20L);
			if(game.getComputer() ==null)updatePieceSingle(piecePosition);
			game.switchTurn(true);
			repaintPieces();
			return;
		} else if (action.equals("blackPlacedMill")) {
			game.displayMessage(ChatColor.GOLD+"Black has formed a mill!");
			game.displayMessage(ChatColor.GOLD+"Black must remove a piece!");
			if(game.getComputer() != null) {
			    MorrisInfinity.doBlackTurnAI();
			}else
			   repaintPieces();
			return;
		} else if (action.equals("whiteRemoval") || action.equals("blackRemoval")) {
			game.switchTurn(true);
			updatePieceSingle(piecePosition);
			return;
		} else if (action.equals("invalidRemoval")) {
			game.displayMessage(1);
		} else if (action.equals("millNoRemoval")) {
			game.displayMessage(2);
		} else if (action.equals("validPieceSelected")) {
			repaintPieces();
			return;
		} else if (action.equals("resetPieceSelected")) {
			repaintPieces();
			return;
		} else if (action.equals("pieceMoved")) {
			repaintPieces();
			game.switchTurn(true);
			return;
		} else if (action == "white") {
			game.displayMessage(3);
			game.getState().setGameStage(5);
			MorrisInfinity.setGame(null);
		} else if (action == "black") {
			game.displayMessage(4);
			game.getState().setGameStage(5);
			MorrisInfinity.setGame(null);
		} else if (action == "draw") {
			game.displayMessage(5);
			game.getState().setGameStage(5);
			MorrisInfinity.setGame(null);
		}
	
	}

	public static enum piece {
		outer_top_left(6, -6), outer_top_middle(6, 0), outer_top_right(6, 6), middle_top_left(4, -4),
		middle_top_middle(4, 0), middle_top_right(4, 4), inner_top_left(2, -2), inner_top_middle(2, 0),
		inner_top_right(2, 2), outer_centre_left(0, -6), middle_centre_left(0, -4), inner_centre_left(0, -2),
		inner_centre_right(0, 2), middle_centre_right(0, 4), outer_centre_right(0, 6), inner_bottom_left(-2, -2),
		inner_bottom_middle(-2, 0), inner_bottom_right(-2, 2), middle_bottom_left(-4, -4), middle_bottom_middle(-4, 0),
		middle_bottom_righ(-4, 4), outer_bottom_left(-6, -6), outer_bottom_middle(-6, 0), outer_bottom_right(-6, 6);

		private int x;
		private int y;

		piece(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}
	}

	private int checkMouseBoundaries(int x, int y) {
		//System.out.println("" + x + ", " + y);
		for (piece val : piece.values()) {
			if (x == val.getX() && y == val.getY()) {
			//	System.out.println("Clicked "+val.name()+" "+val.ordinal());
				return val.ordinal();
			}
		}
		return -1;
	}

	public void repaintPieces() {
		numWhite = 0;
		numBlack = 0;
		for (int point = 0; point < game.getState().getBoardPieces().length; point++) {
			Material selected = null;
			if (game.getState().getBoardPieces()[point] == "white") {
				numWhite++;
				selected = Material.WHITE_CONCRETE;
			} else if (game.getState().getBoardPieces()[point] == "black") {
				numBlack++;
				selected = Material.BLACK_CONCRETE;
			}
			if (game.getState().getGameStage() == 3) {
				if (point == game.getState().getSelectedPiece()) {
					selected = Material.GREEN_WOOL;
				} else if (game.getState().getBoardPieces()[point] == null
						&& game.getState().getMovablePositions().contains(point)) {
					selected = Material.GREEN_CARPET;
				}
			}
			updateBlock(piece.values()[point].getX(), piece.values()[point].getY(), selected,selected == null);
		}
	}

}
