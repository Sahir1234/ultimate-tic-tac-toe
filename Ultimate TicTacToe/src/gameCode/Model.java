package gameCode;

import java.util.ArrayList;


/**
 * The model provides the internal mechanics for how the game will run. It sets up
 * a board for Ultimate Tic-Tac-Toe, which is essentially a regular tic-tac-toe
 * board with a sub-board inside each square. See wikipedia for clarification. 
 * In this class, the squares of the larger board are referred to as superSquares
 * while the squares of the sub-boards are referred to as subSquares. The squares are numbered
 * from left to right and top to bottom, so that the square at the top left of 
 * a given board is 0 and the square at the bottom right is 8. This makes computation
 * easier because dividing by 3 separates the rows and taking the integer modulo 3
 * separates the columns. Also note that X's and O's will referred to as markers.
 * 
 * @author Sahir Mody
 */
public class Model {
	
	/**
	 * Constant value to represent a specific square on the board to being marked
	 * with an X.
	 */
	public final static int EX = 10;
	
	/**
	 * Constant value to represent a specific square on the board to being marked
	 * with an O.
	 */
	public final static int OH = -10;
	
	/**
	 * Constant value to represent a specific square 
	 */
	public final static int EMPTY = 0;
	
	/**
	 * Error value used when something unexpected happens.
	 */
	public final static int ERROR = -1;
	
	/*
	 * Stores the value of super square in which the 
	 */
	private int currentSuperSquare;
	
	/*
	 * Array that models the game board.
	 */
	private int[][] board;
	
	/*
	 * Array that stores the winners of each super square in the game board.
	 */
	private int[] superSquareWinners;
	
	/*
	 * Keeps track of which player's turn it is. True if it is X's turn and false
	 * if it is O's turn.
	 */
	private boolean exTurn;
	
	/*
	 * This ArrayList stores the history of moves made in the current game. For
	 * each value stored in the list, the tens place represents the supersquare in
	 * which the move was made and the ones place represents the subsquare in 
	 * which that move was made.
	 */
	private ArrayList<Integer> moveHistory;
	
	/*
	 * View object that the model is attached to which is necessary so that we can
	 * call the update method of the View class every time the model has changed.
	 */
	private View view;
	
	
	
	/**
	 * Basic constructor that sets the current super square to the central one
	 * and sets it to X's turn.
	 * 
	 * @param view that the model will be attached to.
	 */
	public Model (View view) {
		
		currentSuperSquare = 4;
		
		board = new int[9][9];
		
		superSquareWinners = new int[9];
		
		exTurn = true;
		
		this.view = view;
		
		moveHistory = new ArrayList<Integer>();
		
	}
	
	
	/**
	 * Copy constructor that creates a deep copy of the arrays and ArrayList, and
	 * a reference copy for the view because there is only one View object in the
	 * entire project.
	 * 
	 * @param other model to be copied.
	 */
	public Model (Model other) {
		
		this.currentSuperSquare = other.currentSuperSquare;
		
		this.board = other.getBoard();
		
		superSquareWinners = other.getSuperSquareWinners();
		
		this.exTurn = other.exTurn;
		
		this.view = other.view;
		
		moveHistory = new ArrayList<Integer>();
		
		for(Integer x : other.moveHistory) {
			
			moveHistory.add(x);
			
		}
		
	}
	
	
	/**
	 * Getter for the current supersquare. Will return the value of ERROR if it
	 * is the start of a new game or the super square cannot be played in (this 
	 * only happens if it is full or the super square has already been won).
	 * 
	 * @return current supersquare or ERROR (-1)
	 */
	public int getCurrentSuperSquare () {
		
		if(isSuperSquareFilled(currentSuperSquare) ||
		   isBoardEmpty() ||
		   superSquareWinners[currentSuperSquare] != EMPTY) {
			
			return ERROR;
			
		}
		
		return currentSuperSquare;
		
	}
	
	
	/**
	 * Getter for the board.
	 * 
	 * @return a deep copy of the board array.
	 */
	public int[][] getBoard() {
		
		int[][] copy = new int[9][9];
		
		for(int i = 0; i < 9; i++) {
			
			for(int j = 0; j < 9; j++) {
				
				copy[i][j] = board[i][j];
				
			}
			
		}
				
		return copy;
		
	}
	
	
	/**
	 * Getter for the superSquareWinners array.
	 * 
	 * @return a deep copy of the superSquareWinners array.
	 */
	public int[] getSuperSquareWinners() {
		
		int[] copy = new int[9];
		
		for(int i = 0; i < 9; i++) {
			
			copy[i] = superSquareWinners[i];
			
		}
		
		return copy;
		
	}
	
	
	/**
	 * Getter for exTurn.
	 * 
	 * @return boolean value of exTurn.
	 */
	public boolean getExTurn () {
		return exTurn;
	}
	
	
	
	/*
	 * Returns whether or not the entire board array is empty.
	 * 
	 * @return true if the board is empty and false otherwise.
	 */
	private boolean isBoardEmpty() {
		
		for(int i = 0; i < 9; i++) {
			
			for(int j = 0; j < 9; j++) {
				
				if(board[i][j] != EMPTY) {
					
					return false;
					
				}
				
			}
			
		}
		
		return true;
		
	}
	
	
	/*
	 * Returns whether or not the specified superSquare is filled or still has 
	 * empty spaces.
	 * 
	 * @param the superSquare we want to analyze
	 * @return true if the specified superSquare is completely non-empty, and false
	 * otherwise
	 */
	private boolean isSuperSquareFilled(int superSquare) {
		
		for(int space : board[superSquare]) {
			
			if(space == EMPTY) {
				
				return false;
				
			}
			
		}
		
		return true;
		
	}
	
	
	
