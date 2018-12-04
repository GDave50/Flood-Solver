package search;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ExecutorService;

import game.Board;
import game.Color;
import game.SearchBoard;
import main.Parameters;

/**
 * This class represents a single search node.
 * @author Gage Davidson
 */
public class Node implements Serializable {
	
	int id;
	char suit;
	Node parent;
	Node[] children;
	Color move;
	Color[][] board;
	int moves;
	int fitness;
	boolean goal;
	
	Search search;
	
	Node(Search search) {
		this.search = search;
	}
	
	/**
	 * Generates all this node's children.
	 */
	void generateChildren() {
		children = new Node[Parameters.gameColors()];
		SearchBoard searchBoard = new SearchBoard(board);
		
		for (int color = 0; color < Parameters.gameColors(); ++color) {
			Color colorMove = Color.COLORS[color];
			
			if (searchBoard.isMoveValid(colorMove)) {
				Node node = new Node(search);
				if (search != null) node.id = search.nameGenerator.getAndIncrement();
				node.suit = suit;
				node.parent = this;
				node.move = colorMove;
				node.board = Board.applyMove(board, colorMove);
				node.moves = moves + 1;
				node.fitness = new SearchBoard(node.board).fitness();
				node.goal = Board.goalBoard(node.board);
				
				children[color] = node;
			}
		}
		
		board = null;
	}
	
	/**
	 * Recursively call this on all children (and their children, subsequently)
	 * until the search depth has been reached. Once search depth is reached or
	 * goal board is found, add best child (or this node) to fittest list.
	 * @param exec executor to use
	 */
	void runChildren(ExecutorService exec) {
		if (goal) {
			search.completedNodes.add(this);
			return;
		}
		
		if (moves >= search.permittedSearchDepth - 1) {
			Node fittestChild = fittestChild();
			
			if (fittestChild != null)
				search.completedNodes.add(fittestChild);
			
			return;
		}
		
		for (int child = 0; child < children.length; ++child) {
			final int _child = child;
			
			if (children[_child] == null)
				continue;
			
			search.searchCounter.incrementAndGet();
			
			exec.submit(new Runnable() {
				@Override
				public void run() {
					generateAndRunChildren(exec, children[_child]);
				}
			});
		}
	}
	
	/**
	 * Generates this node's children and runs them.
	 * @param exec executor to use
	 * @param node node to generate and run children for
	 */
	static void generateAndRunChildren(ExecutorService exec, Node node) {
		node.generateChildren();
		node.runChildren(exec);
		node.search.searchCounter.decrementAndGet();
	}
	
	/**
	 * Determines which of this node's children is fittest.
	 * @return fittest child
	 */
	private Node fittestChild() {
		Node fittest = null;
		
		for (int child = 0; child < children.length; ++child) {
			if (fittest == null) {
				fittest = children[child];
				continue;
			}
			
			if (children[child] == null)
				continue;
			
			if (children[child].fitness > fittest.fitness)
				fittest = children[child];
		}
		
		return fittest;
	}
	
	/**
	 * Recursively display the solution.
	 */
	void displaySolution() {
		System.out.println(this);
		
		if (parent == null)
			return;
		
		try {
			Thread.sleep(25);
		} catch (InterruptedException ex) {
		}
		
		parent.displaySolution();
	}
	
	@Override
	public String toString() {
		if (parent == null)
			return String.format("(%c%d | root %s %s %d %d)",
					suit, id, getChildrenString(), move, moves, fitness);
			
		return String.format("(%c%d | %d %s %s %d %d)",
				suit, id, parent.id, getChildrenString(), move, moves, fitness);
	}
	
	/**
	 * @return a String representation of the children (ex. "[1 2 3]")
	 */
	private String getChildrenString() {
		if (children == null || children.length == 0)
			return "[]";
		
		String s = "[";
		
		for (int child = 0; child < children.length; ++child) {
			if (children[child] != null) {
				s += children[child].id + " ";
			}
		}
		
		return s.substring(0, s.length() - 1) + "]";
	}
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(id);
		out.writeChar(suit);
		out.writeObject(parent);
		out.writeObject(children);
		out.writeObject(move);
		out.writeObject(board);
		out.writeInt(moves);
		out.writeInt(fitness);
		out.writeBoolean(goal);
	}
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		id = in.readInt();
		suit = in.readChar();
		parent = (Node) in.readObject();
		children = (Node[]) in.readObject();
		move = (Color) in.readObject();
		board = (Color[][]) in.readObject();
		moves = in.readInt();
		fitness = in.readInt();
		goal = in.readBoolean();
	}
}
