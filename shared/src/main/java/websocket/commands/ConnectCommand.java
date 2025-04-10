package websocket.commands;

public class ConnectCommand extends UserGameCommand {
    private final ClientRole role;

    public ConnectCommand(String authToken, Integer gameID, ClientRole role) {
        super(CommandType.CONNECT, authToken, gameID);
        this.role = role;
    }

    public ClientRole getRole() {
        return role;
    }
}
