package gameCode;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;


/**
 * The View is the JPanel that contains the visuals of the game that players 
 * will see and interact with. This class also defines the pixel coordinate locations
 * within the GUI that separate different squares in the game board to simplify the
 * response to mouse events. Only one instance of the View class will be created
 * in this game.
 * 
 * @author Sahir Mody
 */
public class View extends JPanel {
	
	/*
	 * UID required by serialized classes.
	 */
	private static final long serialVersionUID = 1L;
	
	/*
	 * Sets the dimensions of the sub squares to 80 pixels.
	 */
	private final static int SUB_SQUARE_SIZE = 80;
			
	/*
	 * Defines the border between sub squares as a tenth of the width of the sub
	 * square itself.
	 */
	private final static int SUB_BORDER_WIDTH = SUB_SQUARE_SIZE/10;
	
	/*
	 * Defines width of the border between super squares as three times the width 
	 * of the border between sub squares.
	 */
	private final static int SUPER_BORDER_WIDTH = 3 * SUB_BORDER_WIDTH;
	
	/*
	 * Defines the size of a sub layer (which includes the sub square itself and the
	 * border).
	 */
	private final static int SUB_LAYER_WIDTH = SUB_SQUARE_SIZE + SUB_BORDER_WIDTH;
	
	
	/*
	 * Defines the size of a super layer, which includes the 3 sub squares, 2 sub square
	 * borders and the super square border.
	 */
	private final static int SUPER_LAYER_WIDTH = 3 * SUB_SQUARE_SIZE + 2 * SUB_BORDER_WIDTH + SUPER_BORDER_WIDTH;

	
	/*
	 * Defines the size of a margin within a super square as a tenth of the sub square size
	 * (only to be used when drawing markers).
	 */
	private final static int MARGIN = SUB_SQUARE_SIZE/10;
	
	/*
	 * Defines the dimensions of a super square.
	 */
	private final static int SUPER_SQUARE_SIZE = SUPER_LAYER_WIDTH - 2 * MARGIN - SUPER_BORDER_WIDTH;
	
	
	/**
	 * The overall width required for the game board (to be used when setting the
	 * size of the JFrame).
	 */
	public final static int WIDTH = SUB_SQUARE_SIZE * 9 + SUB_BORDER_WIDTH * 6 + SUPER_BORDER_WIDTH * 2;
	
	/**
	 * The overall height required for the game board and the frame (to be used when
	 * setting the size of the JFrame).
	 */
	public final static int HEIGHT = WIDTH + 50;
	
	
	/*
	 * Model that the view will be painted based on.
	 */
	private Model model;

	
	/*
	 * Represents the color that will be used to paint the background of the board.
	 */
	private Color backgroundColor;
	
	
	/*
	 * Represents the color that will be used to paint the borders between squares
	 * and the markers.
	 */
	private Color lineColor;
	
	
	/*
	 * Represents the color that will paint the squares that the current player can 
	 * make valid moves in.
	 */
	private Color availableSpacesColor;
	
	
	/**
	 * Basic constructor that instantiates a new model.
	 * 
	 * @param background color to be used to paint the background
	 * @param linecolor used to paint markers and border lines
	 * @param availableSpace color used to paint squares where playeers can make valid moves
	 */
	public View (Color background, Color line, Color availableSpace) {
		
		
		model = new Model(this);
		
		backgroundColor = background;
		
		lineColor = line;
		
		availableSpacesColor = availableSpace;
		
		
	}
	
	
	/**
	 * Basic getter for the model.
	 * 
	 * @returns a deep copy of the model.
	 */
	public Model getModel() {
		return new Model(model);
	}
	
	
	/**
	 * Sets the colors to the colors specified as arguments.
	 * 
	 * @param background the new background color
	 * @param line the new color of the lines and markers
	 * @param availableSpace the new color of the playable spaces
	 */
	public void setColors(Color background, Color line, Color availableSpace) {
		backgroundColor = background;
		lineColor = line;
		availableSpacesColor = availableSpace;
	}
	
	
	
