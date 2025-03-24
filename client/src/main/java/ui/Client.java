package ui;

public interface Client {
    State eval(String input);
    State help();

    default State respondToUnknownCmd(State state) {
        System.out.print("Unknown command. Enter a different command, or enter 'h' to see options.");
        return state;
    }
}
