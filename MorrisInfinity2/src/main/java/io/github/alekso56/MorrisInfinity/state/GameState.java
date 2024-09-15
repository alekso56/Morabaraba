package io.github.alekso56.MorrisInfinity.state;

import java.util.ArrayList;

import org.bukkit.ChatColor;

public class GameState {

	//18 * (1) + 26 * (2) + 1 * (3) + 9 * (4) + 10 * (5) + 7 * (6)
	//14 * (1) + 43 * (2) + 10 * (3) + 11 * (4) + 8 * (7) + 1086 * (8)
	//16 * (1) + 10 * (5) + 1 * (6) + 1190 * (8)
	int[] phaseOneEvaluationWeights = new int[] { 20, 15, 1, 10, 10, 5, 0 };
	int[] phaseTwoEvaluationWeights = new int[] { 10, 45, 15, 10, 10, 10, 1000 };
	int[] phaseThreeEvaluationWeights = new int[] { 15, 0, 0, 0, 10, 1, 1000 };
	public final int numberOfPieces = 12;
	public final int totalNumberOfPieces = 24;
	
	private String[] boardPieces;
	private int[][] millLocations = {{0,1,2}, {3,4,5}, {6,7,8}, {9,10,11}, {12,13,14}, {15,16,17}, {18,19,20}, {21,22, 23},
			{0,9,21}, {3,10,18}, {6,11,15}, {1,4,7}, {16,19,22}, {8,12,17}, {5,13,20}, {2,14,23}, {0,3,6}, {2,5,8}, {21,18,15},
			{23,20,17}
	};
	private int[][] adjacentPositions = {{1,3,9}, {0,2,4}, {1,5,14}, {0,4,6,10}, {1,3,5,7}, {2,4,8,13}, {3,7,11}, {4,6,8}, {5,7,12}, 
			{0,10,21}, {3,9,11,18}, {6,10,15}, {8,13,17}, {5,12,14,20}, {2,13,23}, {11,16,18}, {15,17,19}, {12,16,20}, {10,15,19,21}, 
			{16,18,20,22}, {13,17,19,23}, {9,18,22}, {19,21,23}, {14,20,22}
	};
	private ArrayList<Integer> movablePositions = new ArrayList<Integer>();
	private ArrayList<Integer> emptySpaces = new ArrayList<Integer>();
	private String[] millsFound = new String[millLocations.length];
	
	//1 = phase 1 - placing stage
	//2 = phase 2 - movement stage
	//3 = valid piece selected to move adjacently 
	//4 = mill created - remove opponent's piece
	//5 = game end - draw, or player wins
	private int gameStage = 1; 
	public int phase = 1;
	public int piecesPlaced = 0;
	public int whitePiecesPlaced = 0;
	public int blackPiecesPlaced = 0;
	private String turn = "white";
	private int selectedPiece = -1;
	private boolean flyingWhite = false;
	private boolean flyingBlack = false;
	private String endgame;
	
	public GameState() {
		boardPieces = new String[totalNumberOfPieces];
		for(int i=0; i<totalNumberOfPieces; i++) {
			boardPieces[i] = null;
		}
	}
	
	public void setFlyingWhite(boolean bool) {
		flyingWhite=bool;
	}
	
	public void setFlyingBlack(boolean bool) {
		flyingBlack=bool;
	}
	
	public boolean getFlyingWhite() {
		return flyingWhite;
	}
	
	public boolean getFlyingBlack() {
		return flyingBlack;
	}
	
	public int[][] getMillLocations() {
		return millLocations;
	}
	
	public int[][] getAdjacentPositions() {
		return adjacentPositions;
	}
	
	public ArrayList<Integer> getMovablePositions() {
		return movablePositions;
	}
	
	public void appendMovablePositions(int position) {
		movablePositions.add(position);
	}
	
	public void resetMovablePositions() {
		movablePositions.clear();
	}
	
	public void setMillsFound(String[] mills) {
		millsFound = mills;
	}
	
	public String[] getMillsFound() {
		return millsFound;
	}
	
	/*1 = phase 1 - placing stage
	2 = phase 2 - movement stage
	3 = valid piece selected to move adjacently 
	4 = mill created - remove opponent's piece
	5 = game end - draw, or player wins*/
	public int getGameStage() {
		return gameStage;
	}
	
