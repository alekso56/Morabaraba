package io.github.alekso56.MorrisInfinity;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import io.github.alekso56.MorrisInfinity.player.Minimax;
import io.github.alekso56.MorrisInfinity.state.GameState;

public class Game {
	
	private GameState state;
	private Board board;
	private Minimax computer;
	private Player player;
	private Player opponent;

	private String[] gameMessages = {ChatColor.GOLD+"A Mill is formed! Select a piece not in a mill to remove", ChatColor.RED+"Invalid Piece Removal", 
			ChatColor.GOLD+"A mill was formed, you may select a piece inside a mill to remove", ChatColor.GOLD+"The winner is &player", ChatColor.GOLD+"The winner is &opponent", ChatColor.GOLD+"The game is a draw"};
	
	public Game(Player pl) {
		state = new GameState();
		computer = new Minimax(this, state);
		board = new Board(this);
		player = pl;
	}
	
	public Game(Player pl,Player op) {
		state = new GameState();
		board = new Board(this);
        this.player = pl;
        this.opponent = op;
	}
	
	public GameState getState() {
		return state;
	}
	
	public Minimax getComputer() {
		return computer;
	}
	
	
	public Player getPlayer() {
		return player;
	}

	public Player getOpponent() {
		return opponent;
	}

	public void switchTurn(boolean b) {
		if(b) {
		if(state.getTurn()=="white") { 
			state.setTurn("black");
		}
		else if(state.getTurn()=="black") {
			state.setTurn("white");
		}
		}
		if(state.getGameStage() == 1) {
			int remaining = 0;
			if(state.getTurn().equals("white")) {
				remaining = state.numberOfPieces - state.getWhitePiecesPlaced();
			}else {
				remaining = state.numberOfPieces - state.getBlackPiecesPlaced();
			}
			displayMessage(state.getTurn()+ChatColor.GOLD+": has "+ChatColor.RED+remaining+ChatColor.GOLD+" pieces left to place.");
		}else if(state.getGameStage() == 2) {
			displayMessage(state.getTurn()+ChatColor.GOLD+": Please select a piece to move.");
		}else if(state.getGameStage() == 4) {
			displayMessage(state.getTurn()+ChatColor.GOLD+": Please select one of the opponents pieces to remove.");
		}
	}
	
	public void setGameStage(int stage) {
		state.setGameStage(stage);
	}
	
	public void setGameState(GameState newState) {
		state = newState;
	}
	
	public Board getBoard() {
		return board;
	}

	public void displayMessage(int i) {
		Bukkit.getScheduler().runTask(MorrisInfinity.instance, new Runnable() {

			@Override
			public void run() {
				String message = gameMessages[i];
				message = message.replace("&player", player.getDisplayName());
				if(opponent != null) {
					message = message.replace("&opponent", opponent.getDisplayName());
					opponent.sendMessage(message);
				}
				player.sendMessage(message);
				switch(i) {
				case 3:
				case 4:
				case 5:
		    		for(Player pl : Bukkit.getServer().getOnlinePlayers()) {
		    			if(pl.getUniqueId().equals(player.getUniqueId()) || opponent != null && pl.getUniqueId().equals(opponent.getUniqueId())) continue;
		    			Location location = pl.getLocation();
		    			if(location.getWorld().getUID().equals(Board.gameOrigin.getWorld().getUID()) && location.distanceSquared(Board.gameOrigin) < 1000) {
		    				pl.sendMessage(message);
		    			}
		    		}
		    		break;
		    	default:
		    			break;
				}
			}
			
		});
		
	}

	public void displayMessage(String string) {
		Bukkit.getScheduler().runTask(MorrisInfinity.instance, new Runnable() {

			@Override
			public void run() {
				player.sendMessage(string);
				if(opponent != null) {
					opponent.sendMessage(string);
				}
			}
			
		});
	}




}
