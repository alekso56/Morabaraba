package io.github.alekso56.MorrisInfinity;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
			ChatColor.GOLD+"A mill was formed, you may select a piece inside a mill to remove", ChatColor.GOLD+"The winner is White", ChatColor.GOLD+"The winner is Black", ChatColor.GOLD+"The game is a draw"};
	
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
		//1 = phase 1 - placing stage
		//2 = phase 2 - movement stage
		//3 = valid piece selected to move adjacently 
		//4 = mill created - remove opponent's piece
		//5 = game end - draw, or player wins
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
				player.sendMessage(gameMessages[i]);
				if(opponent != null) {
					opponent.sendMessage(gameMessages[i]);
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
