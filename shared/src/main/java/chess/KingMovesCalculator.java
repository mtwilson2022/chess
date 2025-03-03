package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        int currRow = position.getRow();
        int currCol = position.getColumn();
        ChessPosition newPosition;

        // add move going up
        newPosition = new ChessPosition(currRow + 1, currCol);
        addMove(board, position, newPosition, moves);

        // add move going down
        newPosition = new ChessPosition(currRow - 1, currCol);
        addMove(board, position, newPosition, moves);

        // add move going right
        newPosition = new ChessPosition(currRow, currCol + 1);
        addMove(board, position, newPosition, moves);

        // add move going left
        newPosition = new ChessPosition(currRow, currCol - 1);
        addMove(board, position, newPosition, moves);

        // add move along the top-right diagonal
        newPosition = new ChessPosition(currRow + 1, currCol + 1);
        addMove(board, position, newPosition, moves);

        // add move along the bottom-right diagonal
        newPosition = new ChessPosition(currRow - 1, currCol + 1);
        addMove(board, position, newPosition, moves);

        // add move along the top-left diagonal
        newPosition = new ChessPosition(currRow + 1, currCol - 1);
        addMove(board, position, newPosition, moves);

        // add move along the bottom-left diagonal
        newPosition = new ChessPosition(currRow - 1, currCol - 1);
        addMove(board, position, newPosition, moves);

        return moves;
    }
}
