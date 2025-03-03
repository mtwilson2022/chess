package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        // add moves along the top-right diagonal
        addMoves(board, position, moves, Direction.UP_RIGHT);
        // add moves along the bottom-right diagonal
        addMoves(board, position, moves, Direction.DOWN_RIGHT);
        // add moves along the top-left diagonal
        addMoves(board, position, moves, Direction.UP_LEFT);
        // add moves along the bottom-left diagonal
        addMoves(board, position, moves, Direction.DOWN_LEFT);

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
            if (dir == Direction.UP_RIGHT) {
                newPosition = new ChessPosition(currRow + numSquares, currCol + numSquares);
            } else if (dir == Direction.DOWN_RIGHT) {
                newPosition = new ChessPosition(currRow - numSquares, currCol + numSquares);
            } else if (dir == Direction.UP_LEFT) {
                newPosition = new ChessPosition(currRow + numSquares, currCol - numSquares);
            } else { // if (dir == Direction.DOWN_LEFT)
                newPosition = new ChessPosition(currRow - numSquares, currCol - numSquares);
            }
            var move = new ChessMove(position, newPosition, null);

            // check if it's a legal move
            canMove = addMoveIfLegal(move, board, moves, newPosition, movingPiece);

        }

    }
}
