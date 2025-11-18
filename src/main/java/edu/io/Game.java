package edu.io;
import edu.io.player.Player;
import edu.io.token.*;
import java.util.Objects;
import java.util.Scanner;

public class Game {
    private Board board;
    private Player player;

    public void join(Player player) {
        this.player = Objects.requireNonNull(player, "Player cannot be null");
        PlayerToken token = new PlayerToken(player, board);
        player.assignToken(token);

        // ustawienie handlera na śmierć gracza
        player.vitals.setOnDeathHandler(() -> {
            System.out.println("To koniec: pełne odwodnienie. Gra się kończy!");
        });
    }

    public Game() {
        this.board = new Board(5);
        this.player = new Player();
        setupGame();
    }


    public void start() {
        System.out.println("=== GOLD RUSH ===");
        System.out.println("Gra się rozpoczęła!");
        board.display();

        Scanner scanner = new Scanner(System.in);
        boolean gameRunning = true;

        while (gameRunning && player.vitals.isAlive()) {
            System.out.println("\n--- Stan gracza ---");
            System.out.println("Złoto: " + player.gold.amount() + " uncji");
            System.out.println("Nawodnienie: " + player.vitals.hydration() + "%");
            System.out.println("Czy szopka pusta? " + player.shed().isEmpty());

            System.out.println("\nDostępne komendy:");
            System.out.println("góra, dół, lewo, prawo - ruch");
            System.out.println("koniec - zakończ grę");
            System.out.print("Twój wybór: ");

            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "góra", "gora", "up" -> executeMove(PlayerToken.Move.UP);
                case "dół", "dol", "down" -> executeMove(PlayerToken.Move.DOWN);
                case "lewo", "left" -> executeMove(PlayerToken.Move.LEFT);
                case "prawo", "right" -> executeMove(PlayerToken.Move.RIGHT);
                case "koniec", "exit" -> {
                    System.out.println("Koniec gry!");
                    gameRunning = false;
                }
                default -> System.out.println("Nieznana komenda: " + input);
            }

            if (player.vitals.hydration() < 30) {
                System.out.println("⚠  Uwaga: Niskie nawodnienie! Szukaj wody!");
            }
        }

        if (!player.vitals.isAlive()) {
            System.out.println("\n Gracz nie żyje z powodu odwodnienia!");
        }

        System.out.println("\n=== KONIEC GRY ===");
        System.out.println("Końcowy stan złota: " + player.gold.amount() + " uncji");
        scanner.close();
    }

    private void executeMove(PlayerToken.Move move) {
        try {
            player.token().move(move);
            board.display();
        } catch (IllegalArgumentException e) {
            System.out.println("Nie można wykonać ruchu: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("Nie można wykonać ruchu: " + e.getMessage());
        }
    }

    private void setupGame() {
        // umieszczenie różnych tokenów na planszy
        board.placeToken(1, 2, new GoldToken(2.0));
        board.placeToken(2, 2, new PickaxeToken());
        board.placeToken(3, 2, new AnvilToken());
        board.placeToken(4, 2, new WaterToken());
        board.placeToken(0, 3, new GoldToken(1.5));
        board.placeToken(4, 4, new WaterToken(50)); // Duża butelka wody
    }

    // uzyskanie planszy
    public Board getBoard() {
        return board;
    }
}