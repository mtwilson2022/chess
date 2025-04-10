package websocket.commands;

public class LeaveCommand extends UserGameCommand {
    private final ClientRole role;

    public LeaveCommand(String authToken, Integer gameID, ClientRole role) {
        super(CommandType.LEAVE, authToken, gameID);
        this.role = role;
    }

    public ClientRole getRole() {
        return role;
    }
}
