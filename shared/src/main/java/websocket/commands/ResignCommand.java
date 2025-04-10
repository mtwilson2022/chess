package websocket.commands;

public class ResignCommand extends UserGameCommand {
    private final ClientRole role;

    public ResignCommand(String authToken, Integer gameID, ClientRole role) {
        super(CommandType.RESIGN, authToken, gameID);
        this.role = role;
    }

    public ClientRole getRole() {
        return role;
    }
}
