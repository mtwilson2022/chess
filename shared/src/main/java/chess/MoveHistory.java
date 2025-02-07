package chess;

import java.util.Deque;
import java.util.ArrayDeque;

public class MoveHistory {
    private Deque<ChessMove> moveHistory;

    public MoveHistory() {
        this.moveHistory = new ArrayDeque<>();
    }

    public ChessMove getLastMove() {
        return moveHistory.getLast();
    }

    public void addLastMove(ChessMove move) {
        this.moveHistory.addLast(move);
    }
}
