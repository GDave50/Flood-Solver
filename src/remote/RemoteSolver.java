package remote;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import main.Parameters;
import search.Node;
import search.RemoteSearch;
import search.Search;

/**
 * This class is used on remote machines. It receives a
 * node remotely and finds a solution based on that node and
 * then sends the solution node back to the server.
 * 
 * @author Gage Davidson
 */
class RemoteSolver {
	
	private RemoteSolver() {
	}
	
	/**
	 * @param unusedArgs command line arguments (unused)
	 */
	public static void main(String[] unusedArgs) {
		while (true) {
			try (Socket socket = new Socket(RemoteSearch.HOST, RemoteSearch.PORT);
					ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
					ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
				
				communicateServer(out, in);
				
			} catch (UnknownHostException ex) {
				System.out.println("Unknown host: " + ex.getMessage());
			} catch (IOException ex) {
				System.out.println("IOException: " + ex.getMessage());
			} catch (ClassNotFoundException ex) {
				System.out.println("Class not found: " + ex.getMessage());
			}
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ex) {
				System.out.println("InterruptedException: " + ex.getMessage());
			}
		}
	}
	
	/**
	 * Does the communication with the server. In the process it finds
	 * the solution to the node.
	 * @param out oos
	 * @param in ois
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private static void communicateServer(ObjectOutputStream out, ObjectInputStream in)
			throws ClassNotFoundException, IOException {
		// get and set parameters
		String[] args = (String[]) in.readObject();
		Parameters.setParameters(args);
		System.out.println("Parameters set");
		
		// receive initial node and find solution
		Node leafNode = (Node) in.readObject();
		Search search = new Search(leafNode);
		System.out.println("Node received; running search");
		search.run();
		
		// send solution to server
		Node solutionLeaf = search.getSolutionLeaf();
		out.writeObject(solutionLeaf);
		System.out.println("Solution leaf sent to server");
	}
}
