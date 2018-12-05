package search;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import game.Color;
import game.SearchBoard;
import main.Parameters;

/**
 * This class is run on a local machine and handles the
 * search for the solution. The search is done remotely
 * on other computers.
 * 
 * @author Gage Davidson
 */
public class RemoteSearch implements Runnable {
	
	public static final String HOST = "129.3.20.26";
	public static final int PORT = 2600;
	
	private Node root;
	private Node[] leaves;
	private AtomicInteger leafIndex;
	private ConcurrentLinkedQueue<Node> completedNodes;
	private AtomicInteger startedSolvers, finishedSolvers;
	private Node solutionLeaf;
	
	public RemoteSearch(Color[][] board) {
		initRoot(board);
	}
	
	/**
	 * Initializes the root node.
	 * @param board to start with
	 */
	private void initRoot(Color[][] board) {
		root = new Node(null);
		root.id = -1;
		root.parent = null;
		root.move = null;
		root.board = board;
		root.moves = 0;
		root.goal = false;
		
		SearchBoard searchBoard = new SearchBoard(root.board);
		root.fitness = searchBoard.fitness();
	}
	
	@Override
	public void run() {
		leaves = generateLeaves();
		System.out.println(leaves.length + " initial leaves");
		
		leafIndex = new AtomicInteger();
		completedNodes = new ConcurrentLinkedQueue<>();
		startedSolvers = new AtomicInteger();
		finishedSolvers = new AtomicInteger();
		
		try (ServerSocket serverSocket = new ServerSocket(PORT)) {
			while (startedSolvers.get() < leaves.length)
				handleClient(serverSocket.accept());
			
			awaitSolvers();
		} catch (IOException ex) {
			System.out.println("IOException: " + ex.getMessage());
		}
		
		System.out.println("Server stopped");
		
		solutionLeaf = fittestMovesNode();
		
		System.out.println("\nBest solution:");
		solutionLeaf.displaySolution();
	}
	
	/**
	 * Generates the initial leaves which will be sent to the
	 * remote solvers. This array will be length 4+.
	 * @return array of initial leaves to be sent to remote solvers
	 */
	private Node[] generateLeaves() {
		root.generateChildren();
		
		Node[] leaves = getLeaves();
		
		do {
			for (int i = 0; i < leaves.length; ++i)
				leaves[i].generateChildren();
			
			leaves = getLeaves();
		} while (leaves.length < 4);
		
		setSuit(leaves);
		return leaves;
	}
	
	/**
	 * Sets the suit of all the nodes in an array based on their
	 * position in the array.
	 * @param leaves nodes to suit
	 */
	private static void setSuit(Node[] leaves) {
		for (int i = 0; i < leaves.length; ++i)
			leaves[i].suit = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(i);
	}
	
	/**
	 * Generates an array of nodes which are the leaves of the tree.
	 * @return array of leaves
	 */
	private Node[] getLeaves() {
		ArrayList<Node> leaves = new ArrayList<>();
		getLeaves(leaves, root);
		
		Node[] leavesArray = new Node[leaves.size()];
		
		for (int i = 0; i < leaves.size(); ++i)
			leavesArray[i] = leaves.get(i);
		
		return leavesArray;
	}
	
	/**
	 * Recursive helper method for finding the leaves of the tree.
	 * @param nodes list of leaves
	 * @param node node to look at
	 */
	private static void getLeaves(ArrayList<Node> nodes, Node node) {
		if (node.children == null) {
			nodes.add(node);
			return;
		}
		
		for (Node n : node.children) {
			if (n == null)
				continue;
			
			getLeaves(nodes, n);
		}
	}
	
	/**
	 * Accepts a client socket and runs a new thread to
	 * handle it.
	 * @param clientSocket client socket
	 */
	private void handleClient(Socket clientSocket) {
		System.out.println("Accepting socket " + clientSocket);
		
		startedSolvers.incrementAndGet();
		System.out.println("Starting solver " + startedSolvers.get());
		
		new Thread(() -> {
			try (ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
					ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {
				
				communicateClient(out, in);
				
			} catch (IOException ex) {
				System.out.println("IOException while handling client: " + ex.getMessage());
			} catch (ClassNotFoundException ex) {
				System.out.println("ClassNotFounException while handling client: " + ex.getMessage());
			} finally {
				try {
					clientSocket.close();
				} catch (IOException ex) {
					System.out.println("Failed to close client socket: " + ex.getMessage());
				}
			}
			
			finishedSolvers.incrementAndGet();
			System.out.println(finishedSolvers.get() + " solvers done");
		}).start();
	}
	
	/**
	 * Performs the communication with the client, receiving a
	 * solution node at the end
	 * @param out oos
	 * @param in ois
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void communicateClient(ObjectOutputStream out, ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		// send program parameters
		out.writeObject(Parameters.getArgs());
		
		// send initial node
		int leafIndex = this.leafIndex.getAndIncrement();
		Node leafNode = leaves[leafIndex];
		out.writeObject(leafNode);
		
		// receive solution node from remote solver
		Node solutionLeaf = (Node) in.readObject();
		completedNodes.add(solutionLeaf);
	}
	
	/**
	 * Waits for all remote solvers to finish.
	 */
	private void awaitSolvers() {
		while (finishedSolvers.get() < leaves.length) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException ex) {
				System.out.println("InterruptedException while awaiting solvers: " + ex.getMessage());
			}
		}
	}
	
	/**
	 * @return fittest node based on moves
	 */
	private Node fittestMovesNode() {
		Node fittest = null;
		
		while (! completedNodes.isEmpty()) {
			Node node = completedNodes.poll();
			
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
	 * @return solutionLeaf
	 */
	public Node getSolutionLeaf() {
		return solutionLeaf;
	}
}
