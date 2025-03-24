package ui;

import chess.ChessBoard;
import model.GameData;
import server.ResponseException;
import server.ServerFacade;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static ui.EscapeSequences.SET_TEXT_COLOR_YELLOW;
import static ui.EscapeSequences.SET_TEXT_COLOR_WHITE;

public class Postlogin implements Client {
    // Phase 6: (?) add a private final String serverURL and initialize in the constructor (for WebSocket)
    private final ServerFacade server;
    private final String authToken;
    private Map<Integer, Map<String, String>> gameInfo;

    public Postlogin(String url, String auth) {
        server = new ServerFacade(url);
        authToken = auth;
        gameInfo = new HashMap<>();
    }

    @Override
    public String help() {
        return "";
    }

    @Override
    public String eval(String input) {
        return "";
    }

    private String logout() throws ResponseException {
        server.logout(authToken);
        return "Logged out successfully."; // TODO: needs to exit the post-login state
    }

    private String createGame() throws ResponseException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("What would you like to name this game?" + prompt());
        String gameName = scanner.nextLine();
        int gameID = server.createGame(authToken, gameName); // TODO: what do I do with the gameID?
        return "";
    }

    private String listGames() throws ResponseException {
        GameData[] games = server.listGames(authToken);
        return "";
    }

    private String playGame() throws ResponseException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("What game do you want to join?" + prompt()); // TODO: they should enter the game number
        String gameName = scanner.nextLine();
        GameData[] games = server.listGames(authToken);
        int gameID = getGameID(gameName, games);
        if (gameID == 0) {
            return "No game with that name exists. Enter 'l' to see all games, or 'c' if you would like to create a new game.";
        } else {
            System.out.print("game");
        }
        String[][] gameBoard = ChessBoardPrinter.boardGenerator(new ChessBoard()); // change in phase 6

        System.out.print("Enter a color to play as " + prompt());
        String playerColor = scanner.nextLine();

        // phase 5: draw board from white/black's perspective
        if (playerColor.equalsIgnoreCase("white")) {
            ChessBoardPrinter.printBoardForWhite(System.out, gameBoard);
        } else if (playerColor.equalsIgnoreCase("black")) {
            ChessBoardPrinter.printBoardForBlack(System.out, gameBoard);
        } else {
            return "That player color does not exist. You may play as 'white' or 'black'.";
        }

        return "";
    }

    private String observeGame() throws ResponseException {
        // phase 5: draw board from White's perspective
        Scanner scanner = new Scanner(System.in);
        System.out.print("What game do you want to join?" + prompt());
        String gameName = scanner.nextLine();
        GameData[] games = server.listGames(authToken);
        int gameID = getGameID(gameName, games);
        if (gameID == 0) {
            return "No game with that name exists. Enter 'l' to see all games, or 'c' if you would like to create a new game.";
        }

        String[][] gameBoard = ChessBoardPrinter.boardGenerator(new ChessBoard()); // change in phase 6
        ChessBoardPrinter.printBoardForWhite(System.out, gameBoard);

        return "";
    }

    private int getGameID(String name, GameData[] games) {
        for (GameData game : games) {
            if (game.gameName().equals(name)) {
                return game.gameID();
            }
        }
        return 0;
    }

    private String prompt() {
        return SET_TEXT_COLOR_YELLOW + " >>> " + SET_TEXT_COLOR_WHITE;
    }
}
