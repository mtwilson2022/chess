package ui;

public interface Client {
    String eval(String input);
    String help();

    default String respondToUnknownCmd() {
        return "Unknown command. Enter a different command, or enter 'h' to see options.";
    }
}
