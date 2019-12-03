package com.company;
import java.io.*;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {
    private static final int SERVER_PORT = 6789;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
        System.out.println("Waiting for clients to connect...");
        ExecutorService pool = Executors.newFixedThreadPool(2);
        while (true) {
            TicTacToeServer ticTacToe = new TicTacToeServer();
            TicTacToeServer.Player player1 = ticTacToe.new Player(serverSocket.accept(), "X");
            pool.execute(player1);
            TicTacToeServer.Player player2 = ticTacToe.new Player(serverSocket.accept(), "O");
            pool.execute(player2);
        }
    }
}
