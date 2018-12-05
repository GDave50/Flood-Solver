package game;

import java.awt.Graphics;

/**
 * Handles the game control and logic if played by the user.
 * 
 * @author Gage Davidson
 */
public class UserGame {
	
	private Color[][] board;
	private int moves;
	
	/**
	 * @param board board to start with
	 */
	public UserGame(Color[][] board) {
		this.board = board;
	}
	
	/**
	 * Attempts to apply a move to the game. If a move is invalid, the
	 * game state will not be changed.
	 * @param color color move to apply
	 */
	public void applyMove(Color color) {
		SearchBoard searchBoard = new SearchBoard(board);
		
		if (searchBoard.isMoveValid(color)) {
			board = Board.applyMove(board, color);
			
			++moves;
			
			searchBoard = new SearchBoard(board);
			System.out.println("Move " + moves + ". New fitness " + searchBoard.fitness());
		} else {
			System.out.println("Invalid move");
		}
		
		if (Board.goalBoard(board))
			System.out.println("Completed in " + moves + " moves!");
	}
	
	/**
	 * Attempts to apply a move to the game based on a location the user
	 * has clicked.
	 * @param x x-index that was clicked
	 * @param y y-index that was clicked
	 */
	public void applyMove(int x, int y) {
		if (! inBounds(x, y)) {
			System.out.println("Invalid move");
			return;
		}
		
		applyMove(board[x][y]);
	}
	
	/**
	 * Draws the game.
	 * 
	 * ** This method is called asynchronously. **
	 * 
	 * @param g Graphics to use
	 */
	public void drawGame(Graphics g) {
		Board.drawBoard(board, g);
	}
	
	/**
	 * Determines if the given coordinate pair is in bounds of
	 * the game board.
	 * @param x x-coordinate to check
	 * @param y y-coordinate to check
	 * @return true if the coordinate pair is in bounds
	 */
	private boolean inBounds(int x, int y) {
		return x >= 0 && x < board.length &&
				y >= 0 && y < board[x].length;
	}
}
