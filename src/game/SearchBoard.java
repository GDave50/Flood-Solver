package game;

/**
 * This class contains functions that help with searching the board
 * for various purposes. None of these methods alter the board.
 * 
 * @author Gage Davidson
 */
public class SearchBoard {
	
	private final Color[][] board;
	private boolean[][] searched; // which nodes have been searched
	private boolean[][] flip; // when making a move, denotes if a color should be flipped
	
	/**
	 * @param board board to search
	 */
	public SearchBoard(Color[][] board) {
		this.board = board;
	}
	
	/**
	 * Search the board and mark all slots that should be changed.
	 * @param color color to search and mark for
	 * @return 2D boolean array with marks in each slot where color
	 * should be changed
	 */
	synchronized boolean[][] search(Color color) {
		searched = new boolean[board.length][board[0].length];
		flip = new boolean[board.length][board[0].length];
		
		search(color, 0, 0);
		
		return flip;
	}
	
	/**
	 * Recursive method that searches the board, marking slots
	 * to be changed.
	 * @param color color to search for
	 * @param x x-coordinate to look at
	 * @param y y-coordinate to look at
	 */
	private void search(Color color, int x, int y) {
		if (! inBounds(x, y) || searched[x][y])
			return;
		
		searched[x][y] = true;
		
		if (board[x][y] == board[0][0]) {
			flip[x][y] = true;
			
			search(color, x + 1, y);
			search(color, x - 1, y);
			search(color, x, y + 1);
			search(color, x, y - 1);
		}
	}
	
	/**
	 * Determines if the given color is a valid move. A move is valid
	 * only when the color can be reached adjacently by traveling
	 * along the grid spaces with the same color as the upper-left
	 * color.
	 * @param color color to check
	 * @return true if the move is valid
	 */
	public synchronized boolean isMoveValid(Color color) {
		searched = new boolean[board.length][board[0].length];
		
		if (color == board[0][0])
			return false;
		
		return isMoveValid(color, 0, 0);
	}
	
	/**
	 * Recursive method that searches the board, checking if a
	 * move is valid.
	 * @param color color move to check
	 * @param x x-coordinate to look at
	 * @param y y-coordinate to look at
	 * @return true if a move is valid from this space
	 */
	private boolean isMoveValid(Color color, int x, int y) {
		if (searched[x][y])
			return false;
		
		searched[x][y] = true;
		
		if (board[x][y] != board[0][0])
			return false;
		
		if (inBounds(x + 1, y))
			if (board[x + 1][y] == color || isMoveValid(color, x + 1, y))
				return true;
		
		if (inBounds(x - 1, y))
			if (board[x - 1][y] == color || isMoveValid(color, x - 1, y))
				return true;
		
		if (inBounds(x, y + 1))
			if (board[x][y + 1] == color || isMoveValid(color, x, y + 1))
				return true;
		
		if (inBounds(x, y - 1))
			if (board[x][y - 1] == color || isMoveValid(color, x, y - 1))
				return true;
		
		return false;
	}
	
	/**
	 * Determines the fitness of a given board. That is, how many colors
	 * are connected via same color as the upper-left corner. 
	 * @return number of colors that are connected via same color as the
	 * upper-left corner
	 */
	public synchronized int fitness() {
		searched = new boolean[board.length][board[0].length];
		
		return fitness(0, 0);
	}
	
	/**
	 * Recursive method that searches the board, determining fitness.
	 * @param x x-coordinate to look at
	 * @param y y-coordinate to look at
	 * @return fitness of this coordinate pair
	 */
	private	int fitness(int x, int y) {
		if (! inBounds(x, y) || searched[x][y])
			return 0;
		
		searched[x][y] = true;
		
		if (board[x][y] != board[0][0])
			return 0;
		
		return 1 +
				fitness(x + 1, y) +
				fitness(x - 1, y) +
				fitness(x, y + 1) +
				fitness(x, y - 1);
	}
	
	/**
	 * Determines if a given coordinate pair is in bounds.
	 * @param x x-coordinate to check
	 * @param y y-coordinate to check
	 * @return true if the coordinate pair is in bounds
	 */
	private boolean inBounds(int x, int y) {
		return x >= 0 && x < board.length &&
				y >= 0 && y < board[x].length;
	}
}
