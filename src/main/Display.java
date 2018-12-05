package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * Display to render the game.
 * 
 * @author Gage Davidson
 */
abstract class Display extends JPanel {
	
	private static final String DISPLAY_TITLE = "Flood Solver";
	
	Display() {
		setPreferredSize(new Dimension(Parameters.DISPLAY_SIZE, Parameters.DISPLAY_SIZE));
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent evt) {
				Display.this.mousePressed(evt.getX(), evt.getY());
			}
		});
		
		JFrame frame = new JFrame(DISPLAY_TITLE);
		frame.add(this);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		requestFocus();
	}
	
	/**
	 * Called when repaint() is called on the display.
	 */
	@Override
	public void paintComponent(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		draw(g);
	}
	
	/**
	 * Draws on the panel.
	 * @param g Graphics to draw with
	 */
	abstract void draw(Graphics g);
	
	/**
	 * Called when the user presses the mouse on the panel.
	 * @param xClick x-coordinate of the click
	 * @param yClick y-coordinate of the click
	 */
	abstract void mousePressed(int xClick, int yClick);
}
