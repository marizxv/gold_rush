package edu.io.token;

import edu.io.Board;
import edu.io.player.Player;

public class PlayerToken extends Token {
    public enum Move {UP, DOWN, LEFT, RIGHT, NONE}

    private final Board board;
    private final Player player;
    private int col;
    private int row;

    public PlayerToken(Player player, Board board) {
        super(Label.PLAYER_TOKEN_LABEL);
        this.board = board;
        this.player = player;
        Board.Coords coords = board.getAvailableSquare();
        this.col = coords.col();
        this.row = coords.row();
        board.placeToken(col, row, this);
    }

    public void move(Move direction) {
        int newCol = col;
        int newRow = row;

        switch (direction) {
            case UP:
                newRow--;
                break;
            case DOWN:
                newRow++;
                break;
            case LEFT:
                newCol--;
                break;
            case RIGHT:
                newCol++;
                break;
            case NONE:
                return;
        }

        // Sprawdzanie granic przed aktualizacją pozycji
        if (newCol < 0 || newCol >= board.size() || newRow < 0 || newRow >= board.size()) {
            throw new IllegalArgumentException("Cannot move outside the board");
        }

        // interakcja z tokenem na nowym polu
        Token tokenOnNewSquare = board.peekToken(newCol, newRow);
        player.interactWithToken(tokenOnNewSquare);

        // Zapis starej pozycji
        int oldCol = col;
        int oldRow = row;

        // Aktualizacja pozycji
        col = newCol;
        row = newRow;

        // Umieszczenie tokena na nowej pozycji
        board.placeToken(col, row, this);

        // Na starej pozycji umieszczenie EmptyToken
        board.placeToken(oldCol, oldRow, new EmptyToken());
    }

    public Board.Coords pos(){
        return new Board.Coords(col, row);
    }
}

