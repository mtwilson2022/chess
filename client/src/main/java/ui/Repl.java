package ui;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final Client client;

    public Repl(String url) {
        client = new Prelogin(url);
    }

    public Repl(Client client) {
        this.client = client;
    }

    public void run() {
        System.out.println("Welcome to Chess! Register or login to play.");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        String result = "";

        // change the condition to account for logging out or exiting a game
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = client.eval(line);
                System.out.print(result);
            } catch (Exception e) {
                System.out.print(e.getMessage());
            }
        }
    }

    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_BLUE + ">>> " + SET_TEXT_COLOR_WHITE);
    }
}
