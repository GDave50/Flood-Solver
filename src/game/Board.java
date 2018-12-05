package game;

import java.awt.Graphics;
import java.util.concurrent.ThreadLocalRandom;

import main.Parameters;

/**
 * This class holds static functions for operating on a game board.
 * 
 * @author Gage Davidson
 */
public class Board {
	
	/**
	 * Cannot be instantiated.
	 */
	private Board() {
	}
	
	/**
	 * Initializes a game board to a random arrangement of colors.
	 */
	public static Color[][] initRandom() {
		Color[][] board = new Color[Parameters.gameBoardSize()][Parameters.gameBoardSize()];
		
		for (int x = 0; x < Parameters.gameBoardSize(); ++x)
			for (int y = 0; y < Parameters.gameBoardSize(); ++y)
				board[x][y] = Color.COLORS[ThreadLocalRandom.current().nextInt(Parameters.gameColors())];
		
		return board;
	}
	
	/**
	 * Applies a move to the given board. This method assumes the
	 * move given is valid.
	 * @param board board to apply move to
	 * @param color color to use as move
	 * @return board after applied move
	 */
	public static Color[][] applyMove(Color[][] board, Color color) {
		SearchBoard searchBoard = new SearchBoard(board);
		boolean[][] flip = searchBoard.search(color);
		
		Color[][] newBoard = new Color[board.length][board[0].length];
		
		for (int x = 0; x < board.length; ++x)
			for (int y = 0; y < board[x].length; ++y)
				if (flip[x][y])
					newBoard[x][y] = color;
				else
					newBoard[x][y] = board[x][y];
		
		return newBoard;
	}
	
	/**
	 * Draws the board with the given Graphics.
	 * @param board game board
	 * @param g Graphics to use
	 */
	public static void drawBoard(Color[][] board, Graphics g) {
		for (int x = 0; x < board.length; ++x) {
			for (int y = 0; y < board[x].length; ++y) {
				g.setColor(board[x][y].color);
				g.fillRect(x * Parameters.gameCellDrawSize() + Parameters.DRAW_CELL_BORDER_WIDTH,
						y * Parameters.gameCellDrawSize() + Parameters.DRAW_CELL_BORDER_WIDTH,
						Parameters.gameCellDrawSize() - Parameters.DRAW_CELL_BORDER_WIDTH * 2,
						Parameters.gameCellDrawSize() - Parameters.DRAW_CELL_BORDER_WIDTH * 2);
			}
		}
	}
	
	/**
	 * Determines if the given board is in the winning state.
	 * @param board board to check
	 * @return true if the board is in the winning state
	 */
	public static boolean goalBoard(Color[][] board) {
		for (int x = 0; x < board.length; ++x)
			for (int y = 0; y < board[x].length; ++y)
				if (board[x][y] != board[0][0])
					return false;
		
		return true;
	}
	
	/**
	 * Copies the given board.
	 * @param board board to copy
	 * @return copied board
	 */
	public static Color[][] copyBoard(Color[][] board) {
		Color[][] boardCopy = new Color[board.length][board[0].length];
		
		for (int x = 0; x < board.length; ++x)
			for (int y = 0; y < board[x].length; ++y)
				boardCopy[x][y] = board[x][y];
		
		return boardCopy;
	}
	
	/**
	 * @param board board to make string of
	 * @return string representation of the board
	 */
	public static String toString(Color[][] board) {
		String comp = "";
		
		for (int x = 0; x < board.length; ++x) {
			comp += "[";
			
			for (int y = 0; y < board[x].length; ++y)
				comp += board[x][y].nameChar + " ";
			
			comp = comp.substring(0, comp.length() - 1);
			comp += "]\n";
		}
		
		comp = comp.substring(0, comp.length() - 1);
		
		return comp;
	}
}
