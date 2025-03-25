import ui.Repl;

public class Main {
    public static void main(String[] args) {
        String url = "http://localhost:8080";
        var repl = new Repl(url);
        repl.run();
    }
}