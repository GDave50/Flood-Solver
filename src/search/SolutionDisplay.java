package search;

import java.awt.Graphics;

import game.Board;
import game.Color;
import main.Flood;
import main.Parameters;

/**
 * This class helps to make it easy to graphically display
 * the solution found by the search.
 * @author Gage Davidson
 */
public class SolutionDisplay implements Runnable {
	
	private Color[] solution;
	private Color[][] displayBoard; // the current frame's board
	
	/**
	 * @param leaf solution leaf
	 */
	public SolutionDisplay(Node leaf) {
		initSolution(leaf);
	}
	
	/**
	 * Initializes the array of solution nodes, putting them in correct
	 * order 0 to x.
	 * @param leaf solution leaf
	 */
	private void initSolution(Node leaf) {
		displayBoard = Flood.getInitialBoard();
		
		solution = new Color[leaf.moves];
		
		Node node = leaf;
		int i = node.moves - 1;
		
		while (node.parent != null) {
			solution[i--] = node.move;
			node = node.parent;
		}
	}
	
	/**
	 * Run the solution display. Use draw() to render.
	 */
	@Override
	public void run() {
		for (int i = 0; i < solution.length; ++i) {
			displayBoard = Board.applyMove(displayBoard, solution[i]);
			
			try {
				Thread.sleep(1000 / Parameters.SOLUTION_DISPLAY_FPS);
			} catch (InterruptedException ex) {
			}
		}
	}
	
	/**
	 * Draws the current solution frame.
	 * @param g Graphics to use
	 */
	public void draw(Graphics g) {
		Board.drawBoard(displayBoard, g);
	}
}
