package chess;

import java.util.Deque;
import java.util.ArrayDeque;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MoveHistory that = (MoveHistory) o;
        return Objects.equals(moveHistory, that.moveHistory);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(moveHistory);
    }

    @Override
    public String toString() {
        return "MoveHistory{" +
                "moveHistory=" + moveHistory +
                '}';
    }
}
