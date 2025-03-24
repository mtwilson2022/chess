package ui;

import chess.ChessBoard;
import model.GameData;
import server.ResponseException;
import server.ServerFacade;

import java.util.*;

import static ui.EscapeSequences.SET_TEXT_COLOR_YELLOW;
import static ui.EscapeSequences.SET_TEXT_COLOR_WHITE;
import static ui.State.*;

public class Postlogin implements Client {
    // Phase 6: (?) add a private final String serverURL and initialize in the constructor (for WebSocket)
    private final ServerFacade server;
    private final String authToken;
    private Map<Integer, List<String>> gamesInfo;

    public Postlogin(String url, String auth) {
        server = new ServerFacade(url);
        authToken = auth;
        gamesInfo = new HashMap<>();
    }

    @Override
    public State help() {
        return POST_LOGIN;
    }

    @Override
    public State eval(String input) {
        return null;
    } // default: respond to unknown cmd

    private State logout() throws ResponseException {
        server.logout(authToken);
        System.out.print("Logged out successfully.");
        return PRE_LOGIN;
    }

    private State createGame() throws ResponseException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("What would you like to name this game?" + prompt());
        String gameName = scanner.nextLine();
        server.createGame(authToken, gameName);
        return POST_LOGIN;
    }

    private State listGames() throws ResponseException {
        makeList();
        for (int num : gamesInfo.keySet()) {
            printGameInfo(num);
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
        String black = game.blackUsername();
        var names = List.of(name, white, black);
        gamesInfo.put(num, names);
    }

    private void printGameInfo(int gameNumber) {
        var gameInfo = gamesInfo.get(gameNumber);
        System.out.printf("Game %d  --  Name: %s  |  White player: %s  |  Black player: %s",
                gameNumber, gameInfo.get(0), gameInfo.get(1), gameInfo.get(2));
        System.out.println();
    }

    private State playGame() throws ResponseException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of the game you want to join" + prompt()); // TODO: they should enter the game number
        String gameName = scanner.nextLine();
        GameData[] games = server.listGames(authToken);
        int gameID = getGameID(gameName, games);
        if (gameID == 0) {
            System.out.print("No game with that name exists. Enter 'l' to see all games, or 'c' if you would like to create a new game.");
            return POST_LOGIN;
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
            System.out.print("That player color does not exist. You may play as 'white' or 'black'.");
            return POST_LOGIN;
        }

        return POST_LOGIN;
    }

    private State observeGame() throws ResponseException {
        // phase 5: draw board from White's perspective
        Scanner scanner = new Scanner(System.in);
        System.out.print("What game do you want to join?" + prompt());
        String gameName = scanner.nextLine();
        GameData[] games = server.listGames(authToken);
        int gameID = getGameID(gameName, games);
        if (gameID == 0) {
            System.out.print("No game with that name exists. Enter 'l' to see all games, or 'c' if you would like to create a new game.");
        } else {
            String[][] gameBoard = ChessBoardPrinter.boardGenerator(new ChessBoard()); // change in phase 6
            ChessBoardPrinter.printBoardForWhite(System.out, gameBoard);
        }

        return POST_LOGIN;
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
