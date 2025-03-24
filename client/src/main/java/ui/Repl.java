package ui;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final Client client;
    private final State state;

    public Repl(String url) {
        client = new Prelogin(url);
        state = State.PRE_LOGIN;
    }

    public Repl(Client client, State state) {
        this.client = client;
        this.state = state;
    }

    public void run() {
        System.out.println("Welcome to Chess! Register or login to play."); // change for different states
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        State result = state;

        while (result == state) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = client.eval(line);
            } catch (Exception e) {
                System.out.print(e.getMessage());
            }
        }
    }

    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_BLUE + ">>> " + SET_TEXT_COLOR_WHITE);
    }
}