	/**
	 * Returns whether or not the specified square is empty (playable) or not.
	 * 
	 * @param superSquare coordinate of the square we are looking at.
	 * @param subSquare coordinate of the square we are analyzing.
	 * @return true if the specified subSquare of the specified superSquare is empty
	 * and false otherwise.
	 */
	public boolean isPlayable(int superSquare, int subSquare) {
		
		if(superSquare <= ERROR ||
		   subSquare <= ERROR) {
			
			return false;
			
		}
		
		return board[superSquare][subSquare] == EMPTY && superSquareWinners[superSquare] == EMPTY;
		
	}
	
	
	
	/**
	 * Returns whether or not moves can still be made.
	 * 
	 * @return true if there are still spaces that valid moves can be made and
	 * false otherwise.
	 */
	public boolean movesAvailable () {
		
		for(int i = 0; i < 9; i++) {
			
			for(int j = 0; j < 9; j++) {
				
				if(isPlayable(i,j)) {
					
					return true;
					
				}
				
			}
			
		}
		
		return false;
		
	}
	
	
	
	/**
	 * Undoes the previous move made in the current game.
	 */
	public void undoLastMove() {
		
		/*
		 * If the game has just started and there are no moves to undo, the method
		 * does nothing.
		 */
		if(moveHistory.size() <= 0) {
			
			return;
			
		}
		
		/*
		 * Retrieve the previous move and then reset the settings of the model
		 * based on the removal of this move.
		 */
		int previousMove = moveHistory.get(moveHistory.size() - 1);
		
		board[previousMove / 10][previousMove % 10] = EMPTY;
		
		currentSuperSquare = previousMove/10;
		
		
		/*
		 * Reset the superSquareWinners by recalculating the winners after the 
		 * previous move has been removed.
		 */
		for(int i = 0; i < 9; i++) {
			
			superSquareWinners[i] = winnerOfSquare(board[i]);
			
		}
		
		moveHistory.remove(moveHistory.size() - 1);
		
		exTurn = !exTurn;
		
	}
	
	/**
	 * Places an X or an O on a specific place in the board specified by the arguments
	 * 
	 * @param superSquare in which we are placing the marker
	 * @param subSquare of superSquare in which we are placing the marker
	 */
	public void placeMarker(int superSquare, int subSquare) {
		
		/*
		 * We only place the marker if another marker has not been placed at the
		 * specified square.
		 */
		if(board[superSquare][subSquare] == EMPTY) {
			
			moveHistory.add((superSquare * 10) + subSquare);
			
			/*
			 * Based on the rules of UTTT, the new superSquare that the player can play
			 * in must correspond to the last subSquare that was played in.
			 */
			currentSuperSquare = subSquare;
			
			/*
			 * Place the correct marker based on who's turn it is.
			 */
			board[superSquare][subSquare] = exTurn ? EX : OH;
			
			/*
			 * Change which player's turn it is.
			 */
			exTurn = !exTurn;
			
			/*
			 * Update the superSquareWinners if someone has won the current super square.
			 */
			superSquareWinners[superSquare] = winnerOfSquare(board[superSquare]);
			
			/*
			 * Updates the GUI with the new state of the board after the marker
			 * has been placed.
			 */
			view.update(this);
			
		}
		
	}
	
	
	/**
	 * Finds if there is a winner in the array passed in as the argument and returns
	 * the corresponding integer value of the winner, or the value of empty if 
	 * there is no winner. A winner is defined by a three-in-a-row horizontally,
	 * vertically, or diagonally.
	 * 
	 * @param board the array we want to analyze
	 * @throws IllegalArgumentException if the size of the array argument is not 9
	 * (i.e. it cannot represent a valid tic-tac-toe board)
	 * @return the value of the winner or the constant EMPTY if there is no winner
	 */
	public int winnerOfSquare(int[] board) {
		
		/*
		 * Since this method is only designed to look at an array of size 9
		 * representing a tic-tac-toe board, an array of any other size is invalid
		 * so an exception is thrown.
		 */
		if(board.length != 9) {
			
			throw new IllegalArgumentException("Invalid Board Size!");
			
		}
		
		
		/*
		 * This loop iterates three times so  that we can check rows and columns
		 * for three-in-a-rows simultaneously.
		 */
		for(int i = 0; i < 3; i++) {
			
			/*
			 * Checks each column in the ith row for a three-in-a-row.
			 */
			if(board[0 + 3*i] == board[1 + 3*i] && 
			   board[1 + 3*i] == board[2 + 3*i] &&
			   board[3 * i] != EMPTY) {
				
				return board[3 * i];
				
			} 
			
			/*
			 * Checks each row in the ith column for a three-in-a-row.
			 */
			if(board[0 + i] == board[3 + i] && 
			   board[3 + i] == board[6 + i] &&
			   board[i] != EMPTY) {
				
				return board[i];
				
			}
			
		}
		
		
		/*
		 * Checks the diagonals in the board for a three-in-a-row.
		 */
		if((board[0] == board[4] && board[4] == board[8] ||
			board[2] == board[4] && board[4] == board[6]) &&
			board[4] != EMPTY) {
			
			return board[4];
		
		}
		
		return EMPTY;
		
	}

}
