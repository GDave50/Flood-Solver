package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * Display to render the game.
 * @author Gage Davidson
 */
abstract class Display extends JPanel {
	
	Display() {
		setPreferredSize(new Dimension(Parameters.DISPLAY_SIZE, Parameters.DISPLAY_SIZE));
		
		JFrame frame = new JFrame("Flood Solver");
		frame.add(this);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent evt) {
				switch (evt.getKeyCode()) {
				case KeyEvent.VK_ESCAPE:  System.exit(0);  break;
				
				case KeyEvent.VK_1:  buttonPressed(1);  break;
				case KeyEvent.VK_2:  buttonPressed(2);  break;
				case KeyEvent.VK_3:  buttonPressed(3);  break;
				case KeyEvent.VK_4:  buttonPressed(4);  break;
				case KeyEvent.VK_5:  buttonPressed(5);  break;
				case KeyEvent.VK_6:  buttonPressed(6);  break;
				}
			}
		});
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent evt) {
				Display.this.mousePressed(evt.getX(), evt.getY());
			}
		});
		
		// wait for display to start
		try {
			Thread.sleep(200);
		} catch (InterruptedException ex) {
			System.err.println("IE in Display constructor: " + ex.getMessage());
		}
		
		requestFocus();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		render(g);
	}
	
	abstract void render(Graphics g);
	abstract void buttonPressed(int button);
	abstract void mousePressed(int xClick, int yClick);
}
