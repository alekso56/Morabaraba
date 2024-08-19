package io.github.alekso56.MorrisInfinity.player;

public class Move {

	int gameStage = 0;
	int piecePosition = 0;
	int to = 0;
	
	public Move(int gameStage, int boardPiece, int to) {
		this.gameStage=gameStage;
		this.piecePosition = boardPiece;
		this.to = to;
	}
	
	public int getGameStage() {
		return gameStage;
	}
	
	public int getPiecePosition() {
		return piecePosition;
	}
	
	public int getTo() {
		return to;
	}
	
}
