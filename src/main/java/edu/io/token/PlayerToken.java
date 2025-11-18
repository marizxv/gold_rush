package edu.io.token;

import edu.io.Board;
import edu.io.player.Player;
import java.util.Objects;

public class PlayerToken extends Token {
    public enum Move {UP, DOWN, LEFT, RIGHT, NONE}

    private final Board board;
    private final Player player;
    private int col;
    private int row;

    public PlayerToken(Player player, Board board) {
        super(Label.PLAYER_TOKEN_LABEL);
        this.board = Objects.requireNonNull(board, "Board cannot be null");
        this.player = Objects.requireNonNull(player, "Player cannot be null");
        Board.Coords coords = board.getAvailableSquare();
        this.col = coords.col();
        this.row = coords.row();
        board.placeToken(col, row, this);
    }

    public void move(Move direction) {
        Objects.requireNonNull(direction, "Direction cannot be null");

        if (!player.vitals.isAlive()) {
            throw new IllegalStateException("Player is dead and cannot move");
        }

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

        if (newCol < 0 || newCol >= board.size() || newRow < 0 || newRow >= board.size()) {
            throw new IllegalArgumentException("Cannot move outside the board");
        }

        Token tokenOnNewSquare = board.peekToken(newCol, newRow);
        player.interactWithToken(tokenOnNewSquare);

        int oldCol = col;
        int oldRow = row;

        col = newCol;
        row = newRow;

        board.placeToken(col, row, this);
        board.placeToken(oldCol, oldRow, new EmptyToken());
    }

    public Board.Coords pos(){
        return new Board.Coords(col, row);
    }
}