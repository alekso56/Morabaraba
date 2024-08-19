package io.github.alekso56.MorrisInfinity;

import java.util.Vector;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Board {

	private Game game;

	// constant x and y values for 2d array

	int numWhite = 0;
	int numBlack = 0;

	public static Location gameOrigin;

	public Board(Game game) {
		this.game = game;
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

	private void clearDisplayItems() {
		for (Entity entity : gameOrigin.getWorld().getNearbyEntities(gameOrigin, 10, 10, 10)) {
			if (entity instanceof BlockDisplay) {
				entity.remove();
			}
		}
	}
	private void updateBlock(int x, int y, Material air) {
		updateBlock(x,y,air,false);
	}
	private void updateBlock(int x, int y, Material air,boolean clear) {
		Location blockToUpdate = new Location(gameOrigin.getWorld(), x+ 0.5, 1, y+ 0.5);
		if(air.name().contains("GREEN")) {
			blockToUpdate = new Location(gameOrigin.getWorld(), x+ 0.5, 1.5, y+ 0.5);
	    }
		blockToUpdate = blockToUpdate.add(gameOrigin);
		if(clear) {
		for (Entity entity : gameOrigin.getWorld().getNearbyEntities(blockToUpdate, 1, 1, 1)) {
			if (entity instanceof BlockDisplay) {
				entity.remove();
			}
		}}
		BlockDisplay  displayItem = (BlockDisplay) gameOrigin.getWorld().spawnEntity(blockToUpdate,
				EntityType.BLOCK_DISPLAY);
     
		displayItem.setBlock(air.createBlockData());
		displayItem.setBillboard(Billboard.FIXED);
		Transformation transf = displayItem.getTransformation();
		transf.getScale().sub(0.5f, 0.5f, 0.5f);
		transf.getTranslation().set(new Vector3f(-0.5F, 0, -0.5F));
		displayItem.setTransformation(transf);
	}

	public void checkBlock(Location loc) {
		// check which piece position has been clicked

		int piecePosition = checkMouseBoundaries(loc.getBlockX() - gameOrigin.getBlockX(),
				loc.getBlockZ() - gameOrigin.getBlockZ());

		String action = game.getState().boardMouseClick(piecePosition);
		
		if (action.equals("whitePlaced")) {
			loc.getWorld().playSound(loc, Sound.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 0.5f, 1f);
			loc.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, loc, piecePosition);
			repaintPieces();
			game.switchTurn(true);
			return;
		} else if (action.equals("whitePlacedMill")) {
			game.displayMessage(ChatColor.GOLD+"White has formed a mill!");
			repaintPieces();
			return;
		} else if (action.equals("blackPlaced")) {
			loc.getWorld().playSound(loc, Sound.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 0.5f, 1f);
			loc.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, loc, piecePosition);
			if(game.getComputer() ==null)updatePieceSingle(piecePosition);
			game.switchTurn(true);
			repaintPieces();
			return;
		} else if (action.equals("blackPlacedMill")) {
			game.displayMessage(ChatColor.GOLD+"Black has formed a mill!");
			if(game.getComputer() != null)
			    MorrisInfinity.doBlackTurnAI();
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
			MorrisInfinity.game = null;
		} else if (action == "black") {
			game.displayMessage(4);
			game.getState().setGameStage(5);
			MorrisInfinity.game = null;
		} else if (action == "draw") {
			game.displayMessage(5);
			game.getState().setGameStage(5);
			MorrisInfinity.game = null;
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
		clearDisplayItems();
		for (int point = 0; point < game.getState().getBoardPieces().length; point++) {
			if (game.getState().getBoardPieces()[point] == "white") {
				numWhite++;
				updateBlock(piece.values()[point].getX(), piece.values()[point].getY(), Material.WHITE_CONCRETE);
			} else if (game.getState().getBoardPieces()[point] == "black") {
				numBlack++;
				updateBlock(piece.values()[point].getX(), piece.values()[point].getY(), Material.BLACK_CONCRETE);
			}
			if (game.getState().getGameStage() == 3) {
				if (point == game.getState().getSelectedPiece()) {
					updateBlock(piece.values()[point].getX(), piece.values()[point].getY(), Material.GREEN_WOOL);
				} else if (game.getState().getBoardPieces()[point] == null
						&& game.getState().getMovablePositions().contains(point)) {
					updateBlock(piece.values()[point].getX(), piece.values()[point].getY(), Material.GREEN_CARPET);
				}
			}
		}
	}

}
