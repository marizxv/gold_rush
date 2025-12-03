package edu.io.net;

import edu.io.net.command.*;
import java.util.Scanner;

import static edu.io.net.command.Handshake.CmdRe.Status.*;
import static edu.io.net.command.Handshake.CmdRe.Status.OK;
import static edu.io.net.command.JoinGame.CmdRe.Status.*;

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
                switch (cmdRe.status()) {
                    case OK -> System.out.println("Handshake udany!");
                    case LIB_VERSION_TOO_LOW -> System.out.println("Wersja biblioteki zbyt niska");
                    case LIB_VERSION_MALFORMED -> System.out.println("Błędny format wersji");
                    default -> System.out.println("Nieznany status");
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
                switch (cmdRe.status()) {
                    case OK -> System.out.println("Dołączono! ID: " + cmdRe.clientId);
                    case NAME_ALREADY_EXISTS -> System.out.println("Nazwa zajęta");
                    case ALREADY_CONNECTED -> System.out.println("Już połączony");
                    default -> System.out.println("Nieznany status");
                }
            }
        });

        // odbieranie poleceń z serwera
        gsc.onCmdFromServer(cmd -> {
            System.out.println("\n[Serwer] " + cmd.getClass().getSimpleName());

            switch (cmd) {
                case Echo.CmdRe echo -> System.out.println("Echo: " + echo.msg);

                case UpdateState.Cmd update -> {
                    System.out.println("UpdateState – elementów: " + update.stateInfoList.size());
                    update.stateInfoList.forEach(Main::handleGameState);
                }

                case CommandAck ack -> System.out.println("Potwierdzenie: " + ack.reqCmd().getClass().getSimpleName());

                case GetInfo.CmdRe gi -> System.out.println("GetInfo: " + gi.info);

                default -> System.out.println("Nieznany typ komendy");
            }
        });

        // czeka na zakończenie
        System.out.println("\nNaciśnij Enter aby zakończyć...");
        scanner.nextLine();

        gsc.disconnect();
        System.out.println("Rozłączono.");
    }


    // pomocnicza obsługa UpdateState
    private static void handleGameState(GameState state) {
        switch (state) {
            case GameState.BoardInfo board ->
                    System.out.println("  Plansza: rozmiar " + board.size());

            case GameState.BoardSquareInfo square ->
                    System.out.println("  Pole [" + square.pos().col() + "," + square.pos().row() + "]: " + square.label());

            case GameState.PlayerInfo player ->
                    System.out.println("  Gracz: " + player.name()
                            + " | złoto: " + player.gold()
                            + " | nawodnienie: " + player.hydration() + "%"
                            + " | narzędzia: " + player.tools());

            case GameState.PlayerListInfo list -> {
                System.out.println("  Lista graczy (" + list.players().size() + "):");
                for (int i = 0; i < list.players().size(); i++) {
                    var p = list.players().get(i);
                    var marker = (i == list.activePlayerIdx()) ? "→" : " ";
                    System.out.println("    " + marker + " " + p.name()
                            + " @ [" + p.pos().col() + "," + p.pos().row() + "]");
                }
            }

            default ->
                    System.out.println("  Nieznany typ GameState: " + state.getClass().getSimpleName());
        }
    }
}