	/*
	 * Returns the layer (either row or column) that a given pixel is part of.
	 * 
	 * @param position the pixel coordinate (x if we are trying to find the column
	 * and y if we are trying to find the row)
	 * @param isSuperSquare true if we are trying to find a super square layer and
	 * false if we are trying to find a sub square layer
	 * @return integer value of the layer, either 0, 1, 2 or ERROR (-1)
	 */
	private int getLayer (int position, boolean isSuperSquare) {
		
		/*
		 * We establish the dimensions of the border and layer in order to calculate
		 * which layer the position is a part of.
		 */
		int layerWidth = isSuperSquare ? SUPER_LAYER_WIDTH : SUB_LAYER_WIDTH;
		int borderWidth = isSuperSquare ? SUPER_BORDER_WIDTH : SUB_BORDER_WIDTH;
		
		if(position <= layerWidth - borderWidth) {
			
			return 0;
			
		} else if (position <= 2 * layerWidth - borderWidth && 
				   position >= layerWidth) {
			
			return 1;
			
		} else if (position >= 2 * layerWidth) {
			
			return 2;
			
		} else {
			
			return Model.ERROR;
			
		}
		
	}
	
	
	
	/**
	 * Finds the super square that encompasses the pixel location specified by the
	 * parameters x and y.
	 * 
	 * @param x the x-coordinate of the specified pixel location
	 * @param y the y-coordinate of the specified pixel location
	 * @return the number corresponding to the super square that the pixel location is in
	 */
	public int getSuperSquare(int x, int y) {
		
		int column = getLayer(x, true);
		int row = getLayer(y, true);
		
		return column + 3 * row;
	}
	
	
	
	/**
	 * Finds the sub square that encompasses the pixel location specified by the 
	 * the parameters in the super square specified by the parameter.
	 * 
	 * @param x the x-coordinate of the pixel we are looking at
	 * @param y the y-coordinate of the pixel we are looking at
	 * @param superSquare the current super square 
	 * @return the integer value of the sub square the pixel is located in
	 */
	public int getSubSquare(int x, int y, int superSquare) {
		
		int superColumn = superSquare % 3;
		int superRow = superSquare / 3;
		
		int subColumn = getLayer(x - superColumn * SUPER_LAYER_WIDTH, false);
		int subRow = getLayer(y - superRow * SUPER_LAYER_WIDTH, false);
		
		return subColumn + 3 * subRow;
		
	}
	
	
	/*
	 * Calculates the x-coordinate for the pixel at the top left corner of the specified
	 * sub square of the specified super square.
	 * 
	 * @param superSquare the super square of the specific sub square we want to look at
	 * @param subSquare the sub square whose top left x-coordinate we want to find
	 * @return the pixel value of the x-coordinate
	 */
	private int xCoordinate(int superSquare, int subSquare) {
		
		int superColumnNumber = superSquare % 3;
		int subColumnNumber = subSquare % 3;
		
		return superColumnNumber * SUPER_LAYER_WIDTH + subColumnNumber * SUB_LAYER_WIDTH;
		
	}
	
	
	/*
	 * Calculates the y-coordinate for the pixel at the top left corner of the specified
	 * sub square of the specified super square.
	 * 
	 * @param superSquare the super square of the specific sub square we want to look at
	 * @param subSquare the sub square whose top left y-coordinate we want to find
	 * @return the pixel value of the y-coordinate
	 */
	private int yCoordinate (int superSquare, int subSquare) {
		
		int superRowNumber = superSquare / 3;
		int subRowNumber = subSquare / 3;
		
		return superRowNumber * SUPER_LAYER_WIDTH + subRowNumber * SUB_LAYER_WIDTH;
		
	}
	
	
	/*
	 * Draws an X or O on the specified sub square or super square.
	 * 
	 * @param g Graphics object supplied by the paint method
	 * @param superSquare specifies the super square we have to draw the marker in
	 * @param subSquare specifies the sub square we have to draw the marker in
	 * @param ex true if an X is to be drawn and false if an O is to be drawn
	 * @param big true if we have to draw the marker over an entire super square and
	 * false otherwise
	 */
	private void drawMarker (Graphics g, int superSquare, int subSquare, boolean ex, boolean big) {
		
		/*
		 * Specifies some details about the marker before we draw it.
		 */
		int markerMargin = big ? 2 * MARGIN : MARGIN;
		int size = big ? SUPER_SQUARE_SIZE : SUB_SQUARE_SIZE;
		
		if(big) {
			
			/*
			 * This is necessary so that the marker begins drawing at the top left
			 * of the specified super square.
			 */
			subSquare = 0;
			
		}
		
		/*
		 * Here we get the pixel coordinate at the top left of the square in which we are 
		 * drawing the marker to ensure that the marker is drawn in the exact location
		 * we want it.
		 */
		int x1 = xCoordinate(superSquare, subSquare) + markerMargin;
		int y1 = yCoordinate(superSquare, subSquare) + markerMargin;
		
		
		if(ex) {
			
			int x2 = x1 + size - 2 * MARGIN;
			int y2 = y1 + size - 2 * MARGIN;
			
			g.drawLine(x1, y1, x2, y2);
			g.drawLine(x2, y1, x1, y2);
			
		} else {
			
			g.drawOval(x1, y1, size - 2 * MARGIN, size - 2 * MARGIN);
			
		}
		
	}
	
	
	
