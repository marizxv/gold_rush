package edu.io;

import edu.io.token.GoldToken;
import edu.io.token.PlayerToken;

public class Main {
    public static void main(String[] args) {
        Board board = new Board(5);

        GoldToken gold = new GoldToken();
        PlayerToken player = new PlayerToken(board, 3, 4);

        board.placeToken(1, 2, gold);

        board.display();
    }
}