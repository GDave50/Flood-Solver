package game;

/**
 * Possible colors for the game.
 * 
 * @author Gage Davidson
 */
public enum Color {
	
	RED      ("RED",      java.awt.Color.RED),
	BLUE     ("BLUE",     java.awt.Color.BLUE),
	GREEN    ("GREEN",    java.awt.Color.GREEN),
	YELLOW   ("YELLOW",   java.awt.Color.YELLOW),
	MAGENTA  ("MAGENTA",  java.awt.Color.MAGENTA),
	CYAN     ("CYAN",     java.awt.Color.CYAN);
	
	public static final Color[] COLORS = {
			RED, BLUE, GREEN, YELLOW, MAGENTA, CYAN };
	
	final String name;
	final char nameChar; // first letter of the color's name
	final java.awt.Color color; // AWT color correspondent
	
	Color(String name, java.awt.Color color) {
		this.name = name;
		this.nameChar = name.charAt(0);
		this.color = color;
	}
}