	/**
	 * Method called when the GUI needs to be updated because of a change in the
	 * state of the game.
	 * 
	 * @param model with the new state of the game.
	 */
	public void update(Model model) {
		
		/*
		 * Updates the model of the panel to refer to the new model supplied in
		 * the argument.
		 */
		this.model = model;
		
		/*
		 * Asks the panel to paint itself again.
		 */
		this.repaint();
		
	}
	
	
	
	/**
	 * Overrides the paint method of the JPanel class and paints the panel
	 * based on the new state of the model.
	 */
	@Override
	public void paint(Graphics g) {
		
		/*
		 * We use a Graphics2D object instead of a standard Graphics object because
		 * Graphics2D allows us to set different stroke sizes so that the GUI
		 * looks exactly the way it needs to.
		 */
		Graphics2D g2 = (Graphics2D) g;
		
		g2.setColor(lineColor);
		
		g2.fillRect(0, 0, WIDTH, HEIGHT);
		
		
		/*
		 * We create a local copy of the board so that we can paint it.
		 */
		int[][] board = model.getBoard();

		
		
		/*
		 * With these nested for loops, we can iterate through each element of the
		 * board and draw what is contained in each square, as well as define the borders
		 * for each square.
		 */
		for(int i = 0; i < 9; i++) {
			
			for(int j = 0; j < 9; j++) {
				
				
				g2.setStroke(new BasicStroke(SUB_SQUARE_SIZE / 9));
				
				/*
				 * We color each square as the background color if it it not a valid
				 * square in which the player can make a move or the available spaces 
				 * color if it is a place where a player can make a valid move.
				 */
				if((i == model.getCurrentSuperSquare() ||
					model.getCurrentSuperSquare() == Model.ERROR)  &&
				   model.isPlayable(i,j) &&
				   model.winnerOfSquare(model.getSuperSquareWinners()) == Model.EMPTY) {
					
					g2.setColor(availableSpacesColor);
					
				} else {
					
					g2.setColor(backgroundColor);
					
				}
				
				g2.fillRect(xCoordinate(i,j), yCoordinate(i,j), SUB_SQUARE_SIZE, SUB_SQUARE_SIZE);
				
				
				

				/*
				 * Now we paint the markers for each sub square in the following section.
				 */
				
				g2.setColor(lineColor);
				
				if (board[i][j] == Model.EX) {
					
					drawMarker(g2,i,j,true, false);
					
				} else if (board[i][j] == Model.OH) {
					
					drawMarker(g2,i,j, false, false);
					
				}
				
				
				
				/*
				 * Finally, we paint the markers for super squares that have been won.
				 * We first create a bigger stroke and  then draw the markers accordingly.
				 */
				
				g2.setStroke(new BasicStroke(SUB_SQUARE_SIZE / 4));
				
				if(model.getSuperSquareWinners()[i] == Model.EX) {
					
					drawMarker(g2, i, 0, true, true);
					
				} else if (model.getSuperSquareWinners()[i] == Model.OH) {
					
					drawMarker(g2, i, 0, false, true);
					
				}
				
			}
			
		}
		
	}
	
}
