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
 * This class will be used on remote machines. It will receive
 * a node remotely and find a solution based on that node and
 * then send the solution node back to the server.
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
				
			} catch (UnknownHostException ex) {
				System.out.println("Unknown host: " + ex.getMessage());
			} catch (IOException ex) {
				if (ex.getMessage().equals("Connection refused: connect")) {
					System.out.println("No server found");
					continue;
				}
				
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
}
