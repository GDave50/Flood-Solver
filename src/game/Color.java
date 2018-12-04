package game;

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
	final char nameChar;
	final java.awt.Color color;
	
	Color(String name, java.awt.Color color) {
		this.name = name;
		this.nameChar = name.charAt(0);
		this.color = color;
	}
}
