package com.company;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;


public class TicTacToeClient {
    private static final String HOST_ADDRESS = "localhost";
    private static final int PORT = 6789;
    private static final int BOARD_WIDTH = 510;
    private static final int BOARD_HEIGHT = 530;
    private final int SPACE_LENGTH = 160;
    public static final Color PURPLE = new Color(102, 0 ,153);

    private Socket clientSocket;
    private DataOutputStream output;
    private Scanner input;

    private JFrame frame;
    private JTextArea displayMessage;
    private DrawBoard drawBoard;
    private BufferedImage board;
    private BufferedImage blueO;
    private BufferedImage redO;
    private BufferedImage blueX;
    private BufferedImage redX;

    private String[] boardSpaces = new String[9];

    private String myMark = null;
    private boolean bothConnected = false;
    private int chosenPosition;  // position that player clicked on
    private boolean gameOver = false;
    private boolean won = false;
    private boolean lost = false;
    private boolean tie = false;

    private Font font = new Font("Arial", Font.BOLD, 28);
    private Font biggerFont = new Font("Arial", Font.BOLD, 50);

    public TicTacToeClient() {
        loadBoard();
        drawBoard = new DrawBoard();
        drawBoard.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));

        frame = new JFrame();
        frame.setTitle("Tic-Tac-Toe");
        frame.setContentPane(drawBoard);
        frame.setSize(BOARD_WIDTH, BOARD_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);

        connectToServer();
    }

    public void loadBoard() {
        try {
            board = ImageIO.read(getClass().getResourceAsStream("/board.png"));
            blueO = ImageIO.read(getClass().getResourceAsStream("/blueO.png"));
            redO = ImageIO.read(getClass().getResourceAsStream("/redO.png"));
            blueX = ImageIO.read(getClass().getResourceAsStream("/blueX.png"));
            redX = ImageIO.read(getClass().getResourceAsStream("/redX.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connectToServer() {
        try {
            clientSocket = new Socket(HOST_ADDRESS, PORT);
            //input = new DataInputStream(clientSocket.getInputStream());
            input = new Scanner(new InputStreamReader(clientSocket.getInputStream()));
            output = new DataOutputStream(clientSocket.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Connected to server.");
        // create and start thread for this client
//        ExecutorService pool = Executors.newFixedThreadPool(1);
//        pool.execute(this); // execute client
    }

    public void start() throws IOException {
        // first message from server should be a join_response
        String response = input.nextLine();
        processMessage(response);

        while (input.hasNextLine()) {
            response = input.nextLine();
            processMessage(response);
        }
        clientSocket.close();
        frame.dispose();
    }

    // process messages received from the server
    private void processMessage(String message) {
        String[] messageParts = message.split(";");

        if (message.startsWith("JOIN_RESPONSE")) {
            if (messageParts[1].equals("X")) {
                myMark = "X";
                System.out.println("My marks is: " + myMark);
                //playerX = true;
                //myTurn = true;
            } else {
                myMark = "O";
                System.out.println("My marks is: " + myMark);
                bothConnected = true;
                drawBoard.repaint();
                //myTurn = true;
            }
        } else if (message.equals("WAIT")) {
            bothConnected = false;
            drawBoard.repaint();
        } else if (message.equals("OPPONENT_CONNECTED")) {
            bothConnected = true;
            System.out.println("bothConnected = " + bothConnected);
            drawBoard.repaint();
        } else if (message.equals("VALID_MOVE")) {
            // update GUI
            setMark(chosenPosition);
        } else if (message.startsWith("OPPONENT_MOVE")) {
            int position = Integer.parseInt(messageParts[1]);
            setOpponentMark(position);
        } else if (message.startsWith("GAME_OVER")) {
            gameOver = true;
            // print appropriate game over message
            if (messageParts[1].equals("WON")) {
                won = true;
                // print something
                drawBoard.repaint();
            } else if (messageParts[1].equals("LOST")) {
                lost = true;
                drawBoard.repaint();
                // print something
            } else { // it's a tie
                tie = true;
                drawBoard.repaint();
                // print something
            }
        }
    }

    private void sendMoveToServer(int position) throws IOException {
        // send position
        System.out.println("Sending MOVE to server");
        String messageToServer = "MOVE;" + position + '\n';
        output.writeBytes(messageToServer);
        //output.flush();  // necessary?
        //myTurn = false;
    }

    private void setMark(int position) {
        boardSpaces[position] = myMark;
        drawBoard.repaint();
    }

    private void setOpponentMark(int position) {
        if (myMark.equals("X")) {
            boardSpaces[position] = "O";
        } else {
            boardSpaces[position] = "X";
        }
        drawBoard.repaint();
    }

    private void render(Graphics graphics) {
        if (bothConnected) {
            graphics.drawImage(board, 0, 0, null);
            for (int i = 0; i < boardSpaces.length; ++i) {
                int x = (i % 3) * SPACE_LENGTH + (10 * (i % 3));
                int y = (i / 3) * SPACE_LENGTH + (10 * (i / 3));
                if (boardSpaces[i] != null) {
                    if (boardSpaces[i].equals("X")) {
                        if (myMark.equals("X")) {
                            graphics.drawImage(blueX, x, y, null);
                        } else {
                            graphics.drawImage(redX, x, y, null);
                        }
                    } else if (boardSpaces[i].equals("O")) {
                        if (myMark.equals("X")) {
                            graphics.drawImage(redO, x, y, null);
                        } else {
                            graphics.drawImage(blueO, x, y, null);
                        }
                    }
                }
            }

            // print win/lose message to screen
            if (won || lost) {
                Graphics2D graphics2D = (Graphics2D) graphics;
                graphics.setFont(biggerFont);
                String playAgain = "Play again?";
                if (won) {
                    graphics.setColor(Color.GREEN);
                    String wonString = "You won!";
                    int stringWidth = graphics2D.getFontMetrics().stringWidth(wonString);
                    graphics.drawString(wonString, BOARD_WIDTH / 2 - stringWidth / 2, BOARD_HEIGHT / 2);
                } else if (lost) {
                    graphics.setColor(Color.GREEN);
                    String lostString = "Opponent won.";
                    int stringWidth = graphics2D.getFontMetrics().stringWidth(lostString);
                    graphics.drawString(lostString, BOARD_WIDTH / 2 - stringWidth / 2, BOARD_HEIGHT / 2);
                }
                int playAgainWidth = graphics2D.getFontMetrics().stringWidth(playAgain);
                graphics.drawString(playAgain, BOARD_WIDTH / 2 - playAgainWidth / 2, BOARD_HEIGHT-(BOARD_HEIGHT/3));
            }
            if (tie) {
                Graphics2D graphics2D = (Graphics2D) graphics;
                graphics.setColor(Color.GREEN);
                graphics.setFont(biggerFont);
                String tieString = "It's a tie!";
                String playAgain = "Play again?";
                int stringWidth = graphics2D.getFontMetrics().stringWidth(tieString);
                graphics.drawString(tieString, BOARD_WIDTH / 2 - stringWidth / 2, BOARD_HEIGHT / 2);
                int playAgainWidth = graphics2D.getFontMetrics().stringWidth(playAgain);
                graphics.drawString(playAgain, BOARD_WIDTH / 2 - playAgainWidth / 2, BOARD_HEIGHT-(BOARD_HEIGHT/3));
            }
        } else {  // still waiting to connect
            graphics.setColor(PURPLE);
            graphics.setFont(font);
            Graphics2D graphics2D = (Graphics2D) graphics;
            graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            String waiting = "Waiting for another player...";
            int stringWidth = graphics2D.getFontMetrics().stringWidth(waiting);
            graphics.drawString(waiting, (BOARD_WIDTH / 2) - (stringWidth / 2), BOARD_HEIGHT / 2);
        }
    }

    private class DrawBoard extends JPanel implements MouseListener {

        public DrawBoard() {
            setFocusable(true);
            requestFocus();
            setBackground(Color.WHITE);
            addMouseListener(this);
        }

        @Override
        public void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            render(graphics);
        }

        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            if (bothConnected) {
                // your turn and the game hasn't ended yet
                if (!gameOver) {
                    int x = mouseEvent.getX() / SPACE_LENGTH;
                    int y = mouseEvent.getY() / SPACE_LENGTH;
                    y = y * 3;
                    chosenPosition = x + y;
                    try {
                        sendMoveToServer(chosenPosition);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toolkit.getDefaultToolkit().sync();
                }
            }
        } // end mouseClicked()

        @Override
        public void mousePressed(MouseEvent mouseEvent) {}

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {}

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {}

        @Override
        public void mouseExited(MouseEvent mouseEvent) {}
    } // end DrawBoard class
} // end TicTacToeClient class