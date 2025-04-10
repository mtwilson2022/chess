package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {
    private final ChessMove move;
    private final ClientRole role;

    public MakeMoveCommand(String authToken, Integer gameID, ChessMove move, ClientRole role) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.move = move;
        this.role = role;
    }

    public ChessMove getMove() {
        return move;
    }

    public ClientRole getRole() {
        return role;
    }
}
