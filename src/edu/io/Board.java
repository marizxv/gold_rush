package edu.io;

public class Board {
    public int size;
    public Token[][] grid;
    private static final String EMPTY_SQUARE = "・";

    public Board() {
        this(10); // domyślny rozmiar
    }

    public Board(int size) {
        this.size = size;
        this.grid = new Token[size][size];
        clean();
    }

    public void clean() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = new Token(EMPTY_SQUARE);
            }
        }
    }

    public void placeToken(int x, int y, Token token) {
        if (x >= 0 && x < size && y >= 0 && y < size) { //x = col; y = row
            grid[x][y] = token;
        }
    }

    public Token square(int x, int y) {
        if (x >= 0 && x < size && y >= 0 && y < size) {
            return grid[x][y];
        }
        return null;
    }

    public void display() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(grid[i][j].label + " ");
            }
            System.out.println();
        }
    }
}