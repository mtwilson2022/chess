package ui;

import server.ResponseException;
import server.ServerFacade;

import java.util.Scanner;

import static ui.State.*;
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
    public State help() {
        System.out.print("""
                To create an account: 'r' or 'register'
                To login: 'l' or 'login'
                To exit the application: 'q' or 'quit'
                To see available commands: 'h' or 'help' \n""");
        return PRE_LOGIN;
    }

    @Override
    public State eval(String input) throws ResponseException {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";

        return switch (cmd) {
            case "help", "h" -> help();
            case "register", "r" -> register();
            case "login", "l" -> login();
            case "quit", "q" -> quit();
            default -> respondToUnknownCmd(PRE_LOGIN);
        };
    }

    private State register() throws ResponseException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a username" + prompt());
        String username = scanner.nextLine();
        if (username.isEmpty()) {
            username = null;
        }
        System.out.print("Enter a password" + prompt());
        String password = scanner.nextLine();
        if (password.isEmpty()) {
            password = null;
        }
        System.out.print("Enter your email" + prompt());
        String email = scanner.nextLine();
        if (email.isEmpty()) {
            email = null;
        }

        var res = server.register(username, password, email);
        System.out.printf("%s registered successfully!\n", username);

        var repl = beginPostLoginLoop(res.authToken());
        repl.run();

        return PRE_LOGIN;
    }

    private State login() throws ResponseException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a username" + prompt());
        String username = scanner.nextLine();
        System.out.print("Enter a password" + prompt());
        String password = scanner.nextLine();

        var res = server.login(username, password);
        System.out.printf("%s logged in successfully!\n", username);

        var repl = beginPostLoginLoop(res.authToken());
        repl.run();

        return PRE_LOGIN;
    }

    // quit cmd just returns "quit"
    private State quit() {
        return QUIT_APP;
    }

    private String prompt() {
        return SET_TEXT_COLOR_BLUE + " >>> " + SET_TEXT_COLOR_WHITE;
    }

    private Repl beginPostLoginLoop(String authToken) {
        var client = new Postlogin(serverUrl, authToken);
        return new Repl(client, POST_LOGIN);
    }
}
