package edu.io;

import edu.io.player.Player;

public class Main {
    public static void main(String[] args) {
        // Tworzenie gry i gracza
        Game game = new Game();
        Player player = new Player();

        // Dołączenie gracza do gry
        game.join(player);

        // Uruchomienie gry
        game.start();
    }
}