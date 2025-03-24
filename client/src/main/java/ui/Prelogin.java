package ui;

import server.ResponseException;
import server.ServerFacade;

import java.util.Scanner;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_COLOR_WHITE;

public class Prelogin implements Client {
    private final ServerFacade server;
    private final String serverUrl;

    public Prelogin(String url) {
        serverUrl = url;
        server = new ServerFacade(serverUrl);
    }

    @Override
    public String help() {
        return "To create an account: 'r' or 'register'" + "\n" +
                "To login: 'l' or 'login'" + "\n" +
                "To exit the application: 'q' or 'quit'" + "\n" +
                "To see available commands: 'h' or 'help'";
    }

    @Override
    public String eval(String input) {
        return "";
    }

    private String register() throws ResponseException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a username" + prompt());
        String username = scanner.nextLine();
        System.out.print("Enter a password" + prompt());
        String password = scanner.nextLine();
        System.out.print("Enter your email" + prompt());
        String email = scanner.nextLine();

        var res = server.register(username, password, email);
        System.out.printf("%s registered successfully!", username);

        var repl = beginPostLoginLoop(res.authToken());
        repl.run();

        return "";
    }

    private String login() throws ResponseException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a username" + prompt());
        String username = scanner.nextLine();
        System.out.print("Enter a password" + prompt());
        String password = scanner.nextLine();

        var res = server.login(username, password);
        System.out.printf("%s logged in successfully!", username);

        var repl = beginPostLoginLoop(res.authToken());
        repl.run();

        return "";
    }

    // quit cmd just returns "quit"

    private String prompt() {
        return SET_TEXT_COLOR_BLUE + " >>> " + SET_TEXT_COLOR_WHITE;
    }

    private Repl beginPostLoginLoop(String authToken) {
        var client = new Postlogin(serverUrl, authToken);
        return new Repl(client);
    }
}
