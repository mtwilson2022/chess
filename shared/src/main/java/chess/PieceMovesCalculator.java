package chess;

import java.util.Collection;

public interface PieceMovesCalculator {

    enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        UP_LEFT,
        UP_RIGHT,
        DOWN_LEFT,
        DOWN_RIGHT
    }

    /**
     * Finds every legal move that a piece can make.
     * @return Collection<ChessMove>
     */
    Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position);

    default boolean isOccupiedSquare(ChessBoard board, ChessMove move) {
        ChessPosition end = move.getEndPosition();
        return board.getPiece(end) != null;
    }

    /**
     * @param move a potential move for a piece.
     * @return true if the move would leave the boundaries of the board, or false otherwise.
     */
    default boolean outOfBounds(ChessMove move) {
        ChessPosition end = move.getEndPosition();
        return end.getRow() < 1 || end.getRow() > 8 || end.getColumn() < 1 || end.getColumn() > 8;
    }

    default void addMove(ChessBoard board, ChessPosition position,
                         ChessPosition newPosition, Collection<ChessMove> moves) {

        ChessPiece movingPiece = board.getPiece(position);
        var move = new ChessMove(position, newPosition, null);

        // if the desired move is valid, add it to the moves list
        if (!outOfBounds(move)) {
            if (board.getPiece(newPosition) == null) {
                moves.add(move);
            } else if (board.getPiece(newPosition).getTeamColor() != movingPiece.getTeamColor()) {
                moves.add(move);
            }
        }
    }

    default boolean addMoveIfLegal(ChessMove move, ChessBoard board, Collection<ChessMove> moves,
                                   ChessPosition newPosition, ChessPiece movingPiece) {
        if (outOfBounds(move)) {
            return false;
        }
        else if (isOccupiedSquare(board, move)) { // if move square is occupied
            // if the move square has a same-color piece, can't go there
            if (board.getPiece(newPosition).getTeamColor() != movingPiece.getTeamColor()) {
                moves.add(move); // if the move square has opposite-color piece, add move then stop
            }
            return false;
        }
        else {
            moves.add(move);
            return true;
        }
    }
}
