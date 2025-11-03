package edu.io;
import edu.io.player.Player;
import edu.io.token.PlayerToken;

public class Game {
    private Board board = new Board();
    private Player player;

    public void join(Player player) {
        this.player = player;
        PlayerToken token = new PlayerToken(player, board);
        player.assignToken(token);
    }

    public void start() {
//TODO: add implementation
        System.out.println("Gra się rozpoczęła!");
        board.display();
    }

    // uzyskanie planszy
    public Board getBoard() {
        return board;
    }
}