package search;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import game.Color;
import game.SearchBoard;
import main.Parameters;

/**
 * This class handles the search for the solution.
 * 
 * @author Gage Davidson
 */
public class Search implements Runnable {
	
	AtomicInteger nameGenerator;
	ConcurrentLinkedQueue<Node> completedNodes;
	int permittedSearchDepth;
	AtomicInteger searchCounter;
	
	private Node root;
	private ExecutorService exec;
	private Node solutionLeaf;
	
	/**
	 * @param board Board to use for root node
	 */
	public Search(Color[][] board) {
		nameGenerator = new AtomicInteger();
		completedNodes = new ConcurrentLinkedQueue<>();
		permittedSearchDepth = Parameters.searchDepth();
		searchCounter = new AtomicInteger();
		
		initRoot(board);
		
		exec = Executors.newFixedThreadPool(Parameters.maxThreads());
	}
	
	/**
	 * @param node root node of the search
	 */
	public Search(Node node) {
		nameGenerator = new AtomicInteger();
		nameGenerator.set(node.id + 1);
		
		completedNodes = new ConcurrentLinkedQueue<>();
		permittedSearchDepth = node.moves + Parameters.searchDepth();
		searchCounter = new AtomicInteger();
		root = node;
		exec = Executors.newFixedThreadPool(Parameters.maxThreads());
		
		root.search = this;
	}
	
	/**
	 * Initializes the root node.
	 * @param board to start with
	 */
	private void initRoot(Color[][] board) {
		root = new Node(this);
		root.id = -1;
		root.parent = null;
		root.move = null;
		root.board = board;
		root.moves = 0;
		root.goal = false;
		
		SearchBoard searchBoard = new SearchBoard(root.board);
		root.fitness = searchBoard.fitness();
	}
	
	/**
	 * Runs the search in parallel.
	 */
	@Override
	public void run() {
		searchCounter.incrementAndGet();
		Node.generateAndRunChildren(exec, root);
		
		for (;;) {
			waitForSearchHalt();
			
			Node fittestNode = fittestNode();
			
			System.out.printf("Fittest node has %d moves with fittness %d\n",
					fittestNode.moves, fittestNode.fitness);
			
			if (fittestNode.goal) {
				solutionLeaf = fittestNode;
				fittestNode.board = null;
				break;
			}
			
			permittedSearchDepth += Parameters.searchDepth();
			
			searchCounter.incrementAndGet();
			Node.generateAndRunChildren(exec, fittestNode);
		}
		
		System.out.println("Search is complete.");
		if (Parameters.displaySolution())
			solutionLeaf.displaySolution();
		
		exec.shutdown();
		try {
			exec.awaitTermination(2, TimeUnit.DAYS);
		} catch (InterruptedException ex) {
		}
	}
	
	/**
	 * Blocks execution until threads are finished searching.
	 */
	private void waitForSearchHalt() {
		for (;;) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException ex) {
			}
			
			if (searchCounter.get() == 0)
				return;
			
			System.out.printf("Awaiting search halt, size = %d, counter = %d\n", completedNodes.size(), searchCounter.get());
		}
	}
	
	/**
	 * @return fittest node in the queue
	 */
	private Node fittestNode() {
		if (containsGoalBoard())
			return fittestMovesNode();
		
		return fittestFitnessNode();
	}
	
	/**
	 * @return fittest node based on fitness
	 */
	private Node fittestFitnessNode() {
		Node fittest = null;
		
		while (! completedNodes.isEmpty()) {
			Node node = completedNodes.poll();
			
			if (fittest == null) {
				fittest = node;
				continue;
			}
			
			if (node.fitness > fittest.fitness)
				fittest = node;
		}
		
		return fittest;
	}
	
	/**
	 * @return fittest node based on moves
	 */
	private Node fittestMovesNode() {
		Node fittest = null;
		
		while (! completedNodes.isEmpty()) {
			Node node = completedNodes.poll();
			
			if (! node.goal)
				continue;
			
			if (fittest == null) {
				fittest = node;
				continue;
			}
			
			if (node.moves < fittest.moves)
				fittest = node;
		}
		
		return fittest;
	}
	
	/**
	 * @return true if the queue contains a goal-state board
	 */
	private boolean containsGoalBoard() {
		for (Node node : completedNodes)
			if (node.goal)
				return true;
		
		return false;
	}
	
	/**
	 * @return leaf node of the solution
	 */
	public Node getSolutionLeaf() {
		return solutionLeaf;
	}
}
