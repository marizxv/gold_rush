package edu.io.net;

import edu.io.net.client.GameServerConnector;
import edu.io.net.client.SocketConnector;
import edu.io.net.command.*;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class Main {
    static CountDownLatch handshakeLatch = new CountDownLatch(1);

    private static String clientId;
    private static String playerName;
    private static int boardSize = 0;
    private static String[][] boardGrid;
    private static boolean gameRunning = true;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Gold Rush Client (v1.2.6) ===");

        // tworzy GameServerConnector z SocketConnector
        var gsc = new GameServerConnector("tcp://localhost:1313", new SocketConnector());

        // połączenie
        gsc.connect();
        if (!gsc.isConnected()) {
            System.err.println("Błąd: Nie można połączyć się z serwerem!");
            return;
        }
        System.out.println("Połączono z serwerem.");

        // handshake
        gsc.issueCommand(new Handshake.Cmd("1.2.6"), (CommandRe res) -> {
            if (res instanceof Handshake.CmdRe cmdRe) {
                System.out.println("Handshake: " + cmdRe.status());
                handshakeLatch.countDown(); // sygnalizujemy zakonczenie handshake
                if (cmdRe.status() == Handshake.CmdRe.Status.OK) {
                    System.out.println("Handshake udany!");
                } else if (cmdRe.status() == Handshake.CmdRe.Status.LIB_VERSION_TOO_LOW) {
                    System.out.println("Wersja biblioteki zbyt niska");
                } else if (cmdRe.status() == Handshake.CmdRe.Status.LIB_VERSION_MALFORMED) {
                    System.out.println("Błędny format wersji");
                } else {
                    System.out.println("Nieznany status");
                }
            }
        });

        try {
            handshakeLatch.await(); // czekamy, az handshake sie skonczy
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // pyta o imię gracza
        System.out.print("\nPodaj imię gracza: ");
        var scanner = new Scanner(System.in);
        playerName = scanner.nextLine();

        // dołącza do gry
        CountDownLatch joinLatch = new CountDownLatch (1);

        gsc.issueCommand(new JoinGame.Cmd(playerName), (CommandRe res) -> {
            if (res instanceof JoinGame.CmdRe cmdRe) {
                System.out.println("JoinGame: " + cmdRe.status());
                if (cmdRe.status() == JoinGame.CmdRe.Status.OK) {
                    clientId = cmdRe.clientId;
                    System.out.println("Dołączono! ID: " + clientId);
                    System.out.println("Czekam na rozpoczęcie gry...");
                } else if (cmdRe.status() == JoinGame.CmdRe.Status.NAME_ALREADY_EXISTS) {
                    System.out.println("Nazwa zajęta");
                } else if (cmdRe.status() == JoinGame.CmdRe.Status.ALREADY_CONNECTED) {
                    System.out.println("Już połączony");
                } else {
                    System.out.println("Nieznany status");
                }
                joinLatch.countDown(); // sygnalizujemy zakonczenie join
            }
        });
        joinLatch.await(); // czekamy, az join sie zakonczy

        // odbieranie poleceń z serwera

        // teraz dwie metody. 1. dla poleceń od serwera (UpdateState, RequestMove)
        gsc.onCommandFromServer((Command cmd) -> {
            System.out.println("\n[Serwer →] " + cmd.getClass().getSimpleName());

            if (cmd instanceof UpdateState.Cmd) {
                return handleUpdateState((UpdateState.Cmd) cmd);
            } else if (cmd instanceof RequestMove.Cmd) {
                return handleRequestMove((RequestMove.Cmd) cmd, gsc);
            } else {
                System.out.println("Nieznane polecenie od serwera");
                return CommandAck.NO_ACK; // nie odpowiadamy:p
            }
        });

        // 2. dla odpowiedzi na nasze polecenia
        gsc.onResponseFromServer((CommandRe res) -> {
            System.out.println("[Odpowiedź] " + res.getClass().getSimpleName());
            if (res instanceof Echo.CmdRe) {
                Echo.CmdRe echo = (Echo.CmdRe) res;
                System.out.println("Echo: " + echo.msg);
            } else if (res instanceof GetInfo.CmdRe) {
                GetInfo.CmdRe getInfo = (GetInfo.CmdRe) res;
                System.out.println("Info: " + getInfo.info);
            } else if (res instanceof LeaveGame.CmdRe) {
                LeaveGame.CmdRe leave = (LeaveGame.CmdRe) res;
                System.out.println("Leave: " + leave.status());
            }
        });

        // główna pętla gry
        while (gameRunning && gsc.isConnected()) {
            System.out.println("\n=== MENU ===");
            System.out.println("1. Wyświetl planszę");
            System.out.println("2. Wyślij echo");
            System.out.println("3. Pobierz informacje");
            System.out.println("4. Opuść grę");
            System.out.println("5. Zakończ");
            System.out.print("Wybierz opcję: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> displayBoard();
                case "2" -> {
                    System.out.print("Wiadomość do echa: ");
                    String message = scanner.nextLine();
                    gsc.issueCommand(new Echo.Cmd(message), (CommandRe res) -> {
                        if (res instanceof Echo.CmdRe echo) {
                            System.out.println("Serwer odpowiedział: " + echo.msg);
                        }
                    });
                }
                case "3" -> gsc.issueCommand(new GetInfo.Cmd(), (CommandRe res) -> {
                    if (res instanceof GetInfo.CmdRe info) {
                        System.out.println("Informacje: " + info.info);
                    }
                });
                case "4" -> {
                    gsc.issueCommand(new LeaveGame.Cmd(), (CommandRe res) -> {
                        System.out.println("Opuściłeś grę");
                        gameRunning = false;
                    });
                }
                case "5" -> {
                    System.out.println("Kończę połączenie...");
                    gameRunning = false;
                }
                default -> System.out.println("Nieznana opcja");
            }
        }

        // rozłączenie
        gsc.disconnect();
        scanner.close();
        System.out.println("Rozłączono.");
    }

    // musimy zwrócić CommandAck
    private static CommandAck handleUpdateState(UpdateState.Cmd update) {
        // pobranie listy stanów
        int count = 0;
        for (GameState state : update) {
            count++;
            handleGameState(state);
        }
        System.out.println("UpdateState - elementów: " + count);

        // potwierdzenie dla serwera
        return new CommandAck(update, new UpdateState.CmdRe(UpdateState.CmdRe.Status.OK));
    }

    // serwer prosi nas o ruch
    private static CommandAck handleRequestMove(RequestMove.Cmd request, GameServerConnector gsc) {
        System.out.println("⚠ Serwer prosi o wykonanie ruchu!");
        System.out.println("Naciśnij Enter, aby kontynuować...");

        // TODO: logika wyboru ruchu przez gracza

        return new CommandAck(request, new RequestMove.CmdRe(RequestMove.CmdRe.Status.OK));
    }

    // pomocnicza obsługa UpdateState
    private static void handleGameState(GameState state) {
        switch (state) {
            case GameState.BoardInfo board -> {
                boardSize = board.size();
                boardGrid = new String[boardSize][boardSize];
                // Inicjalizuj wszystkie komórki jako puste, już jako str
                for (int i = 0; i < boardSize; i++) {
                    for (int j = 0; j < boardSize; j++) {
                        boardGrid[i][j] = "·";
                    }
                }
                System.out.println("Plansza: " + boardSize + "x" + boardSize);
            }
            case GameState.BoardSquareInfo square -> {
                int col = square.pos().col();
                int row = square.pos().row();
                String label = square.label();
                if (col >= 0 && col < boardSize && row >= 0 && row < boardSize) {
                    boardGrid[row][col] = label;
                }
            }
            case GameState.PlayerInfo player -> {
                System.out.println("Gracz " + player.name() +
                        ": złoto=" + player.gold() +
                        ", nawodnienie=" + player.hydration() + "%" +
                        ", narzędzia=" + player.tools());
            }
            case GameState.PlayerListInfo list -> {
                System.out.println("Lista graczy (" + list.players().size() + "):");
                for (int i = 0; i < list.players().size(); i++) {
                    var p = list.players().get(i);
                    var marker = (i == list.activePlayerIdx()) ? "→" : " ";
                    System.out.println("  " + marker + p.name() +
                            " @ [" + p.pos().col() + "," + p.pos().row() + "]");
                }
            }
            default -> System.out.println("Nieznany GameState: " + state.getClass().getSimpleName());
        }
    }

    // Wyświetlanie planszy
    private static void displayBoard() {
        if (boardSize == 0 || boardGrid == null) {
            System.out.println("Plansza jeszcze nie została załadowana.");
            return;
        }

        System.out.println("\n=== PLANSZA " + boardSize + "x" + boardSize + " ===");
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                String cell = boardGrid[row][col];
                // Jeśli komórka jest pusta, wyświetl kropkę
                if (cell == null || cell.isEmpty()) {
                    System.out.print("· ");
                } else {
                    System.out.print(cell + " ");
                }
            }
            System.out.println();
        }
    }
}

//o matko święta...