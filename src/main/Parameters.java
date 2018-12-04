package main;

/**
 * This class holds static program parameters and handles setting
 * the game parameters based on command line input, or using default values.
 * @author Gage Davidson
 */
public class Parameters {
	
	public static final int DISPLAY_SIZE = 900;
	public static final int DISPLAY_FPS = 5;
	public static final int DRAW_CELL_BORDER_WIDTH = 1;
	public static final int SOLUTION_DISPLAY_FPS = 3;
	
	/**
	 * Program parameters which aren't changed.
	 */
	private static int gameBoardSize, gameCellDrawSize, gameColors, searchDepth, maxThreads;
	private static boolean doSearch, displaySolution, solveRemote;
	private static String[] args;
	
	private static final int GAME_BOARD_SIZE_DEFAULT = 10;
	private static final int GAME_COLORS_DEFAULT = 4;
	private static final boolean DO_SEARCH_DEFAULT = true;
	private static final int SEARCH_DEPTH_DEFAULT = 4;
	private static final int MAX_THREADS_DEFAULT = 10;
	private static final boolean DISPLAY_SOLUTION_DEFAULT = true;
	private static final boolean SOLVE_REMOTE_DEFAULT = false;
	
	/**
	 * Cannot be instantiated.
	 */
	private Parameters() {
	}
	
	public static int gameBoardSize() { return gameBoardSize; }
	public static int gameCellDrawSize() { return gameCellDrawSize; }
	public static int gameColors() { return gameColors; }
	public static boolean doSearch() { return doSearch; }
	public static int searchDepth() { return searchDepth; }
	public static int maxThreads() { return maxThreads; }
	public static boolean displaySolution() { return displaySolution; }
	public static boolean solveRemote() { return solveRemote; }
	public static String[] getArgs() { return args; }
	
	/**
	 * Initializes all the program parameters
	 * @param args command line arguments
	 */
	public static void setParameters(String[] args) {
		Parameters.args = args;
		
		if (args.length > 0 && args[0].equals("-help"))
			printHelp();
		
		gameBoardSize = getBoardSize(args);
		gameCellDrawSize = DISPLAY_SIZE / gameBoardSize;
		gameColors = getGameColors(args);
		doSearch = getDoSearch(args);
		searchDepth = getSearchDepth(args);
		maxThreads = getMaxThreads(args);
		displaySolution = getDisplaySolution(args);
		solveRemote = getSolveRemote(args);
	}
	
	/**
	 * Prints usage parameters and terminates.
	 */
	private static void printHelp() {
		System.out.println("Optionally use the following flags:");
		System.out.println("-bs <boardSize>        (an integer)");
		System.out.println("-gc <gameColors>       (an integer)");
		System.out.println("-search <doSearch>     (a boolean)");
		System.out.println("-sd <searchDepth>      (an integer)");
		System.out.println("-t  <threadCount>      (an integer)");
		System.out.println("-ds <displaySolution>  (a boolean)");
		System.out.println("-rem <solveRemote>     (a boolean)");
		System.out.println("Example usage: java -jar Flood.jar -bs 10 -gc 4 -search true -sd 4 -t 10 -ds true -rem false");
		
		System.exit(0);
	}
	
	/**
	 * Searches command line arguments for "-bs" flag. If one doesn't
	 * exist, returns 10.
	 * @param args command line arguments
	 * @return board size to use
	 */
	private static int getBoardSize(String[] args) {
		for (int i = 0; i < args.length; ++i) {
			if (args[i].equals("-bs")) {
				try {
					return Integer.parseInt(args[i + 1]);
				} catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
					invalidUsage();
				}
			}
		}
		
		return GAME_BOARD_SIZE_DEFAULT;
	}
	
	/**
	 * Searches command line arguments for "-gc" flag. If one doesn't
	 * exist, returns 4.
	 * @param args command line arguments
	 * @return game colors to use
	 */
	private static int getGameColors(String[] args) {
		for (int i = 0; i < args.length; ++i) {
			if (args[i].equals("-gc")) {
				try {
					return Integer.parseInt(args[i + 1]);
				} catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
					invalidUsage();
				}
			}
		}
		
		return GAME_COLORS_DEFAULT;
	}
	
	/**
	 * Searches command line arguments for "-search" flag. If one doesn't
	 * exist, returns true.
	 * @param args command line arguments
	 * @return true if the program should perform the search
	 */
	private static boolean getDoSearch(String[] args) {
		for (int i = 0; i < args.length; ++i) {
			if (args[i].equals("-search")) {
				String boolString = "";
				
				try {
					boolString = args[i + 1];
				} catch (ArrayIndexOutOfBoundsException ex) {
					invalidUsage();
				}
				
				if (boolString.equalsIgnoreCase("true"))
					return true;
				else if (boolString.equalsIgnoreCase("false"))
					return false;
				else
					invalidUsage();
			}
		}
		
		return DO_SEARCH_DEFAULT;
	}
	
	/**
	 * Searches command line arguments for "-sd" flag. If one doesn't
	 * exist, returns 4.
	 * @param args command line arguments
	 * @return search depth
	 */
	private static int getSearchDepth(String[] args) {
		for (int i = 0; i < args.length; ++i) {
			if (args[i].equals("-sd")) {
				try {
					return Integer.parseInt(args[i + 1]);
				} catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
					invalidUsage();
				}
			}
		}
		
		return SEARCH_DEPTH_DEFAULT;
	}
	
	/**
	 * Searches command line arguments for "-t" flag. If one doesn't
	 * exist, returns 10.
	 * @param args command line arguments
	 * @return maximum threads to use for search
	 */
	private static int getMaxThreads(String[] args) {
		for (int i = 0; i < args.length; ++i) {
			if (args[i].equals("-t")) {
				try {
					return Integer.parseInt(args[i + 1]);
				} catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
					invalidUsage();
				}
			}
		}
		
		return MAX_THREADS_DEFAULT;
	}
	
	/**
	 * Searches command line arguments for "-ds" flag. If one doesn't
	 * exist, returns true.
	 * @param args command line arguments
	 * @return true if the program should graphically display the solution
	 */
	private static boolean getDisplaySolution(String[] args) {
		for (int i = 0; i < args.length; ++i) {
			if (args[i].equals("-ds")) {
				String boolString = "";
				
				try {
					boolString = args[i + 1];
				} catch (ArrayIndexOutOfBoundsException ex) {
					invalidUsage();
				}
				
				if (boolString.equalsIgnoreCase("true"))
					return true;
				else if (boolString.equalsIgnoreCase("false"))
					return false;
				else
					invalidUsage();
			}
		}
		
		return DISPLAY_SOLUTION_DEFAULT;
	}
	
	/**
	 * Searches command line arguments for "-rem" flag. If one doesn't
	 * exist, returns false.
	 * @param args command line arguments
	 * @return true if the program should perform the search
	 */
	private static boolean getSolveRemote(String[] args) {
		for (int i = 0; i < args.length; ++i) {
			if (args[i].equals("-rem")) {
				String boolString = "";
				
				try {
					boolString = args[i + 1];
				} catch (ArrayIndexOutOfBoundsException ex) {
					invalidUsage();
				}
				
				if (boolString.equalsIgnoreCase("true"))
					return true;
				else if (boolString.equalsIgnoreCase("false"))
					return false;
				else
					invalidUsage();
			}
		}
		
		return SOLVE_REMOTE_DEFAULT;
	}
	
	/**
	 * Informs the user the usage was invalid and exits the program.
	 */
	private static void invalidUsage() {
		System.out.println("Invalid usage. Use -help for help.");
		System.exit(-1);
	}
}
