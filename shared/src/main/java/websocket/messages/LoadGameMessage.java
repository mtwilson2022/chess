package websocket.messages;

public class LoadGameMessage extends ServerMessage {

    public LoadGameMessage() {
        super(ServerMessageType.LOAD_GAME);
    }
}
