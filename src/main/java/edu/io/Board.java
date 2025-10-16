package edu.io;

public class Board {
    public int size;
    public Token[][] grid;
    private static final String EMPTY_TOKEN_LABEL = "・";

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
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = new Token(EMPTY_TOKEN_LABEL);
            }
        }
    }

    public void placeToken(int x, int y, Token token) {
        checkBounds(x, y);
        grid[y][x] = token;
    }

    public Token square(int x, int y) {
        checkBounds(x, y);
        return grid[y][x];
    }

    public void display() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(grid[i][j].label + " ");
            }
            System.out.println();
        }
    }

    private void checkBounds(int x, int y) {
        if (x < 0 || x >= size || y < 0 || y >= size) {
            throw new IllegalArgumentException(
                    String.format("Coordinates out of bounds: (%d,%d) for size %d", x, y, size));
        }
    }
}