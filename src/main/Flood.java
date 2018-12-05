package main;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import game.Board;
import game.Color;
import game.UserGame;
import search.Node;
import search.RemoteSearch;
import search.Search;
import search.SolutionDisplay;

/**
 * Flood is a game in which the player is given a grid of randomly-
 * colored squares. The goal is to get the entire board to be one
 * color. This is done by flood-filling the top left corner with a
 * color, in which every square orthogonally connected by the same
 * color gets turned to the new color.
 * 
 * This program is an extension of the game in that it features a
 * parallel state space problem solver which attempts to find the
 * best solution for a given board.
 * 
 * Additionally, this program features a cluster-parallel state
 * space problem solver, which means some number of remote computers
 * help find a best solution. This is certainly more effective than
 * simply using one computer.
 * 
 * @author Gage Davidson
 */
public class Flood {
	
	static UserGame game;
	private static Color[][] gameBoard;
	private static SolutionDisplay solutionDisplay;
	
	/**
	 * @param args command line arguments
	 * 		-bs <boardSize>
	 * 		-gc <gameColors>
	 * 		-search <doSearch>
	 * 		-sd <searchDepth>
	 * 		-t <threads>
	 * 		-ds <displaySolution>
	 * 		-rem <solveRemote>
	 */
	public static void main(String[] args) {
		Parameters.setParameters(args);
		
		gameBoard = Board.initRandom();
		game = new UserGame(getInitialBoard());
		
		runDisplay();
		
		if (! Parameters.doSearch())
			return;
		
		Node solutionLeaf;
		
		if (Parameters.solveRemote()) {
			RemoteSearch remoteSearch = new RemoteSearch(getInitialBoard());
			remoteSearch.run();
			solutionLeaf = remoteSearch.getSolutionLeaf();
		} else {
			Search search = new Search(getInitialBoard());
			search.run();
			solutionLeaf = search.getSolutionLeaf();
		}
		
		if (Parameters.displaySolution()) {
			solutionDisplay = new SolutionDisplay(solutionLeaf);
			solutionDisplay.run();
		}
	}
	
	/**
	 * Initializes the display.
	 */
	private static void runDisplay() {
		Display display = new Display() {
			@Override
			public void draw(Graphics g) {
				if (Parameters.doSearch() && Parameters.displaySolution())
					drawSolution(g);
				else
					game.drawGame(g);
			}
			
			@Override
			public void mousePressed(int xClick, int yClick) {
				int xIndex = (int) ((double) xClick / Parameters.DISPLAY_SIZE * Parameters.gameBoardSize());
				int yIndex = (int) ((double) yClick / Parameters.DISPLAY_SIZE * Parameters.gameBoardSize());
				game.applyMove(xIndex, yIndex);
			}
		};
		
		new Timer(1000 / Parameters.DISPLAY_FPS, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				SwingUtilities.invokeLater(() -> display.repaint());
			}
		}).start();
	}
	
	/**
	 * Draws the solution, or the initial board if no solution has
	 * yet been found.
	 * @param g Graphics to use
	 */
	static void drawSolution(Graphics g) {
		if (solutionDisplay == null) {
			Board.drawBoard(gameBoard, g);
			return;
		}
		
		solutionDisplay.draw(g);
	}
	
	/**
	 * @return copy of the game board
	 */
	public static Color[][] getInitialBoard() {
		return Board.copyBoard(gameBoard);
	}
}