	public void setGameStage(int stage) {
		gameStage = stage;
	}
	
	public String getTurn() {
		return turn;
	}
	
	public void setTurn(String newTurn) {
		turn = newTurn;
	}
	
	public void switchTurn() {
		if(turn=="white") { 
			turn = "black";
		}
		else if(turn=="black") {
			turn = "white";
		}
	}
	
	public String[] getBoardPieces() {
		return boardPieces;
	}

	public void setBoardPieces(String[] pieces) {
		for(int i=0; i<pieces.length; i++) {
			boardPieces[i]=pieces[i];
		}
	}
	public void setBoardPiece(int index, String value) {
		boardPieces[index] = value;
	}
	
	public String getBoardPiece(int index) {
		return boardPieces[index];
	}
	
	public int getWhitePiecesPlaced() {
		return whitePiecesPlaced;
	}
	
	public int getBlackPiecesPlaced() {
		return blackPiecesPlaced;
	}
	
	public void setWhitePiecesPlaced(int pieces) {
		whitePiecesPlaced = pieces;
		
	}
	
	public void setBlackPiecesPlaced(int pieces) {
		blackPiecesPlaced = pieces;
	}
	
	public void setPiecesPlaced(int pieces) {
		piecesPlaced = pieces;
	}
	
	public int getSelectedPiece() {
		return selectedPiece;
	}
	
	public void setSelectedPiece(int piece) {
		selectedPiece = piece;
	}
	
	
	public boolean checkForMill() {
		boolean newMillFound = false;
		for(int i=0; i<millLocations.length; i++) {
			if(boardPieces[millLocations[i][0]]=="white" && boardPieces[millLocations[i][1]]=="white" && boardPieces[millLocations[i][2]]=="white") {
				if(millsFound[i]==null) {
					millsFound[i] = "white";
					gameStage = 4;   
					newMillFound=true;
				}
			}
			else if(boardPieces[millLocations[i][0]]=="black" && boardPieces[millLocations[i][1]]=="black" && boardPieces[millLocations[i][2]]=="black") {
				if(millsFound[i]==null) {
					millsFound[i] = "black";
					gameStage = 4;   
					newMillFound=true;
				}
			}
			else {
				millsFound[i] = null;
			}
		}
		
		return newMillFound;
		
	}
	
