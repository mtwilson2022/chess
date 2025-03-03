package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        // Queen moves are a combination of rook moves and bishop moves
        // ROOK MOVES
        // add moves going up
        addMoves(board, position, moves, Direction.UP);
        // add moves going down
        addMoves(board, position, moves, Direction.DOWN);
        // add moves going right
        addMoves(board, position, moves, Direction.RIGHT);
        // add moves going left
        addMoves(board, position, moves, Direction.LEFT);

        // BISHOP MOVES
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
        while (true) {

            // set up the potential move
            numSquares++;
            ChessPosition newPosition;

            // Rook movement
            if (dir == Direction.UP) {
                newPosition = new ChessPosition(currRow + numSquares, currCol);
            } else if (dir == Direction.DOWN) {
                newPosition = new ChessPosition(currRow - numSquares, currCol);
            } else if (dir == Direction.RIGHT) {
                newPosition = new ChessPosition(currRow, currCol + numSquares);
            } else if (dir == Direction.LEFT) {
                newPosition = new ChessPosition(currRow, currCol - numSquares);
            }

            // Bishop movement
            else if (dir == Direction.UP_RIGHT) {
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
            if (outOfBounds(move)) {
                break;
            }
            else if (isOccupiedSquare(board, move)) { // if move square is occupied
                // if the move square has a same-color piece, can't go there
                if (board.getPiece(newPosition).getTeamColor() == movingPiece.getTeamColor()) {
                    break;
                } else { // if the move square has opposite-color piece, add move then stop
                    moves.add(move);
                    break;
                }
            }
            else {
                moves.add(move);
            }
        }

    }
}
