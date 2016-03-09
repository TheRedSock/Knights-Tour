import java.awt.Point;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

	// Variables for validation.
	private final static int MIN_COL = 0;
	private final static int MIN_ROW = 0;
	private final static int MAX_SIZE = 64;
	private final static int MIN_SIZE = 3;
	private final static int BORDER = 2;
	
	// Other global variables.
	private static int row, col, size, realSize, board[][];
	private static long startTime, endTime;
	private static double runTime;
	private static boolean validate = false;
	private static ArrayList<Point> pointList;
	private static Point currentPoint;
	private static String newLine = System.getProperty("line.separator"); 
	
	// Point array which declares the eight moves that a knight can make.
	private static final Point[] MOVES = new Point[] {
			
		new Point(2, 1),   // down-right
		new Point(1, 2),   // right-down
		new Point(-1, 2),  // left-down
		new Point(-2, 1),  // up-right
		new Point(-2, -1), // up-left
		new Point(-1, -2), // left-up
		new Point(1, -2),  // right-up
		new Point(2, -1),  // down-left
	};
	
	
	// Main function, the program starts here.
	public static void main(String[] args) {
		
		System.out.println("You can change the parameter boundaries in the validation variables in the code.");
		
		//Run functions to check user input.
		Scanner input = new Scanner(System.in);
		inputSize(input);
		inputRow(input);       
		inputCol(input);
		input.close();
		
		System.out.println(newLine + "---" + newLine);
		
		// Start timer and run algorithm.
		startTime = System.nanoTime();
		
		// Instantiate the move list and mark the first position.
		currentPoint = new Point(col - BORDER, row - BORDER);
		pointList = new ArrayList<Point>();
		
		// Run the program.
		runSolution(col, row);
	}
	
	public static void runSolution(int startRow, int startCol) {
		
		//Create a decimal format for a cleaner view of double variables.
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.HALF_EVEN);
		
		// Initialise the board and set the starting position.
		initialiseBoard();
		board[startRow][startCol] = 1;
		
		// Run the solution. If true is returned, it has been solved.
		if (solution(row, col, 2)) {
			
			// Stop timer and print results.
			endTime = System.nanoTime();
			runTime = (((double) endTime - (double) startTime) / 1000000);
			pointList.add(currentPoint);
			
			printOutput();
			System.out.println(newLine + "Success! The algorithm took " + df.format(runTime) + " milliseconds.");
		}
		else {
			System.out.println("There is no solution with these parameters.");
		}
	}
	
	private static boolean solution(int startRow, int startCol, int currentMove) {
		
		// Ends the simulation if maximum number of moves have been achieved.
		if (currentMove > (size * size)) {
			return true;
		}
		
		// Cycles through the list containing moves sorted by Warnsdorf's rule.
		for (Point p : sortedMoves(startRow, startCol)) {
			
			// Adds the current move to the board.
			board[p.x][p.y] = currentMove;
			
			// Run the function recursively until it returns true, which means a solution is found.
			if (solution(p.x, p.y, currentMove + 1)) {
				
				/*
				* If this point has been reached a solution was found, so the moves are added to a list.
				* This list will be in reverse order because the last stack is the last move.
				*/
				
				pointList.add(new Point(p.x - BORDER, p.y - BORDER));
				return true;
				
			} 
			
			// If there is a dead end, go back.
			else {
				board[p.x][p.y] = -1;
				
			}
		}
			
		/*
		 * The way this algorithm works is in the nature of how recursive functions work.
		 * Every time the 'solution' function is ran, it expects a return call.
		 * But since instead of giving a return call, the function starts over, expecting a new return call.
		 * In this way, when there is a dead end, instead of returning false on the initial call,
		 * it returns false to the previous call. 
		 * Therefore you backtrack all the way until the initial call returns false.
		 */
		
		return false;
	}
	
	// Function which returns a list of legal moves from the parameter coordinates.
	private static ArrayList<Point> amountOfMoves(int row, int col) {
		
		ArrayList<Point> listOfMoves = new ArrayList<Point>();
		
		// Cycles through possible moves.
		for (Point p : MOVES) {
			
			int nextRow = row + p.x;
			int nextCol = col + p.y;
			
			if (safeMove(nextRow, nextCol)) {
				listOfMoves.add(new Point(nextRow, nextCol));
			}			
		}
		
		return listOfMoves;
	}

	// Returns a list of moves sorted by the Warnsdorf rule.
	private static ArrayList<Point> sortedMoves(int row, int col) {
		
		HashMap<Integer, Point> map = new HashMap<Integer, Point>(64);
		
		// Gets the amount of legal moves from the current position.
		ArrayList<Point> moveList = amountOfMoves(row, col);
		
		// Checks every legal move in this list.
		for (Point p : moveList){
			
			// Creates a hashmap where the key is the amount of legal moves, and the value is the move itself.
			int i = amountOfMoves(p.x, p.y).size();			
			map.put(i, p);
		}
		
		// Returns a list sorted by the key of the hashmap.		    
		return sortHashMap(map);
	}
	
	// Function that sorts a hashmap by an integer key.
	@SuppressWarnings("rawtypes")
	private static ArrayList<Point> sortHashMap(HashMap<Integer, Point> map) {
		
		ArrayList<Point> list = new ArrayList<Point>();
		
		Map<Integer, Point> sortedMap = new TreeMap<Integer, Point>(map);
		Set set = sortedMap.entrySet();
		Iterator iterator = set.iterator();
		
		while(iterator.hasNext()) {
			
			Map.Entry me = (Map.Entry) iterator.next();
			list.add((Point) me.getValue());
		}
		
		return list;		
	}
	
	// Checks if the parameter cell is a valid location to land.
	private static boolean safeMove(int row, int col) {
		
		// Since we have the border, we won't get an index out of bound exception.
		return (board[row][col] == -1);
	}
	
	// Prints out the two-dimensional array.
	private static void printOutput() {
		
		// Loops through the array, but ignores the border.
		for (int i = 0 + BORDER; i < (realSize - BORDER); i++) {
			for (int j = 0 + BORDER; j < realSize - BORDER; j++) {
				
				System.out.printf("%-8d", board[i][j]);
			}
			
			System.out.printf("%n");
		}
		
		System.out.println(newLine + "---" + newLine);
		
		// Prints out the move list in an easy to read fashion.
		for (int i = pointList.size() - 1; i > 0; i--) {
			
			System.out.println("(" + pointList.get(i).x + 
					   ", " + pointList.get(i).y + 
					   ") to (" + pointList.get(i - 1).x + 
					   ", " + pointList.get(i - 1).y + ")");
		}
	}
	
	// Initialise the board object to populate it with dummy numbers.
	private static void initialiseBoard() {
		
		//In order to prevent out of bound exceptions, we create a border.
		realSize = size + BORDER + BORDER;
		
		board = new int[realSize][realSize];
		
		for (int i = 0; i < realSize; i++) {
			for (int j = 0; j < realSize; j++) {
				
				// Populate border cells with 0, and movable cells with -1.
				if (i < BORDER || j < BORDER) {
					board[i][j] = 0;
				}
				else if (i >= (realSize - BORDER) || j >= realSize - BORDER) {
					board[i][j] = 0;
				}
				else {
					board[i][j] = -1;
				}
			}
		}
	}

	// Function for reading user input.
	private static void inputRow(Scanner scanner) {
		
		System.out.printf("Type in starting row (from " + MIN_ROW + " to " + (size - 1) + "): ");
		
		// While loop for validation.
		do {
			
			// Tries user input, catches exception if input is not integer.
			try {
				
				row = scanner.nextInt();
				
				// Checks if user input is within accepted boundary.
				if (row <= (size - 1) && row >= MIN_ROW) {
					validate = true;
				} 
				else {
					
					validate = false;
					System.out.println("The integer is not within the boundaries, try again:");
					scanner.nextLine();
				} 
			} catch (InputMismatchException e) {
				
				validate = false;
				System.out.println("You did not input an integer, try again:");
				scanner.nextLine();
			}
		} while (!validate);
		
		// For user-friendliness we add the border in the code so the board index is intuitive.
		row += BORDER;
	}
	
	// Same structure as inputRow function.
	private static void inputCol(Scanner scanner) {
		
		System.out.printf("Type in starting column (from " + MIN_COL + " to " + (size - 1) + "): ");
		
		do {
			try {
				
				col = scanner.nextInt();
				
				if (col <= (size - 1) && col >= MIN_COL) {
					validate = true;
				} 
				else {
					
					validate = false;
					System.out.println("The integer is not within the boundaries, try again:");
					scanner.nextLine();
				} 
			} catch (InputMismatchException e) {
				
				validate = false;
				System.out.println("You did not input an integer, try again:");
				scanner.nextLine();
			}
		} while (!validate);
		
		col += BORDER;
	}

	//Same structure as inputRow function.
	private static void inputSize(Scanner scanner) {
		
		System.out.printf("Type in the size of the board (from " + MIN_SIZE + " to " + MAX_SIZE + "): ");

		do {
			try {
				
				size = scanner.nextInt();
				
				if (size <= MAX_SIZE && size >= MIN_SIZE) {
					validate = true;
				} 
				else {
					
					validate = false;
					System.out.println("The integer is not within the boundaries, try again:");
					scanner.nextLine();
				} 
			} catch (InputMismatchException e) {
				
				validate = false;
				System.out.println("You did not input an integer, try again:");
				scanner.nextLine();
			}
		} while (!validate);
	}
}
