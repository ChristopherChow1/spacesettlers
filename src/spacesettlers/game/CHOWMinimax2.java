package spacesettlers.game;


public class CHOWMinimax2 extends AbstractGameAgent {




	public CHOWMinimax2() {
	}
	int score2d(TicTacToe2DBoard board, int depth){
		if (board.getWinningPlayer() == 1){
			return 10 - depth;
		} else {
			return depth - 10;
		}
		//return 0;
	}
	int minimax2d(TicTacToe2DBoard board, int depth, boolean isMax){
		if (depth == 0){
			return score2d(board, depth);
		}
		if (board.getWinningPlayer() == this.player){
			return 10;
		}
		if (isMax){
			//Maximize for player
			int best = -1000;
			for (int i = 0; i < 3; i++){
				for (int j = 0; j < 3; j++){
					//check if space is empty
					if (board.board[i][j] == board.empty){
						//make a move
						TicTacToe2DAction action = new TicTacToe2DAction(i, j);
						board.makeMove(action, this.player);
						//recursively call minimax
						best = Math.max(best, minimax2d(board, depth-1, false));
					}
				}
			}
			//System.out.println("Best value in minimax: " + best);
			return best;
		} else {
			// minimize
			int best = 1000;
			for (int i = 0; i < 3; i++){
				for (int j = 0; j < 3; j++){
					//check if space is empty
					if (board.board[i][j] == board.empty){
						//make a move
						TicTacToe2DAction action = new TicTacToe2DAction(i, j);
						board.makeMove(action, this.player);
						//recursively call minimax
						best = Math.min(best, minimax2d(board, depth-1, true));
					}
				}
			}
			//System.out.println("Best value in minimax: " + best);
			return best;
		}

		//return 0; // temporary
	}
	TicTacToe2DAction findBestMove2d(TicTacToe2DBoard board){
		TicTacToe2DAction move = new TicTacToe2DAction(-1,-1);
		int bestVal = -1000;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (board.board[i][j] == board.empty){
					move = new TicTacToe2DAction(i,j);
					board.makeMove(move, this.player);
					//use minimax
					int moveValue = minimax2d(board,3, true);
					//undo move
					board.board[i][j] = board.empty;
					if (moveValue > bestVal){
						move = new TicTacToe2DAction(i, j);
						board.makeMove(move, this.player);
						bestVal = moveValue;
					}
				}
			}
		}
		return move;
	}



	int score3d(TicTacToe3DBoard board, int depth){
		if (board.getWinningPlayer() == 1){
			return 10 - depth;
		} else {
			return depth - 10;
		}
		//return 0;
	}
	int minimax3d(TicTacToe3DBoard board, int depth, boolean isMax){
		if (depth == 0){
			return score3d(board, depth);
		}
		if (board.getWinningPlayer() == this.player){
			return 10;
		}
		if (isMax){
			//Maximize for player
			int best = -1000;
			for (int i = 0; i < 3; i++){
				for (int j = 0; j < 3; j++){
					for (int k = 0; k < 3; k++){
						if (board.board[i][j][k] == board.empty){
							//make a move
							TicTacToe3DAction action = new TicTacToe3DAction(i, j, k);
							board.makeMove(action, this.player);
							//recursively call minimax
							best = Math.max(best, minimax3d(board, depth-1, false));
					}
					//check if space is empty

					}
				}
			}
			//System.out.println("Best value in minimax: " + best);
			return best;
		} else {
			// minimize
			int best = 1000;
			for (int i = 0; i < 3; i++){
				for (int j = 0; j < 3; j++){
					for (int k = 0; k < 3; k++){
						//check if space is empty
						if (board.board[i][j][k] == board.empty){
							//make a move
							TicTacToe3DAction action = new TicTacToe3DAction(i, j,k);
							board.makeMove(action, this.player);
							//recursively call minimax
							best = Math.min(best, minimax3d(board, depth-1, true));
					}


					}
				}
			}
			//System.out.println("Best value in minimax: " + best);
			return best;
		}

		//return 0; // temporary
	}
	TicTacToe3DAction findBestMove3d(TicTacToe3DBoard board){
		TicTacToe3DAction move = new TicTacToe3DAction(-1,-1,-1);
		int bestVal = -1000;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < 3; k++){
					if (board.board[i][j][k] == board.empty){
						move = new TicTacToe3DAction(i,j,k);
						board.makeMove(move, this.player);
						//use minimax
						int moveValue = minimax3d(board,3, true);
						//setting the depth limit to 3 seems to help the minimax agent win more frequently
						//undo move
						board.board[i][j][k] = board.empty;
						if (moveValue > bestVal){
							move = new TicTacToe3DAction(i, j, k);
							board.makeMove(move, this.player);
							bestVal = moveValue;
				}

					}
				}
			}
		}
		return move;
	}
	public AbstractGameAction getNextMove(AbstractGame game) {
		if (game.getClass() == TicTacToe2D.class){
			TicTacToe2DBoard board = (TicTacToe2DBoard) game.getBoard();

			//here is an attempt to use my agent
			System.out.println("using custom minimax2d agent");
			TicTacToe2DAction bestMove = findBestMove2d(board);
			board.makeMove(bestMove,this.player);

			//System.out.println();

			//System.out.println("ROW: " + bestMove.getRow() + " COL: " + bestMove.getCol());
			//System.out.println("Returning a null action for the following board\n");
			System.out.println(board);
			return bestMove;
		} else {

			//here is an attempt to use my 3d agent
			System.out.println("using custom minimax3d agent");
			TicTacToe3DBoard board = (TicTacToe3DBoard) game.getBoard();
			TicTacToe3DAction bestMove3d = findBestMove3d(board);
			board.makeMove(bestMove3d, this.player);
			System.out.println(board);
			return bestMove3d;
		}
	}
}
