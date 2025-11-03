package edu.io;

import edu.io.token.GoldToken;
import edu.io.token.PickaxeToken;
import edu.io.token.AnvilToken;
import edu.io.player.Player;

public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        Player player = new Player();
        game.join(player);

        Board board = new Board(5);

        // dodaje złoto, kilof i kowadło na planszę
        GoldToken gold = new GoldToken(2.0);
        PickaxeToken pickaxe = new PickaxeToken();
        AnvilToken anvil = new AnvilToken();

        board.placeToken(1, 2, gold);
        board.placeToken(2, 2, pickaxe);
        board.placeToken(3, 2, anvil);

        System.out.println("Plansza początkowa:");
        board.display();

        System.out.println("\nZłoto gracza: " + player.gold() + " uncji");
    }
}