package websocket.messages;

public class ErrorMessage extends ServerMessage {

    public ErrorMessage() {
        super(ServerMessageType.ERROR);
    }
}
