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
        printWelcome();
        client.help();

        Scanner scanner = new Scanner(System.in);
        State result = state;

        while (result == state) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = client.eval(line);
            } catch (Exception e) {
                System.out.print(e.getMessage());
                System.out.println();
            }
        }
    }

    private void printPrompt() {
        if (state == State.PRE_LOGIN) {
            System.out.print(RESET_BG_COLOR + SET_TEXT_COLOR_BLUE + ">>> " + SET_TEXT_COLOR_WHITE);
        } else if (state == State.POST_LOGIN) {
            System.out.print(RESET_BG_COLOR + SET_TEXT_COLOR_YELLOW + ">>> " + SET_TEXT_COLOR_WHITE);
        } else if (state == State.GAMEPLAY) {
            System.out.print(RESET_BG_COLOR + SET_TEXT_COLOR_GREEN + ">>> " + SET_TEXT_COLOR_WHITE);
        }
    }

    private void printWelcome() {
        if (state == State.PRE_LOGIN) {
            System.out.println(SET_TEXT_COLOR_BLUE + "Welcome to Chess! Register or login to play." + SET_TEXT_COLOR_WHITE);
        } else if (state == State.POST_LOGIN) {
            System.out.println(SET_TEXT_COLOR_YELLOW + "Welcome to the Game Center." + SET_TEXT_COLOR_WHITE);
        }
    }
}
