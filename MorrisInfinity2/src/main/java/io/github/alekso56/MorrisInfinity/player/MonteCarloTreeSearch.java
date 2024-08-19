package io.github.alekso56.MorrisInfinity.player;

import java.util.ArrayList;
import java.util.Stack;

import io.github.alekso56.MorrisInfinity.Game;
import io.github.alekso56.MorrisInfinity.state.GameState;

public class MonteCarloTreeSearch {

	Game game;
	GameState state;
	GameState copyState;
	Stack<GameState> states = new Stack<GameState>();
	
	int nodesEvaluated = 0;
	
	public MonteCarloTreeSearch(Game game, GameState state) {
		this.game = game;
		this.state = state;
	}
	
	public void setCopyState(GameState newState) {
		this.copyState = newState;
		states.push(state);
	}
	
	public MoveScore monteCarloTreeSearch(String player, long timeForSearch) {
		
		Node root = new Node(copyState, null);
		long startTime = System.currentTimeMillis();
		
		while((System.currentTimeMillis() - startTime) < timeForSearch) {
			
			Node bestNode = selection(root);
			
		}
		/*let startTime = Date.now();
		  while ((Date.now() - startTime) < 1000) {
		    let promisingNode = selectPromisingNode(rootNode);
		    
		    if (promisingNode.state.board.checkStatus() === IN_PROGRESS) {
		      expandNode(promisingNode);
		    }
		    let nodeToExplore = promisingNode;
		    if (nodeToExplore.childArray.length > 0) {
		      nodeToExplore = promisingNode.getRandomChild();
		    }
		    let playoutResult = simRanPlayout(nodeToExplore, opponent)
		    backpropagation(nodeToExplore, playoutResult);
		  }
		  let winnerNode = rootNode.getChildWithMaxScore();
		  return winnerNode.state.board;*/
		
		
		return null;
		
	}
	
	
	private Node selection(Node node) {
		
		Node n = node;
		if(n.getChildren().isEmpty()) {
			return n;
		}
		
		int bestChildIndex = 0;
		double bestScore = 0;

		for(int i=0; i<n.getChildren().size(); i++) {
			double UCBScore = calculateUCBScore(n.getChildren().get(i));
			if(UCBScore> bestScore) {
				bestScore = UCBScore;
				bestChildIndex = i;
			}
		}
		  
		return n.getChildren().get(bestChildIndex);
	}

	private double calculateUCBScore(Node node) {
		
		double V = node.getTotalScore() / node.getVisits();
		int N = node.getParent().getVisits();
		int n = node.getVisits();
		
		return V + 2*(Math.sqrt(Math.log(N)/n));
	}

	public ArrayList<Move> findValidMoves(String player) {
		
		String otherTurn;
		if(player=="black") {
			otherTurn = "white";
		}
		else {
			otherTurn = "black";
		}
		ArrayList<Move> validMoves = new ArrayList<Move>();
		
		if(copyState.getGameStage()==1) {
			
			for(int i=0; i<copyState.getBoardPieces().length; i++) {
				if(copyState.getBoardPieces()[i]==null) {
					validMoves.add(new Move(1, i, -1));
				}
			}
		}
		else if(copyState.getGameStage()==2) {
			
			for(int i=0; i<copyState.getBoardPieces().length; i++) {
				if(copyState.getTurn()==player && copyState.getBoardPieces()[i]==player) {
					copyState.showAvailablePositionsToMove(i);
					if(!copyState.getMovablePositions().isEmpty()) {
						for(int j=0; j<copyState.getMovablePositions().size(); j++) {
							validMoves.add(new Move(2, i, copyState.getMovablePositions().get(j)));
						}
						copyState.resetMovablePositions();
					}
				}
			}	
			
		}
		else if(copyState.getGameStage()==4) {
			
			for(int i=0; i<copyState.getBoardPieces().length; i++) {
				
				if(copyState.inMill(i) && copyState.canPieceBeRemoved()) {
	    			continue;
	    		}
				if((copyState.getTurn()==player && copyState.getBoardPieces()[i]==otherTurn))  {
					validMoves.add(new Move(4, i, -1));
				}
				
			}
		}
		
		return validMoves;
	}
	
}
