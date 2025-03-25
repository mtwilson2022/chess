package ui;

import server.ResponseException;

public interface Client {
    State eval(String input) throws ResponseException;
    State help();

    default State respondToUnknownCmd(State state) {
        System.out.print("Unknown command. Enter a different command, or enter 'h' to see options.\n");
        return state;
    }
}
