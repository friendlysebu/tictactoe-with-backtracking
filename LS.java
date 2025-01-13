//package tictactoe;

import java.awt.*;
import javax.swing.*;

// Class to represent Move objects
class Move {
    int val;   // Value of the move
    int row;   // Row and column coordinates
    int col;

    public Move(int v, int r, int c) {
        val=v;
        row=r;
        col=c;
    }
}

// Class Button extends JButton with (x,y) coordinates
class Button extends javax.swing.JButton {
    public int i;   // The row and column coordinate of the button in a GridLayout
    public int j;

    public Button (int x, int y) {
        // Create a JButton with a blank icon. This also gives the button its correct size.
        super();
        super.setIcon(new javax.swing.ImageIcon(getClass().getResource("None.png")));
        this.i = x;
        this.j = y;
    }

    // Return row coordinate
    public int get_i () {
        return i;
    }

    // Return column coordinate
    public int get_j () {
        return j;
    }

}

public class LS extends javax.swing.JFrame {

    // Marks on the board
    public static final int EMPTY    = 0;
    public static final int HUMAN    = 1;
    public static final int COMPUTER = 2;

    // Outcomes of the game
    public static final int HUMAN_WIN    = 4;
    public static final int DRAW         = 5;
    public static final int CONTINUE     = 6;
    public static final int COMPUTER_WIN = 7;

    public static final int SIZE = 3;
    private int[][] board = new int[SIZE][SIZE];  // The marks on the board
    private javax.swing.JButton[][] jB;           // The buttons of the board
    private int turn = HUMAN;                    // HUMAN starts the game

    /* Constructor for the Tic Tac Toe game */
    public LS() {
        // Close the window when the user exits
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        initBoard();      // Set up the board with all marks empty
    }

    // Initalize an empty board.
    private void initBoard(){
        // Create a SIZE*SIZE gridlayput to hold the buttons
        java.awt.GridLayout layout = new GridLayout(SIZE, SIZE);
        getContentPane().setLayout(layout);

        // The board is a grid of buttons
        jB = new Button[SIZE][SIZE];
        for (int i=0; i<SIZE; i++) {
            for (int j=0; j<SIZE; j++) {
                // Create a new button and add an actionListerner to it
                jB[i][j] = new Button(i,j);
                // Add an action listener to the button to handle mouse clicks
                jB[i][j].addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent act) {
                        jBAction(act);
                    }
                });
                add(jB[i][j]);   // Add the buttons to the GridLayout

