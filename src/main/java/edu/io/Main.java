package edu.io;

public class Main {
    public static void main(String[] args) {
        Board board = new Board(5);

        Token gold = new Token("\uD83D\uDCB0");
        Token player = new Token("\uC6C3");

        board.placeToken(1, 2, gold);
        board.placeToken(3, 4, player);

        board.display();
    }
}
