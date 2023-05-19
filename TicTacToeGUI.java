package finalproject;

import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TicTacToeGUI extends JFrame implements ActionListener {
    private static final int PLAYER = 1;
    private static final int COMPUTER = 2;
    private static final int EMPTY = 0;
    private static final int BOARD_SIZE = 3;
    private static int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
    private static JButton[][] buttonGrid = new JButton[BOARD_SIZE][BOARD_SIZE];
    private static JButton resetButton = new JButton("Reset");
    private static JLabel statusLabel = new JLabel("Player's turn");

    public TicTacToeGUI() {
        setTitle("Tic Tac Toe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel gamePanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                JButton button = new JButton("-");
                buttonGrid[i][j] = button;
                button.addActionListener(this);
                gamePanel.add(button);
            }
        }

        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetGame();
            }
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(resetButton);
        buttonPanel.add(statusLabel);

        JPanel mainPanel = new JPanel();
        mainPanel.add(gamePanel);
        mainPanel.add(buttonPanel);
        add(mainPanel);

        pack();
        setVisible(true);

        initializeBoard();
    }

    public void actionPerformed(ActionEvent event) {
        JButton button = (JButton) event.getSource();
        int row = -1;
        int column = -1;
        boolean found = false;
        for (int i = 0; i < BOARD_SIZE && !found; i++) {
            for (int j = 0; j < BOARD_SIZE && !found; j++) {
                if (buttonGrid[i][j] == button) {
                    row = i;
                    column = j;
                    found = true;
                }
            }
        }
        if (isValidMove(row, column)) {
            button.setText("X");
            board[row][column] = PLAYER;
            if (!gameOver()) {
                statusLabel.setText("Computer's turn");
                int[] move = getBestMove();
                board[move[0]][move[1]] = COMPUTER;
                buttonGrid[move[0]][move[1]].setText("O");
                if (!gameOver()) {
                    statusLabel.setText("Player's turn");
                }
            }
        }
        if (getWinner() == PLAYER) {
            statusLabel.setText("Player wins!");
        } else if (getWinner() == COMPUTER) {
            statusLabel.setText("Computer wins!");
        } else if (isBoardFull()) {
            statusLabel.setText("It's a draw!");
        }
        resetButton.setVisible(true);
    }

    private static void initializeBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = EMPTY;
            }
        }
        resetButton.setVisible(false);
    }

    private static boolean isValidMove(int row, int column) {
        return row >= 0 && row < BOARD_SIZE && column >= 0 && column < BOARD_SIZE && board[row][column] == EMPTY;
    }

    private static boolean gameOver() {
        return getWinner() != EMPTY || isBoardFull();
    }

    private static boolean isBoardFull() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    private static int getWinner() {
        int winner = EMPTY;
        // check rows
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[i][0] == board[i][1] && board[i][1] == board[i][2] && board[i][0] != EMPTY) {
                winner = board[i][0];
            }
        }
        // check columns
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[0][i] == board[1][i] && board[1][i] == board[2][i] && board[0][i] != EMPTY) {
                winner = board[0][i];
            }
        }
        // check diagonals
        if (board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[0][0] != EMPTY) {
            winner = board[0][0];
        }
        if (board[0][2] == board[1][1] && board[1][1] == board[2][0] && board[0][2] != EMPTY) {
            winner = board[0][2];
        }
        return winner;
    }

    private static int[] getBestMove() {
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = new int[2];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == EMPTY) {
                    board[i][j] = COMPUTER;
                    int score = alphaBetaPruning(0, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
                    board[i][j] = EMPTY;
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove[0] = i;
                        bestMove[1] = j;
                    }
                }
            }
        }
        return bestMove;
    }

    private static int evaluate() {
        int score = 0;
        // check rows
        for (int i = 0; i < BOARD_SIZE; i++) {
            int playerCount = 0;
            int computerCount = 0;
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == PLAYER) {
                    playerCount++;
                } else if (board[i][j] == COMPUTER) {
                    computerCount++;
                }
            }
            if (playerCount == BOARD_SIZE) {
                return -10;
            } else if (computerCount == BOARD_SIZE) {
                return 10;
            }
        }
        // check columns
        for (int i = 0; i < BOARD_SIZE; i++) {
            int playerCount = 0;
            int computerCount = 0;
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[j][i] == PLAYER) {
                    playerCount++;
                } else if (board[j][i] == COMPUTER) {
                    computerCount++;
                }
            }
            if (playerCount == BOARD_SIZE) {
                return -10;
            } else if (computerCount == BOARD_SIZE) {
                return 10;
            }
        }
        // check diagonals
        int playerCount = 0;
        int computerCount = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[i][i] == PLAYER) {
                playerCount++;
            } else if (board[i][i] == COMPUTER) {
                computerCount++;
            }
        }
        if (playerCount == BOARD_SIZE) {
            return -10;
        } else if (computerCount == BOARD_SIZE) {
            return 10;
        }
        playerCount = 0;
        computerCount = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[i][BOARD_SIZE - i - 1] == PLAYER) {
                playerCount++;
            } else if (board[i][BOARD_SIZE - i - 1] == COMPUTER) {
                computerCount++;
            }
        }
        if (playerCount == BOARD_SIZE) {
            return -10;
        } else if (computerCount == BOARD_SIZE) {
            return 10;
        }
        return score;
    }

    private static int alphaBetaPruning(int depth, int alpha, int beta, boolean isMaximizingPlayer) {
        int score = evaluate();
        if (score == 10 || score == -10) {
            return score;
        }
        if (isBoardFull()) {
            return 0;
        }
        if (isMaximizingPlayer) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (board[i][j] == EMPTY) {
                        board[i][j] = COMPUTER;
                        bestScore = Math.max(bestScore, alphaBetaPruning(depth + 1, alpha, beta, false));
                        board[i][j] = EMPTY;
                        alpha = Math.max(alpha, bestScore);
                        if (beta <= alpha) {
                            break;
                        }
                    }
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (board[i][j] == EMPTY) {
                        board[i][j] = PLAYER;
                        bestScore = Math.min(bestScore, alphaBetaPruning(depth + 1, alpha, beta, true));
                        board[i][j] = EMPTY;
                        beta = Math.min(beta, bestScore);
                        if (beta <= alpha) {
                            break;
                        }
                    }
                }
            }
            return bestScore;
        }
    }

    private static void resetGame() {
        initializeBoard();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                buttonGrid[i][j].setText("-");
            }
        }
        statusLabel.setText("Player's turn");
        resetButton.setVisible(false);
    }

    public static void main(String[] args) {
        new TicTacToeGUI();
    }
}