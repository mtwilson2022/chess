package server.websocket;

public class IllegalCommandException extends RuntimeException {
    public IllegalCommandException(String message) {
        super(message);
    }
}
