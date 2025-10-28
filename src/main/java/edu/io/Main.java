package edu.io;

import edu.io.token.GoldToken;
import edu.io.token.PlayerToken;

public class Main {
    public static void main(String[] args) {
        // tworzenie gry i gracza
        Game game = new Game();
        Player player = new Player();
        game.join(player);

        // umieszczenie złota na planszy
        Board board = new Board(5);
        GoldToken gold = new GoldToken();
        board.placeToken(1, 2, gold);

        // umieszczenie gracza na planszy
        PlayerToken playerToken = new PlayerToken(player, board);

        System.out.println("Plansza początkowa:");
        board.display();

        //zimprowizowany test poruszania się
        System.out.println("\nPo ruchu gracza w prawo:");
        try {
            playerToken.move(PlayerToken.Move.RIGHT);
            board.display();
        } catch (IllegalArgumentException e) {
            System.out.println("Nie można się ruszyć: " + e.getMessage());
        }

        System.out.println("\nZłoto gracza: " + player.gold() + " uncji");
    }
}