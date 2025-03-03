package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        int currRow = position.getRow();
        int currCol = position.getColumn();
        ChessPosition newPosition;

        // add move going up-up-right
        newPosition = new ChessPosition(currRow + 2, currCol + 1);
        addMove(board, position, newPosition, moves);

        // add move going up-right-right
        newPosition = new ChessPosition(currRow + 1, currCol + 2);
        addMove(board, position, newPosition, moves);

        // add move going down-right-right
        newPosition = new ChessPosition(currRow - 1, currCol + 2);
        addMove(board, position, newPosition, moves);

        // add move going down-down-right
        newPosition = new ChessPosition(currRow - 2, currCol + 1);
        addMove(board, position, newPosition, moves);

        // add move going down-down-left
        newPosition = new ChessPosition(currRow - 2, currCol - 1);
        addMove(board, position, newPosition, moves);

        // add move going down-left-left
        newPosition = new ChessPosition(currRow - 1, currCol - 2);
        addMove(board, position, newPosition, moves);

        // add move going up-left-left
        newPosition = new ChessPosition(currRow + 1, currCol - 2);
        addMove(board, position, newPosition, moves);

        // add move going up-up-left
        newPosition = new ChessPosition(currRow + 2, currCol - 1);
        addMove(board, position, newPosition, moves);

        return moves;
    }
}

