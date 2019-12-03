package com.company;

import java.io.IOException;


public class Client {
    public static void main(String[] args) throws IOException {
        TicTacToeClient client = new TicTacToeClient();
        client.start();
    }
}
