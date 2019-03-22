package gameCode;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.net.URI;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 * This is my first independent project to practice using Java Swing, interconnected-
 * classes, MVC design patterns and some other fuindamentals of Java and Object-Oriented 
 * Programming. This class serves as the driver for the GUI in which two players can play a game
 * of Ultimate Tic-Tac-Toe, which is slightly more complex and way more fun than
 * regular tic-tac-toe. I implemented the rules based on Wikipedia's description
 * of the game, so check that article if you have any questions about the game itself.
 * 
 * @author Sahir Mody
 */
public class UltimateTicTacToe {
	
	/*
	 * The view is the JPanel that will be displayed in the GUI. It is final
	 * because we never change the panel in the frame, only the contents within
	 * the panel.
	 */
	private final View view;
	
	/*
	 * The model contains the data on the state of the game and keeps track of 
	 * the moves made.
	 */
	private Model model;
	
	/*
	 * These strings define the names of the two players and can be changed.
	 */
	private String player1 = "X";
	private String player2 = "O";
	
	/*
	 * These Color objects represents the current color scheme, which 
	 * defines how the board will look in terms of color on GUI.
	 */ 
	private Color background = Color.WHITE;
	private Color borderLines = Color.BLACK;
	private Color openSquares = Color.GREEN;
	
	/*
	 * These strings hold the various prompts that will come up if a player
	 * wishes to change some setting of the game.
	 */
	private final static String TITLE = "Ultimate Tic-Tac-Toe!";
	private final static String TURN_PROMPT = " Who's Turn Is It?";
	private final static String PLAYER_PROMPT = " Set Player Names ";
	private final static String COLOR_PROMPT = " Change Color Scheme ";
	private final static String UNDO_PROMPT = " Undo ";
	private final static String RESET_PROMPT = " Restart Game ";
	private final static String HELP_PROMPT = " Help ";
	
