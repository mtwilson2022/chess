package websocket.messages;

public class NotificationMessage extends ServerMessage {

    public NotificationMessage() {
        super(ServerMessageType.NOTIFICATION);
    }
}