	public boolean inMill(int piecePosition) {
		for(int i=0; i<millLocations.length; i++) {
			for(int j=0; j<millLocations[i].length; j++) {
				if(piecePosition==millLocations[i][j]) {
					if(millsFound[i]!=null) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public int countMills(String color) {
		int mills = 0;
		for(int i=0; i<millsFound.length; i++) {
			if(millsFound[i]==color) {
				mills++;
			}
		}
		return mills;
	}
	
	public String boardMouseClick(int piecePosition) {
		
		if(piecePosition==-1 && gameStage!=3) {
			return "invalidClick";
		}
		
		switch(gameStage) {
		
		case 1:
			if(boardPieces[piecePosition]==null) {
				boardPieces[piecePosition] = turn;
				if(turn=="white") 
					whitePiecesPlaced++;
				else 
					blackPiecesPlaced++;
	    		piecesPlaced++;
				if(checkForMill()) {
					if(!canPieceBeRemoved()) {
			       		return "millNoRemoval";
			       	}    	 
					resetMovablePositions();
			        return turn + "PlacedMill";
				}
				else if(piecesPlaced>=totalNumberOfPieces){
					endgame = hasGameEnded();
					if(endgame!=null) {
						return endgame;
					}
					gameStage=2;
					phase = 2;
				}
				return turn + "Placed";
			}
			break;
			
		case 2:
			if((turn=="white" && boardPieces[piecePosition]=="white") || (turn=="black" && boardPieces[piecePosition]=="black")) {
				gameStage = 3;
				selectedPiece = piecePosition;
        		showAvailablePositionsToMove(piecePosition);
				return "validPieceSelected";
			}
			else {
				return "invalidPieceSelected";
			}
			
		case 3:
			if(piecePosition==-1) {
				gameStage = 2;
				selectedPiece = -1;
        		resetMovablePositions();
				return "resetPieceSelected";
			}
			if(movablePositions.contains(piecePosition)) {
				boardPieces[selectedPiece] = null;
				boardPieces[piecePosition] = turn;
				if(checkForMill()) {
					if(!canPieceBeRemoved()) {
			       		return "millNoRemoval";
			       	}    	 
			        return turn + "PlacedMill";
				}
				endgame = hasGameEnded();
				if(endgame!=null) {
					return endgame;
				}
				gameStage = 2;
				selectedPiece = -1;
        		resetMovablePositions();
				return "pieceMoved";
			}
			break;
			
		case 4:
			if(inMill(piecePosition) && canPieceBeRemoved()) {
    			return "invalidRemoval";
    		}
    		if((turn=="white" && boardPieces[piecePosition]=="black") || (turn=="black" && boardPieces[piecePosition]=="white"))  {
    			boardPieces[piecePosition] = null;
    			checkForMill();
    			endgame = hasGameEnded();
    			if(endgame!=null) {
    				return endgame;
    			}
    			if(piecesPlaced<totalNumberOfPieces) {
    				gameStage = 1;
    			}
    			else {
    				gameStage = 2;
    			}
    			resetMovablePositions();
    			return turn + "Removal";
    		}		
    		break;
		}
		
		return "invalid";
	}


	public String hasGameEnded() {
		
		if(checkForDraw()) {
			return "draw";
		}
		String winner = checkForWin();
		if(winner!=null) {
			return winner;
		}
		
		return null;
		
	}
	public boolean canPieceBeRemoved() {
		
		String otherTurn;
		if(turn=="black") {
			otherTurn = "white";
		}
		else {
			otherTurn = "black";
		}
		for(int i=0; i<boardPieces.length; i++) {
			if(boardPieces[i]==otherTurn) {
				if(!inMill(i)) {
					return true;
				}
			} 
		}
		return false;
		
	}
	private boolean checkForDraw() {
		
		if(gameStage==1) {
			for(int i=0; i<boardPieces.length; i++) {
				if(boardPieces[i]==null) {
					return false;
				}
			}
			return true;
		}
		return false;
   	 
	}
	
	public String checkForWin() {
		
		int whitePieces = 0;
		int blackPieces = 0;
		
		for(int i=0; i<boardPieces.length; i++) {
			if(boardPieces[i]=="white")
				whitePieces++;
			else if(boardPieces[i]=="black") 
				blackPieces++;
		}
		if(whitePieces==3 && piecesPlaced>=totalNumberOfPieces) {
			flyingWhite = true;
			phase = 3;
			return null;
		}
		if(blackPieces==3 && piecesPlaced>=totalNumberOfPieces) {
			flyingBlack = true;
			phase = 3;
			return null;
		}
		if(whitePieces==2 && piecesPlaced>=totalNumberOfPieces) 
			return "black";
		else if(blackPieces==2 && piecesPlaced>=totalNumberOfPieces) 
			return "white";
		
		String otherTurn;
		if(turn=="white") {
			otherTurn="black";
			if(flyingBlack) {
				return null;
			}
		}
		else {
			otherTurn = "white";
			if(flyingWhite) {
				return null;
			}
		}
		
		for(int i=0; i<boardPieces.length; i++) {
			if(boardPieces[i]==otherTurn) {
				if(checkForValidMove(i)) 
					return null;
			}	
		}
		
		if(gameStage==1) {
			return null;
		}
		
		//returns the winner
		return turn;
		
	}

	private boolean checkForValidMove(int piece) {
		
		for(int i=0; i<adjacentPositions[piece].length; i++) {
			if(boardPieces[adjacentPositions[piece][i]]==null) {
				return true;
			}
		}
		return false;
		
	}
	
	public void showAvailablePositionsToMove(int piecePosition) {
		if((flyingWhite && turn=="white") || (flyingBlack && turn=="black")) {
			emptySpaces = getEmptySpaces();
			for(int i=0; i<emptySpaces.size(); i++) {
				appendMovablePositions(emptySpaces.get(i));
			}
		}
		int size = adjacentPositions[piecePosition].length;
		for(int i=0; i<size; i++) {
			if(boardPieces[adjacentPositions[piecePosition][i]]==null) {
				appendMovablePositions(adjacentPositions[piecePosition][i]);
			}
		}
		
	}
	
	public ArrayList<Integer> getEmptySpaces() {
		emptySpaces.clear();
		for(int i=0; i<boardPieces.length; i++) {
			if(boardPieces[i]==null) {
				emptySpaces.add(i);
			}
		}
		return emptySpaces;
	}
	
	public boolean pieceBlocked(int piece) {
		for(int i=0; i<adjacentPositions[piece].length; i++) {
			if(boardPieces[adjacentPositions[piece][i]]==null) {
				return false;
			}
		}
		return true;
	}
	
	public int countBlockedPieces(String player) {
		int blockedPieces = 0;
		for(int i=0; i<boardPieces.length; i++) {
			if(boardPieces[i]==player) {
				if(pieceBlocked(i)) {
					blockedPieces++;
				}
			}
		}
		return blockedPieces;
	}
	
	
	public int countPieces(String player) {
		int pieces = 0;
		
		for(int i=0; i<boardPieces.length; i++) {
			if(boardPieces[i]==player) {
				pieces++;
			}
		}
		return pieces;
		
	}
	
	public int countTwoPieceConfigurations(String player) {
		int number = 0;
		
		for(int i=0; i<millLocations.length; i++) {
			if(boardPieces[millLocations[i][0]]==player && boardPieces[millLocations[i][1]]==player && boardPieces[millLocations[i][2]]==null) {
				number++;
			}
			else if(boardPieces[millLocations[i][0]]==player && boardPieces[millLocations[i][1]]==null && boardPieces[millLocations[i][2]]==player) {
				number++;
			}
			else if(boardPieces[millLocations[i][0]]==null && boardPieces[millLocations[i][1]]==player && boardPieces[millLocations[i][2]]==player) {
				number++;
			}
		} 
		
		
		return number;
	}
	
	public ArrayList<ArrayList<Integer>> findTwoPieceConfigurations(String player) {
		
		ArrayList<ArrayList<Integer>> configs = new ArrayList<ArrayList<Integer>>();
		
		for(int i=0; i<millLocations.length; i++) {
			if(boardPieces[millLocations[i][0]]==player && boardPieces[millLocations[i][1]]==player && boardPieces[millLocations[i][2]]==null) {
				ArrayList<Integer> config = new ArrayList<Integer>();
				config.add(millLocations[i][0]);
				config.add(millLocations[i][1]);
				configs.add(config);
			}
			else if(boardPieces[millLocations[i][0]]==null && boardPieces[millLocations[i][1]]==player && boardPieces[millLocations[i][2]]==player) {
				ArrayList<Integer> config = new ArrayList<Integer>();
				config.add(millLocations[i][1]);
				config.add(millLocations[i][2]);
				configs.add(config);
			}
		} 
		
		return configs;
		
	}
	
	public int countThreePieceConfigurations(String player) {
		ArrayList<ArrayList<Integer>> configs = findTwoPieceConfigurations(player);
		int number = 0;
		
		for(int i=0; i<configs.size(); i++) {
			for(int j=i; j<configs.size(); j++) {
				if(i==j) {
					continue;
				}
				if(configs.get(i).contains(configs.get(j).get(0)) || configs.get(i).contains(configs.get(j).get(1))) {
					number++;
				}
			}
		}
		
		return number;
	}
	
	public ArrayList<Integer> findOtherMills(int piece, int currentMillIndex) {
		ArrayList<Integer> mills = new ArrayList<Integer>();
		
		for(int i=0; i<millLocations.length; i++) {
			if(i==currentMillIndex) {
				continue;
			}
			for(int j=0; j<millLocations[i].length; j++) {
				if(millLocations[i][j]==piece) {
					mills.add(i);
					continue;
				}
			}
		}
		
		return mills;
	}
	/*want to look at the following evaluation features:
	
	-Closed Morris: 1 if a morris was closed in the last move by the player (and an opponent’s piece should be grabbed in this move), -1 if a 
	morris was closed by the opponent in the last move, 0 otherwise.
	
	-Number of Morrises: Difference between the number of yours and yours opponent’s morrises
	
	-Number of blocked opponent pieces: Difference between the number of yours opponent’s and yours blocked pieces 
	(pieces which don’t have an empty adjacent point)
	
	-Number of pieces: Difference between the number of yours and yours opponent’s pieces
	
	-Number of 2 piece configurations: Difference between the number of yours and yours opponent’s 
	2 piece configurations (A 2-piece configuration is one to which adding one more piece would close a morris)
	
	-Number of 3-piece configurations: Difference between the number of yours and yours opponent’s 3 piece configurations 
	(A 3-piece configuration is one to which a piece can be added in which one of two ways to close a morris)
	
	-Winning configuration: 1 if the state is winning for the player, -1 if losing, 0 otherwise
	
*/
	public int evaluateState(String player, boolean print) {
		
		ArrayList<String> prints = new ArrayList<String>();
		
		prints.add("Game is in phase: " + phase);
		int[] evaluationWeights;
		switch(phase) {
		case 1:
			evaluationWeights = phaseOneEvaluationWeights;
			break;
		case 2:
			evaluationWeights = phaseTwoEvaluationWeights;
			break;
		case 3:
			evaluationWeights = phaseThreeEvaluationWeights;
			break;
		default:
			evaluationWeights = phaseOneEvaluationWeights;
		}
		
		int score = 0;
		
		String otherPlayer;
		if(player=="white") {
			otherPlayer="black";
		}
		else {
			otherPlayer="white";
		}
		
		
		//evaluation 1
		if(gameStage==4) {
			if(player=="black")  {
				prints.add("Black just placed a mill");
				score+=(1*evaluationWeights[0]);
			}
			else 
				prints.add("White just placed a mill");
				score-=(1*evaluationWeights[0]);
		}
		
		//evaluation 2
		score += ((countMills("black") - countMills("white"))*evaluationWeights[1]);
		prints.add("Black has " + countMills("black") + " mills");
		prints.add("White has " + countMills("white") + " mills");
		
		//evaluation 3
		score +=  ((countBlockedPieces("white") - countBlockedPieces("black"))*evaluationWeights[2]);
		prints.add("Black has " + countBlockedPieces("black") + " blocked pieces");
		prints.add("White has " + countBlockedPieces("white") + " blocked pieces");
		
		//evaluation 4
		score += ((countPieces("black") - countPieces("white"))*evaluationWeights[3]);
		prints.add("Black has " + countPieces("black") + " pieces");
		prints.add("White has " + countPieces("white") + " pieces");
		
		//evaluation 5
		score += ((countTwoPieceConfigurations("black") - countTwoPieceConfigurations("white"))*evaluationWeights[4]);
		prints.add("Black has " + countTwoPieceConfigurations("black") + " two piece configs");
		prints.add("White has " + countTwoPieceConfigurations("white") + " two piece configs");
		
		//evaluation 6
		score += ((countThreePieceConfigurations("black") - countThreePieceConfigurations("white"))*evaluationWeights[5]);
		prints.add("Black has " + countThreePieceConfigurations("black") + " three piece configs");
		prints.add("White has " + countThreePieceConfigurations("white") + " three piece configs");
		
		//evaluation 7
		String winner = hasGameEnded();
		if(winner=="draw") {
			score+=0;
		}
		else if(winner=="black") {
			prints.add("Black wins with this move");
			score+=(1*evaluationWeights[6]);
		}
		else if(winner=="white") {
			prints.add("White wins with this move");
			score-=(1*evaluationWeights[6]);
		}
		
		if(print) {
			for(int i=0; i<prints.size(); i++) {
				System.out.println(prints.get(i));
			}
			
		}
		
		
		return score;
	}
	
	
	public GameState saveGameState() {
		
		GameState saveState = new GameState();
		
		saveState.setBoardPieces(this.getBoardPieces().clone());
		saveState.setWhitePiecesPlaced(this.getWhitePiecesPlaced());
		saveState.setBlackPiecesPlaced(this.getBlackPiecesPlaced());
		saveState.setPiecesPlaced(this.whitePiecesPlaced + this.blackPiecesPlaced);
		saveState.setGameStage(this.getGameStage());
		saveState.setMillsFound(this.getMillsFound().clone());
		saveState.setTurn(this.getTurn());
		saveState.setSelectedPiece(this.getSelectedPiece());
		saveState.setFlyingWhite(this.getFlyingWhite());
		saveState.setFlyingBlack(this.getFlyingBlack());
		
		return saveState;
		
	}
	
	public void printBoardPieces() {
		
		String pieces = "[";
		for(int i=0; i<boardPieces.length; i++) {
			pieces += boardPieces[i] + ", ";
		}
		System.out.println(pieces + "]");
	}

	public int getPhase() {
		return phase;
	}
	


	
	
}
