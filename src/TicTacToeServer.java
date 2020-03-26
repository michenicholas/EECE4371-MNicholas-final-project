//package com.company;

import java.io.*;
import java.net.Socket;
import java.util.*;


public class TicTacToeServer {
    private Player currentPlayer;

    // I changed this to an int array because comparing a String with null using String.equals() in
    // the checkForWinner() method throws a NullPointerException
    // 1 represents X
    // 2 represents O
    private int[] boardSpaces = new int[9]; // tic tac toe board

    public TicTacToeServer() {
        // nothing to do
    }

    private boolean playerMove(Player player, int position) throws IOException {
        if (player.opponent == null) {
            player.output.writeBytes("WAIT\n");
            System.out.println("Sent WAIT to client");
            return false;
        } else if (player != currentPlayer) {
            player.output.writeBytes("INVALID_MOVE\n");
            System.out.println("Sent INVALID_MOVE to client because it's not their turn");
            return false;
        } else if (boardSpaces[position] != 0) {
            player.output.writeBytes("INVALID_MOVE\n");
            System.out.println("Sent INVALID_MOVE to client because the space is taken");
            return false;
        } else {
            int num;
            if (player.myMark.equals("X")) {
                num = 1;
            } else {
                num = 2;
            }
            boardSpaces[position] = num;
            currentPlayer = player.opponent;
            return true;
        }
    }

    private boolean checkForWinner(int num) {
        return (boardSpaces[0] != 0 && boardSpaces[0] == num && boardSpaces[1] == num && boardSpaces[2] == num)
                || (boardSpaces[3] != 0 && boardSpaces[3]== num && boardSpaces[4]== num && boardSpaces[5]== num)
                || (boardSpaces[6] != 0 && boardSpaces[6]== num && boardSpaces[7]== num && boardSpaces[8]== num)
                || (boardSpaces[0] != 0 && boardSpaces[0]== num && boardSpaces[3]== num && boardSpaces[6]== num)
                || (boardSpaces[1] != 0 && boardSpaces[1]== num && boardSpaces[4]== num && boardSpaces[7]== num)
                || (boardSpaces[2] != 0 && boardSpaces[2]== num && boardSpaces[5]== num && boardSpaces[8]== num)
                || (boardSpaces[0] != 0 && boardSpaces[0]== num && boardSpaces[4]== num && boardSpaces[8]== num)
                || (boardSpaces[2] != 0 && boardSpaces[2]== num && boardSpaces[4]== num && boardSpaces[6] == num);
    }

    private boolean checkForTie() {
        // call this after checkForWinner, then all you need to do is check if all the spaces are filled
        for (int boardSpace : boardSpaces) {
            if (boardSpace == 0) {
                return false;
            }
        }
        return true;
    }

    // keeps track of player connection information
    class Player extends Thread {
        Socket clientSocket;
        Scanner input;
        DataOutputStream output;
        String myMark;
        Player opponent;
        boolean won;
        boolean lost;
        boolean tie;

        public Player(Socket socket, String mark) {
            clientSocket = socket;
            myMark = mark;
        }

        public void run() {
            try {
                connectToClient();
                // keep processing messages from clients
                processMessage();
            } catch (IOException e) {
                e.printStackTrace();
            } finally { // both players left the game
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void connectToClient() throws IOException {
            input = new Scanner(clientSocket.getInputStream());
            output = new DataOutputStream(clientSocket.getOutputStream());
            output.writeBytes("JOIN_RESPONSE;" + myMark + '\n');
            System.out.println("Sent JOIN_RESPONSE;" + myMark + " to client");

            if (myMark.equals("X")) {  // player 1
                currentPlayer = this;
                output.writeBytes("WAIT\n");
                System.out.println("Sent WAIT to client");

            } else {  // player 2
                opponent = currentPlayer;
                opponent.opponent = this;
                System.out.println("Sending opponent connected to player 1");
                opponent.output.writeBytes("OPPONENT_CONNECTED\n");
                System.out.println("Current player is: " + currentPlayer.myMark);
            }
        }

        private void processMessage() throws IOException {
            while (input.hasNextLine()) {  // keep processing incoming messages
                String message = input.nextLine();
                if (message.startsWith("MOVE")) {
                    System.out.println("Got MOVE message from client");
                    String[] messageParts = message.split(";");
                    int position = Integer.parseInt(messageParts[1]);

                    // check to make sure the position is valid
                    if (playerMove(this, position)) {
                        output.writeBytes("VALID_MOVE\n");
                        System.out.println("Sent VALID_MOVE to client");
                        opponent.output.writeBytes("OPPONENT_MOVE;" + position + '\n');
                        System.out.println("Sent OPPONENT_MOVE;" + position + " to opponent");
                    }
                }
                int num;
                int opponentNum;
                if (myMark.equals("X")) {
                    num = 1;
                    opponentNum = 2;
                } else {
                    num = 2;
                    opponentNum = 1;
                }
                won = checkForWinner(num);
                lost = checkForWinner(opponentNum);
                tie = checkForTie();
                if (won || lost || tie) {
                    sendGameOverMessage();
                }
            }
        } // end processMessage

        private void sendGameOverMessage() throws IOException {
            if (won) {
                output.writeBytes("GAME_OVER;WON\n");
                opponent.output.writeBytes("GAME_OVER;LOST\n");
            } else if (lost) {
                output.writeBytes("GAME_OVER;LOST");
                opponent.output.writeBytes("GAME_OVER;WON\n");
            } else {
                output.writeBytes("GAME_OVER;TIE");
                opponent.output.writeBytes("GAME_OVER;TIE\n");
            }
            System.out.println("Sent GAME_OVER messages to clients");
        }
    } // end Player class
} // end TicTacToeServer class
