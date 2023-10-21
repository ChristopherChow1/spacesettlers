package spacesettlers.game;

import spacesettlers.game.*;
/**
 * Plays TicTacToe2D using Heuristics since I can't implement Minimax in code the students can see
 * 
 * @author amy
 *
 */
public class CHOWMinimaxGameAgent extends AbstractGameAgent {
	int score = -1;
	int minimax(TicTacToe2DBoard board, int depth, boolean isMax) {
		System.out.println("running minimax");
		score = board.getWinningPlayer();// the default value is 0
		if (score == 1) {
			System.out.println("returning player Score: " + score);
			return 10;
		}
		//this is looping the code
		if (score == 0) {
			System.out.println("returning opponent Score: " + score);
			return -10;
		}
		if (isMax){
			int best = -1000;
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					//check if space is empty
					if (board.board[i][j] == board.empty) {
						//make a move
						TicTacToe2DAction action = new TicTacToe2DAction(i, j);
						board.makeMove(action, this.player);

						//recursively call minimax

						best = Math.max(best, minimax(board, depth+1, !isMax));


						//undo move
						board.board[i][j] = board.empty;
					}
				}
			}
			//System.out.println("Best value in minimax: " + best);
			return best;
		} else {
			int best = 1000;
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					//check if space is empty
					if (board.board[i][j] == board.empty) {
						//make a move
						//TicTacToe2DAction action = new TicTacToe2DAction(i, j);
						//board.makeMove(action, this.player);

						//recursively call minimax

						best = Math.min(best, minimax(board, depth+1, !isMax));


						//undo move
						board.board[i][j] = board.empty;
					}
				}
			}
			//System.out.println("Best value in minimax: " + best);
			return best;
		}

	}
	TicTacToe2DAction findBestMove(TicTacToe2DBoard board) {
		//System.out.println("finding best move using minimax");
		int bestVal = -10;
		//initialize move
		TicTacToe2DAction move = new TicTacToe2DAction(-1,-1);
		for (int i = 0; i < 3; i++){
			for (int j = 0; j < 3; j++){
				// check if space is empty
				if (board.board[i][j] == board.empty){
					//make move
					move = new TicTacToe2DAction(i, j);
					board.makeMove(move, this.player);
					// use minimax
					int moveValue = minimax(board,0, false);
					System.out.println("moveValue: " + moveValue);
					//undo move
					board.board[i][j] = board.empty;
					//replace previous values with better values
					if (moveValue > bestVal){
						move = new TicTacToe2DAction(i, j);
						board.makeMove(move, this.player);
						bestVal = moveValue;
					}
				}
			}
		}
		//System.out.println("The best value is: " + bestVal);
		//System.out.println("The best move is: ROW: " + move.getRow() + ", COL: "+ move.getCol());
		return move;
	}
	public CHOWMinimaxGameAgent() {
	}

	/**
	 * First see if we can win in one and then otherwise take the first available 
	 * (this will be improved over time)
	 * 
	 * @param game
	 * @return
	 */
	//int bestScore;
	public AbstractGameAction getNextMove(AbstractGame game) {
		if (game.getClass() == TicTacToe2D.class){

			TicTacToe2DBoard board = (TicTacToe2DBoard) game.getBoard();

			//here is an attempt to use my agent
			System.out.println("using custom minimax agent");
			TicTacToe2DAction bestMove = findBestMove(board);
			board.makeMove(bestMove,this.player);
			//System.out.println();

			System.out.println("ROW: " + bestMove.getRow() + " COL: " + bestMove.getCol());
			//System.out.println("Returning a null action for the following board\n");
			//System.out.println(board);
			return null;

		} else {
			//this is the default 3x3x3 tictactoe agent
			TicTacToe3DBoard board = (TicTacToe3DBoard) game.getBoard();
			//System.out.println("Using the default 3d tictactoe");
			//System.out.println("Heuristic agent current state of the board is \n" + board);

			// check to see if the center is free
			if (board.board[1][1][1] == board.empty) {
				return new TicTacToe3DAction(1, 1, 1);
			}

			// check to see if the other two centers are free
			if (board.board[1][1][0] == board.empty) {
				return new TicTacToe3DAction(1, 1, 0);
			}

			if (board.board[1][1][2] == board.empty) {
				return new TicTacToe3DAction(1, 1, 2);
			}

			// check to see if we can win in one
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					for (int k = 0; k < 3; k++) {
						if (board.board[i][j][k] == board.empty) {
							TicTacToe3DAction action = new TicTacToe3DAction(i, j, k);
							board.makeMove(action, this.player);
							if (board.getWinningPlayer() == this.player) {
								return action;
							}
							// unmake the move
							board.board[i][j][k] = board.empty;
						}
					}
				}
			}


			// otherwise play the first available move
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					for (int k = 0; k < 3; k++) {
						if (board.board[i][j][k] == board.empty) {
							return new TicTacToe3DAction(i, j, k);
						}
					}
				}
			}
			return null;
		}

	}

}
