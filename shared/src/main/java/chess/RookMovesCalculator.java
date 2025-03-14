package chess;

import java.util.Collection;
import java.util.ArrayList;

public class RookMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        // add moves going up
        addMoves(board, position, moves, Direction.UP);
        // add moves going right
        addMoves(board, position, moves, Direction.RIGHT);
        // add moves going down
        addMoves(board, position, moves, Direction.DOWN);
        // add moves going left
        addMoves(board, position, moves, Direction.LEFT);

        return moves;
    }

    private void addMoves(ChessBoard board, ChessPosition position,
                         Collection<ChessMove> moves, PieceMovesCalculator.Direction dir) {

        ChessPiece movingPiece = board.getPiece(position); // the piece you are trying to move
        int currRow = position.getRow();
        int currCol = position.getColumn();

        int numSquares = 0; // how many squares to move in a certain direction
        boolean canMove = true;
        while (canMove) {

            // set up the potential move
            numSquares++;
            ChessPosition newPosition;
            if (dir == Direction.UP) {
                newPosition = new ChessPosition(currRow + numSquares, currCol);
            } else if (dir == Direction.RIGHT) {
                newPosition = new ChessPosition(currRow, currCol + numSquares);
            } else if (dir == Direction.DOWN) {
                newPosition = new ChessPosition(currRow - numSquares, currCol);
            } else { // if (dir == Direction.LEFT)
                newPosition = new ChessPosition(currRow, currCol - numSquares);
            }
            var move = new ChessMove(position, newPosition, null);

            // check if it's a legal move
            canMove = addMoveIfLegal(move, board, moves, newPosition, movingPiece);
        }

    }
}
