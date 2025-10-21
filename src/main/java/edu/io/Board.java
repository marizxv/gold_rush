package edu.io;

import edu.io.token.EmptyToken;
import edu.io.token.Token;

public class Board {
    public int size;
    public Token[][] grid;

    public Board() {
        this(10); // domyślny rozmiar
    }

    public Board(int size) {
        if (size <= 0) throw new IllegalArgumentException("Size must be > 0");
        this.size = size;
        this.grid = new Token[size][size];
        clean(); // inicjalizacja planszy
    }

    public void clean() {
        EmptyToken emptyToken = new EmptyToken();
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                grid[row][col] = emptyToken;
            }
        }
    }

    public int size() {
        return size;
    }

    public Token placeToken(int row, int col, Token token) {
        checkBounds(row, col);
        return grid[row][col];
    }

    public Token peekToken(int col, int row) {
        checkBounds(col, row);
        return grid[row][col];
    }

    public void display() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                System.out.print(grid[row][col].label() + " ");
            }
            System.out.println();
        }
    }

    private void checkBounds(int col, int row) {
        if (col < 0 || col >= size || row < 0 || row >= size) {
            throw new IllegalArgumentException(
                    String.format("Coordinates out of bounds: (%d,%d) for size %d", col, row, size));
        }
    }

    public record Coords(int col, int row) {}
}