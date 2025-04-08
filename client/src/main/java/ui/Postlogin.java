package ui;

import chess.ChessBoard;
import model.GameData;
import server.ResponseException;
import server.ServerFacade;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static ui.EscapeSequences.*;
import static ui.State.*;

public class Postlogin implements Client {
    // Phase 6: (?) add a private final String serverURL and initialize in the constructor (for WebSocket)
    private final ServerFacade server;
    private final String authToken;
    private final Map<Integer, List<String>> gamesInfo;
    private final Map<Integer, Integer> gameIDs;

    public Postlogin(String url, String auth) {
        server = new ServerFacade(url);
        authToken = auth;
        gamesInfo = new HashMap<>();
        gameIDs = new HashMap<>();
    }

    @Override
    public State help() {
        System.out.print("""
                To see all games: "l" or "list"
                To make a new game: "c" or "create"
                To join a game: "p" or "play"
                To observe a game: "o" or "observe"
                To logout: 'x' or 'logout'
                To see available commands: 'h' or 'help' \n""");
        return POST_LOGIN;
    }

    @Override
    public State eval(String input) throws ResponseException {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";

        return switch (cmd) {
            case "help", "h" -> help();
            case "logout", "x" -> logout();
            case "list", "l" -> listGames();
            case "create", "c" -> createGame();
            case "play", "p" -> playGame();
            case "observe", "o" -> observeGame();
            default -> respondToUnknownCmd(POST_LOGIN);
        };
    }

    private State logout() throws ResponseException {
        server.logout(authToken);
        System.out.print("Logged out successfully.\n");
        return PRE_LOGIN;
    }

    private State createGame() throws ResponseException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("What would you like to name this game?" + prompt());
        String gameName = scanner.nextLine();
        if (gameName.isEmpty()) {
            gameName = null;
        }
        server.createGame(authToken, gameName);
        return POST_LOGIN;
    }

    private State listGames() throws ResponseException {
        makeList();
        for (int num : gamesInfo.keySet()) {
            printGameInfo(num);
        }
        if (gamesInfo.isEmpty()) {
            System.out.print("There are currently no games.\n");
        }
        return POST_LOGIN;
    }

    private void makeList() throws ResponseException {
        gamesInfo.clear();
        GameData[] games = server.listGames(authToken);
        int i = 0;
        for (GameData game : games) {
            i++;
            processGame(game, i);
        }
    }

    private void processGame(GameData game, Integer num) {
        String name = game.gameName();
        String white = game.whiteUsername();
        if (white == null) {
            white = SET_TEXT_COLOR_BLUE + "<available>" + SET_TEXT_COLOR_WHITE;
        }
        String black = game.blackUsername();
        if (black == null) {
            black = SET_TEXT_COLOR_BLUE + "<available>" + SET_TEXT_COLOR_WHITE;
        }
        int id = game.gameID();
        var names = List.of(name, white, black);
        gamesInfo.put(num, names);
        gameIDs.put(num, id);
    }

    private void printGameInfo(int gameNumber) {
        var gameInfo = gamesInfo.get(gameNumber);
        System.out.printf("Game %d  --  Name: %s  |  White player: %s  |  Black player: %s",
                gameNumber, gameInfo.get(0), gameInfo.get(1), gameInfo.get(2));
        System.out.println();
    }

    private State playGame() throws ResponseException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of the game you want to join" + prompt());
        int gameNum = getGameNum(scanner);
        if (gameNum == 0) {
            System.out.print("Invalid input. Please enter a number for the game you want to join.\n");
            return POST_LOGIN;
        }

        var gameInfo = gamesInfo.get(gameNum);
        if (gameInfo == null) {
            System.out.print("No game with that number exists. Enter 'l' to see a list of games and their numbers.\n");
            return POST_LOGIN;
        }

        ChessBoard board = new ChessBoard();
        board.resetBoard();
        String[][] gameBoard = ChessBoardPrinter.boardGenerator(board); // change in phase 6

        System.out.print("Enter a color to play as (either 'white' or 'black')" + prompt());
        String playerColor = scanner.nextLine();

        server.joinGame(authToken, playerColor, gameIDs.get(gameNum));

        // phase 5: draw board from white/black's perspective
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        if (playerColor.equalsIgnoreCase("white")) {
            ChessBoardPrinter.printBoardForWhite(out, gameBoard);
        } else if (playerColor.equalsIgnoreCase("black")) {
            ChessBoardPrinter.printBoardForBlack(out, gameBoard);
        } else {
            System.out.print("That player color does not exist. You may play as 'white' or 'black'.\n");
            return POST_LOGIN;
        }

        return POST_LOGIN;
    }

    private State observeGame() { // throws ResponseException
        // phase 5: draw board from White's perspective
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of the game you want to observe" + prompt());
        int gameNum = getGameNum(scanner);
        if (gameNum == 0) {
            System.out.print("Please enter a number for the game you want to join.");
            return POST_LOGIN;
        }

        var gameInfo = gamesInfo.get(gameNum);
        if (gameInfo == null) {
            System.out.print("No game with that number exists. Enter 'l' to see a list of games and their numbers.\n");
        } else {
            PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
            ChessBoard board = new ChessBoard();
            board.resetBoard();
            String[][] gameBoard = ChessBoardPrinter.boardGenerator(board); // change in phase 6
            ChessBoardPrinter.printBoardForWhite(out, gameBoard);
        }

        return POST_LOGIN;
    }

    private int getGameNum(Scanner scanner) {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String prompt() {
        return SET_TEXT_COLOR_YELLOW + " >>> " + SET_TEXT_COLOR_WHITE;
    }
}
