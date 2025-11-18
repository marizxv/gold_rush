package edu.io;

import edu.io.token.*;
import edu.io.player.Player;

public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        Player player = new Player();

        // Ustawienie handlera na śmierć gracza
        player.vitals.setOnDeathHandler(() -> {
            System.out.println("To koniec: pełne odwodnienie.");
        });

        game.join(player);

        Board board = new Board(5);

        // Dodanie różnych tokenów na planszę
        GoldToken gold = new GoldToken(2.0);
        PickaxeToken pickaxe = new PickaxeToken();
        AnvilToken anvil = new AnvilToken();
        WaterToken water = new WaterToken();

        board.placeToken(1, 2, gold);
        board.placeToken(2, 2, pickaxe);
        board.placeToken(3, 2, anvil);
        board.placeToken(4, 2, water);

        System.out.println("Plansza początkowa:");
        board.display();

        System.out.println("\nZłoto gracza: " + player.gold.amount() + " uncji");
        System.out.println("Nawodnienie: " + player.vitals.hydration() + "%");
        System.out.println("Czy szopka pusta? " + player.shed().isEmpty());
        System.out.println("Czy gracz żyje? " + player.vitals.isAlive());
    }
}