	/**
	 * Constructor that instantiates the view with the default color scheme,
	 * instantiates the model, and sets up the structure of the GUI window. It
	 * also creates the action listeners needed for the buttons to work properly
	 * and the mouse listener for the gameplay.
	 */
	public UltimateTicTacToe() {
		
		
		view = new View(background, borderLines, openSquares);
		
		model = view.getModel();
		
		/*
		 * Instantiates the frame, menu bar, and menu items, the essential parts
		 * of the GUI.
		 */
		JFrame frame = new JFrame(TITLE);
		JMenuBar menuBar = new JMenuBar();
		JMenuItem[] menu = {new JMenuItem(TURN_PROMPT),
						  new JMenuItem(PLAYER_PROMPT),
						  new JMenuItem(COLOR_PROMPT),
						  new JMenuItem(UNDO_PROMPT),
						  new JMenuItem(RESET_PROMPT),
						  new JMenuItem(HELP_PROMPT)};
		
		
		/*
		 * Defines the ActionListener that will be used for each of the menu items
		 * so that they can respond to clicks.
		 */
		ActionListener listener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				/*
				 * Based on the menu item clicked, a different method or output is called.
				 */
				
				if(e.getActionCommand().equals(TURN_PROMPT)) {
					
					String currentPlayer = model.getExTurn() ? player1 : player2;
					
					JOptionPane.showMessageDialog(null, "It's "+ currentPlayer + "'s turn!", TITLE, JOptionPane.DEFAULT_OPTION);
					
				} else if(e.getActionCommand().equals(PLAYER_PROMPT)) {
					
					playerNameSetup();
					
				} else if (e.getActionCommand().equals(COLOR_PROMPT)) {
					
					colorSetup();
					
					view.setColors(background, borderLines, openSquares);
					
				} else if (e.getActionCommand().equals(UNDO_PROMPT)) {
					
					model.undoLastMove();
					
				} else if (e.getActionCommand().equals(RESET_PROMPT)) {
					
					model = new Model(view);
					
				} else if(e.getActionCommand().equals(HELP_PROMPT)) {
					
					displayInstructionPrompt();
					
				}
				
				view.update(model);
				
			}
			
		};
		
		
		/*
		 * For each JMenuItem, attach a listener so that it can respond to clicks
		 * and then add it to the menu bar.
		 */
		for(JMenuItem item : menu) {
			
			item.addActionListener(listener);
			menuBar.add(item);
			
		}
		
		
		/*
		 * Allow the JPanel to respond to mouse events so players can actually play.
		 */
		view.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
				/*
				 * Only respond to mouse events if the the game is still going
				 */
				if(model.winnerOfSquare(model.getSuperSquareWinners()) == Model.EMPTY) {
					
					
					int superSquare = view.getSuperSquare(e.getX(), e.getY());
					int subSquare = view.getSubSquare(e.getX(), e.getY(), superSquare);
					
					if(subSquare < 9 &&
					   model.isPlayable(superSquare, subSquare) &&
					   (superSquare == model.getCurrentSuperSquare() ||
						model.getCurrentSuperSquare() == Model.ERROR)) {
						
						/*
						 * Only draw X or O if the move has been made in a valid,
						 * empty subsquare in a supersquare that has not been one.
						 */
						model.placeMarker(superSquare, subSquare);
						
					} else {
						
						/*
						 * Error message if player attempted an invalid move.
						 */
						JOptionPane.showMessageDialog(null, "You can only play in the highlighted squares!", 
			       				  "Invalid Move!", JOptionPane.PLAIN_MESSAGE);
						
					}
					
					/*
					 * If the game has ended after the move, output a final message declaring winner or draw.
					 */
					if(model.winnerOfSquare(model.getSuperSquareWinners()) != Model.EMPTY) {

						int winner = model.winnerOfSquare(model.getSuperSquareWinners());

						String winningPlayer = (winner == Model.EX) ? player1 : player2;

						JOptionPane.showMessageDialog(null, winningPlayer + " wins!", 
								"Game Over!", JOptionPane.PLAIN_MESSAGE);

					} else if (model.movesAvailable() == false){

						JOptionPane.showMessageDialog(null, "It's a Draw!", 
								"Game Over!", JOptionPane.PLAIN_MESSAGE);

					}

				} 
				
			}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}	
			
		});
		
		
		view.setFocusable(true);
	
		frame.getContentPane().add(view);
		
		frame.setJMenuBar(menuBar);
		
		frame.setSize(View.WIDTH, View.HEIGHT);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // end program if GUI is closed
		
		frame.setResizable(false); // panel works for a specific size, so the frame should not be resized
		
		frame.setLocationRelativeTo(null); // frame will appear in the center of the screen
		
		frame.setVisible(true);	
		
	}
	
	/*
	 * Allows for the user to change the names of the  players.
	 */
	private void playerNameSetup() {
		
		/*
		 * Uses JOptionPane to get text input from the user and store is as the
		 * first player's name.
		 */
		player1 = JOptionPane.showInputDialog(null, "Enter Player 1's Name: ", TITLE, JOptionPane.PLAIN_MESSAGE);
		
		if(player1 == null) {
			
			player1 = "X";
			
		}
		
		
		/*
		 * Repeats process above for the second player.
		 */
		player2 = JOptionPane.showInputDialog(null, "Enter Player 2's Name: ", TITLE, JOptionPane.PLAIN_MESSAGE);
		
		if(player2 == null) {
			
			player2 = "O";
			
		}
		
	}
	
	
	/*
	 * Provides a menu for the user to change the color scheme to a different
	 * setting.
	 */
	private void colorSetup() {
		
		String[] colorOptions = {"Halloween", "#GoTerps", "Inverted", "Standard"};
		
		int choice = JOptionPane.showOptionDialog(null, 
			"Choose a Color Scheme:", TITLE, JOptionPane.DEFAULT_OPTION, 
			JOptionPane.PLAIN_MESSAGE, null, colorOptions, colorOptions[3]);
		
		switch(choice) {
		
		case 0:
			/*
			 * Halloween color scheme, with orange and black as classic Halloween colors
			 * and red as the color of blood.
			 */
			background = Color.ORANGE;
			borderLines = Color.BLACK;
			openSquares = Color.RED;
			break;
		
		case 1:
			/*
			 * #GoTerps color scheme, with green and red for the colors of Testudo 
			 * and a standard black border.
			 */
			background = Color.GREEN;
			borderLines = Color.BLACK;
			openSquares = Color.RED;
			break;
		
		case 2:
			/*
			 * Inverted color scheme where the colors are flipped (I know that pink
			 * isn't the reverse of green but it just looks better with pink).
			 */
			background = Color.BLACK;
			borderLines = Color.WHITE;
			openSquares = Color.PINK;
			break;
			
		case 3:
			/*
			 * Standard color scheme.
			 */
			background = Color.WHITE;
			borderLines = Color.BLACK;
			openSquares = Color.GREEN;
		
		}
		
	}
	
	
	/*
	 * If the user has a question or doesn't understand something, this method
	 * redirects them to a website that hopefully answer their question.
	 */
	private void displayInstructionPrompt () {
		
		String[] options = {"Answer a Question", "Show Game Rules"};
		
		int choice = JOptionPane.showOptionDialog(null, "How can I help you?", TITLE, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[1]);
		
		/*
		 * We access the desktop of the user so that we can go to the internet browser.
		 */
		Desktop d = Desktop.getDesktop();
		
		try {

			if(choice == 0) {

				/*
				 * If the user selected "Answer a Question" in the JOptionPane,
				 * we open the borwser and go to Google, where the user should
				 * be able to get their question answered (if Google can't answer 
				 * it then no one can).
				 */
				d.browse(new URI("https://www.google.com"));

			} else if (choice == 1) {

				/*
				 * If the user selected "Show Game Rules", the browser is directed
				 * to the wikipedia page with the rules of the game, which is where
				 * I found them.
				 */
				d.browse(new URI("https://en.wikipedia.org/wiki/Ultimate_tic-tac-toe#Rules"));

			}

		} catch (Exception ex){

			/*
			 * If some exception is thrown due to the links malfunctioning or
			 * the browser being unable to connect to the internet, we simply
			 * display a JOptionPane instructing the user to google them.
			 */
			JOptionPane.showMessageDialog(null, "Google them!", "Rules", JOptionPane.PLAIN_MESSAGE);

		}

	}
	
	
	
	/**
	 * Main method to start the game.
	 */
	public static void main(String[] args) {
		
		/*
		 * We use the invokeLater() method to ensure that the our GUI is created 
		 * in the event-dispatching thread to prevent concurrent access to the data
		 * once we create our event listeners.
		 */
		javax.swing.SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				
				 new UltimateTicTacToe();
				
			}
			
		});
		
	}

}
