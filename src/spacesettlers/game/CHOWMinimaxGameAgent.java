package spacesettlers.game;

import spacesettlers.game.*;

/**
 * Plays TicTacToe2D using Heuristics since I can't implement Minimax in code the students can see
 * 
 * @author amy
 *
 */
public class CHOWMinimaxGameAgent extends AbstractGameAgent {
	int minimax(TicTacToe2DBoard board, int depth) {

		int score = board.getWinningPlayer();
		int best = -10;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				TicTacToe2DAction action = new TicTacToe2DAction(i, j);
				board.makeMove(action, this.player);
				best = Math.max(best, minimax(board, depth + 1));
				board.board[i][j] = board.empty;
			}
		}
		return score;
	}

	public TicTacToe2DAction findBestMove(TicTacToe2DBoard board) {
		int best = -10;
		TicTacToe2DAction move = new TicTacToe2DAction(-1,-1);
		for (int i = 0; i < 3; i++){
			for (int j = 0; j < 3; j++){
				if (board.board[i][j] == board.empty){
					TicTacToe2DAction action = new TicTacToe2DAction(i, j);
					board.makeMove(action, this.player);
					int moveValue = minimax(board,0);
					board.board[i][j] = board.empty;
					if (moveValue > best){
						move.row = i;
						move.col = j;
					}
				}
			}
		}
		return null;
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


/*
	int evaluateBoard(TicTacToe2DBoard board){
		int result = 0;
		for (int i  = 0; i < 3; i++){
			if (board.board[i][0]==board.board[i][1] && board.board[i][1]==board.board[i][2]){
				if (board.getWinningPlayer() == this.player){
					result = 10;
				} else {
					result = -10;
				}
			}
		}
		*/


	int bestScore;
	public AbstractGameAction getNextMove(AbstractGame game) {
		int bestScore = -10;

		if (game.getClass() == TicTacToe2D.class){

			TicTacToe2DBoard board = (TicTacToe2DBoard) game.getBoard();

			System.out.println("Heuristic agent current state of the board is \n" + board);

			// check to see if the center is free
			if (board.board[1][1] == board.empty) {
				return new TicTacToe2DAction(1, 1);
			}

			// check to see if we can win in one
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					if (board.board[i][j] == board.empty) {
						TicTacToe2DAction action = new TicTacToe2DAction(i, j);
						board.makeMove(action, this.player);
						if (board.getWinningPlayer() == this.player) {
							return action;
						}
						// unmake the move
						board.board[i][j] = board.empty;
					}
				}
			}


			// otherwise play the first available move
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					if (board.board[i][j] == board.empty) {
						return new TicTacToe2DAction(i, j);
					}
				}

			}

			System.out.println("Returning a null action for the following board\n");
			System.out.println(board);
			return null;

		} else {
			TicTacToe3DBoard board = (TicTacToe3DBoard) game.getBoard();
			System.out.println("Heuristic agent current state of the board is \n" + board);

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
