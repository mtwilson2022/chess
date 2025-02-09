package chess;

import java.util.Deque;
import java.util.ArrayDeque;

public class MoveHistory {
    private Deque<ChessMove> moveHistory;

    public MoveHistory() {
        this.moveHistory = new ArrayDeque<>();
    }

    /**
     * Gets all the in-game moves
     * @return a deque of all the moves made in the game
     */
    public Deque<ChessMove> getMoveHistory() {
        return this.moveHistory;
    }

    public ChessMove getLastMove() {
        return moveHistory.getLast();
    }

    public void addLastMove(ChessMove move) {
        this.moveHistory.addLast(move);
    }
}