                board[i][j] = EMPTY;     // Initialize all marks on the board to empty
            }
        }
        // Pack the GridLayout and make it visible
        pack();
    }

    // Action listener which handles mouse clicks on the buttons
    private void jBAction(java.awt.event.ActionEvent act) {
        Button thisButton = (Button) act.getSource(); // Get the button clicked on
        int i = thisButton.get_i();
        int j = thisButton.get_j();

        // Check if the clicked cell is already occupied
        if (board[i][j] != EMPTY) {
            return; // Ignore the click since the cell is not empty
        }

        System.out.println("Button[" + i + "][" + j + "] was clicked by " + turn); // DEBUG

        if (turn == HUMAN) {
            thisButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("X.png")));
            place(i, j, HUMAN); // Mark the human move on the board
            if (checkResult() == HUMAN_WIN) {
                endGame(HUMAN_WIN);
                return;
            }
        }

        // Check for a draw after human's move
        if (!isMovesLeft()) {
            endGame(DRAW);
            return;
        }

        // Computer's turn
        turn = COMPUTER;
        computerMove(); // Make the computer's move
        if (checkResult() == COMPUTER_WIN) {
            endGame(COMPUTER_WIN);
            return;
        }

        // Check for a draw after computer's move
        if (!isMovesLeft()) {
            endGame(DRAW);
            return;
        }

        // Switch turn back to human
        turn = HUMAN;
    }

    private Move findBestMove() {
        int bestVal = -1000; // Initialize the best value to a very low number
        Move bestMove = new Move(-1, -1, -1); // Initialize the best move, initially set to an invalid position

        // Iterate over all cells of the board
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                // Check if the current cell is empty
                if (board[i][j] == EMPTY) {
                    board[i][j] = COMPUTER; // Make a temporary move for the computer

                    // Call the minimax function to calculate the value of this move
                    int moveVal = minimax(0, false);

                    board[i][j] = EMPTY; // Undo the temporary move

                    // If the value of the current move is greater than the best value
                    if (moveVal > bestVal) {
                        bestMove.row = i; // Update the best move's row
                        bestMove.col = j; // Update the best move's column
                        bestVal = moveVal; // Update the best value
                    }
                }
            }
        }
        return bestMove; // Return the best move found
    }

    private int minimax(int depth, boolean isMax) {
        int score = evaluate(); // Evaluate the current board state

        // If the computer is winning return the evaluated score minus depth
        if (score == 10) return score - depth;

        // If the human is winning return the evaluated score plus depth
        if (score == -10) return score + depth;

        // If there are no more moves left and no winner, return 0 (draw)
        if (!isMovesLeft()) return 0;

        // Maximizing player (computer)
        if (isMax) {
            int best = -1000; // Initialize best to the lowest possible value

            // Traverse all cells
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    // Check if the cell is empty
                    if (board[i][j] == EMPTY) {
                        // Make the move
                        board[i][j] = COMPUTER;

                        // Call minimax recursively and choose the maximum value
                        best = Math.max(best, minimax(depth + 1, !isMax));

                        // Undo the move
                        board[i][j] = EMPTY;
                    }
                }
            }
            return best; // Return the best score found
        }
        // Minimizing player (human)
        else {
            int best = 1000; // Initialize best to the highest possible value

            // Traverse all cells
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    // Check if the cell is empty
                    if (board[i][j] == EMPTY) {
                        // Make the move
                        board[i][j] = HUMAN;

                        // Call minimax recursively and choose the minimum value
                        best = Math.min(best, minimax(depth + 1, !isMax));

                        // Undo the move
                        board[i][j] = EMPTY;
                    }
                }
            }
            return best; // Return the best score found
        }
    }

    private void computerMove() {
        Move bestMove = findBestMove(); // Determine the best move for the computer

        // Update the game board with the computer's move
        board[bestMove.row][bestMove.col] = COMPUTER;

        // Update the corresponding button on the GUI to show the computer's symbol (O)
        jB[bestMove.row][bestMove.col].setIcon(new javax.swing.ImageIcon(getClass().getResource("O.png")));
    }

    private int evaluate() {
        // Check all rows for a win
        for (int row = 0; row < SIZE; row++) {
            // Check if all cells in a row are the same
            if (board[row][0] == board[row][1] && board[row][1] == board[row][2]) {
                // If the computer occupies the entire row
                if (board[row][0] == COMPUTER) return +10;
                    // If the human occupies the entire row
                else if (board[row][0] == HUMAN) return -10;
            }
        }

        // Check all columns for a win
        for (int col = 0; col < SIZE; col++) {
            // Check if all cells in a column are the same
            if (board[0][col] == board[1][col] && board[1][col] == board[2][col]) {
                // If the computer occupies the entire column
                if (board[0][col] == COMPUTER) return +10;
                    // If the human occupies the entire column
                else if (board[0][col] == HUMAN) return -10;
            }
        }

        // Check both diagonals for a win
        // Check the diagonal from top-left to bottom-right
        if (board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            if (board[0][0] == COMPUTER) return +10;
            else if (board[0][0] == HUMAN) return -10;
        }
        // Check the diagonal from top-right to bottom-left
        if (board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            if (board[0][2] == COMPUTER) return +10;
            else if (board[0][2] == HUMAN) return -10;
        }

        // No one has won yet
        return 0;
    }

    private boolean isMovesLeft() {
        // Check cells for player markings to determine the state of the game
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == EMPTY) {
                    return true; // There is at least one cell that is still empty
                }
            }
        }
        return false; // No empty cells found, no moves left
    }

    private int checkResult() {
        // Check each row for a win
        for (int row = 0; row < SIZE; row++) {
            // If all cells in a row are the same and not empty
            if (board[row][0] == board[row][1] && board[row][1] == board[row][2]) {
                // Check who occupies the row (computer or human) and return the corresponding win constant
                if (board[row][0] == COMPUTER) return COMPUTER_WIN;
                else if (board[row][0] == HUMAN) return HUMAN_WIN;
            }
        }

        // Check each column for a win
        for (int col = 0; col < SIZE; col++) {
            // If all cells in a column are the same and not empty
            if (board[0][col] == board[1][col] && board[1][col] == board[2][col]) {
                // Check who occupies the column (computer or human) and return the corresponding win constant
                if (board[0][col] == COMPUTER) return COMPUTER_WIN;
                else if (board[0][col] == HUMAN) return HUMAN_WIN;
            }
        }

        // Check both diagonals for a win
        // Check the diagonal from top-left to bottom-right
        if (board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            if (board[0][0] == COMPUTER) return COMPUTER_WIN;
            else if (board[0][0] == HUMAN) return HUMAN_WIN;
        }
        // Check the diagonal from top-right to bottom-left
        if (board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            if (board[0][2] == COMPUTER) return COMPUTER_WIN;
            else if (board[0][2] == HUMAN) return HUMAN_WIN;
        }

        // Check for a draw by looking for any empty cell
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == EMPTY) {
                    return CONTINUE; // If there's an empty cell, the game is still ongoing
                }
            }
        }

        return DRAW; // If no empty cells and no winner, the game is a draw
    }
    /* the resetBoard() and endGame() methods are for starting a new game in the game
    instead of manually running the program over and over again.
    they are here simply for convenience.
     */
    private void resetBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = EMPTY;
                jB[i][j].setIcon(new javax.swing.ImageIcon(getClass().getResource("None.png")));
            }
        }
        turn = HUMAN; // Human starts the new game
    }

    private void endGame(int result) {
        String message;
        if (result == HUMAN_WIN) {
            message = "Congratulations! You won!";
        } else if (result == COMPUTER_WIN) {
            message = "Computer wins!";
        } else {
            message = "It's a draw!";
        }
        // Options for the JOptionPane
        Object[] options = {"Play Again", "Exit"};
        int choice = JOptionPane.showOptionDialog(this, message, "Game Over",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        if (choice == JOptionPane.YES_OPTION) {
            resetBoard();
        } else {
            System.exit(0); // Exit the game
        }
    }

    // Place a mark for one of the players (HUMAN or COMPUTER) in the specified position
    public void place (int row, int col, int player){
        board [row][col] = player;
    }

    public static void main (String [] args){

        String threadName = Thread.currentThread().getName();
        LS lsGUI = new LS();      // Create a new user interface for the game
        lsGUI.setVisible(true);

        java.awt.EventQueue.invokeLater (new Runnable() {
            public void run() {
                while ( (Thread.currentThread().getName() == threadName) &&
                        (lsGUI.checkResult() == CONTINUE) ){
                    try {
                        Thread.sleep(100);  // Sleep for 100 millisecond, wait for button press
                    } catch (InterruptedException e) { };
                }
            }
        });
    }
}
