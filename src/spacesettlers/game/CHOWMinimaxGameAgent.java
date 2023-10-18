package spacesettlers.game;

import spacesettlers.game.*;

/**
 * Plays TicTacToe2D using Heuristics since I can't implement Minimax in code the students can see
 * 
 * @author amy
 *
 */
public class CHOWMinimaxGameAgent extends AbstractGameAgent {

	public CHOWMinimaxGameAgent() {
	}

	/**
	 * First see if we can win in one and then otherwise take the first available 
	 * (this will be improved over time)
	 * 
	 * @param game
	 * @return
	 */




	public AbstractGameAction getNextMove(AbstractGame game) {
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
