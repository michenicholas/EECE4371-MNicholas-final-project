//package com.company;
import java.io.*;
import java.net.ServerSocket;


public class Server {
    private static final int SERVER_PORT = 6789;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
        System.out.println("Waiting for clients to connect...");
        while (true) {
            TicTacToeServer ticTacToe = new TicTacToeServer();
            TicTacToeServer.Player player1 = ticTacToe.new Player(serverSocket.accept(), "X");
            player1.start();
            TicTacToeServer.Player player2 = ticTacToe.new Player(serverSocket.accept(), "O");
            player2.start();
        }
    }
}
