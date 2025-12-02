package edu.io.net;

import edu.io.net.command.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Gold Rush Client ===");

        // tworzy GameServerConnector z SocketConnector
        var gsc = new GameServerConnector("tcp://localhost:1313", new SocketConnector());

        gsc.connect();
        if (!gsc.isConnected()) {
            System.err.println("Błąd: Nie można połączyć się z serwerem!");
            return;
        }
        System.out.println("Połączono z serwerem.");

        // handshake
        gsc.issueCommand(new Handshake.Cmd("1.1.19"), res -> {
            if (res instanceof Handshake.CmdRe cmdRe) {
                System.out.println("Handshake: " + cmdRe.status());
                if (cmdRe.status() == Handshake.CmdRe.Status.OK) {
                    System.out.println("Handshake udany!");
                } else {
                    System.out.println("Handshake nieudany: " + cmdRe.msg);
                }
            }
        });

        // pyta o imię gracza
        System.out.print("\nPodaj imię gracza: ");
        var scanner = new Scanner(System.in);
        var name = scanner.nextLine();

        // dołącza do gry
        gsc.issueCommand(new JoinGame.Cmd(name), res -> {
            if (res instanceof JoinGame.CmdRe cmdRe) {
                System.out.println("JoinGame: " + cmdRe.status());
                if (cmdRe.status() == JoinGame.CmdRe.Status.OK) {
                    System.out.println("Dołączono do gry! ID: " + cmdRe.clientId());
                } else {
                    System.out.println("Nie udało się dołączyć: " + cmdRe.msg);
                }
            }
        });

        // handler dla poleceń od serwera
        gsc.onCmdFromServer(cmd -> {
            System.out.println("\n[Otrzymano z serwera] " + cmd.getClass().getSimpleName());

            if (cmd instanceof Echo.CmdRe echo) {
                System.out.println("Echo: " + echo.msg);
            }
            else if (cmd instanceof UpdateState.Cmd update) {
                System.out.println("UpdateState - elementów: " + update.stateInfoList.size());
                for (var state : update.stateInfoList) {
                    handleGameState(state);
                }
            }
            else if (cmd instanceof CommandAck ack) {
                System.out.println("Potwierdzenie: " + ack.reqCmd().getClass().getSimpleName());
            }
        });

        // czeka na zakończenie
        System.out.println("\nNaciśnij Enter aby zakończyć...");
        scanner.nextLine();

        gsc.disconnect();
        System.out.println("Rozłączono.");
    }

    private static void handleGameState(GameState state) {
        if (state instanceof GameState.BoardInfo board) {
            System.out.println("  Plansza: rozmiar " + board.size());
        }
        else if (state instanceof GameState.BoardSquareInfo square) {
            System.out.println("  Pole [" + square.pos().col() + "," + square.pos().row() + "]: " + square.label());
        }
        else if (state instanceof GameState.PlayerInfo player) {
            System.out.println("  Gracz: " + player.name() +
                    ", złoto: " + player.gold() +
                    ", nawodnienie: " + player.hydration() + "%" +
                    ", narzędzia: " + player.tools());
        }
        else if (state instanceof GameState.PlayerListInfo players) {
            System.out.println("  Lista graczy (" + players.players().size() + "):");
            for (int i = 0; i < players.players().size(); i++) {
                var p = players.players().get(i);
                System.out.println("    " + (i == players.activePlayerIdx() ? "→ " : "  ") +
                        p.name() + " @ [" + p.pos().col() + "," + p.pos().row() + "]");
            }
        }
    }
}