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

        ChessPiece moving_piece = board.getPiece(position); // the piece you are trying to move
        int curr_row = position.getRow();
        int curr_col = position.getColumn();

        int num_squares = 0; // how many squares to move in a certain direction
        while (true) {

            // set up the potential move
            num_squares++;
            ChessPosition new_position;
            if (dir == Direction.UP_RIGHT) {
                new_position = new ChessPosition(curr_row + num_squares, curr_col + num_squares);
            } else if (dir == Direction.DOWN_RIGHT) {
                new_position = new ChessPosition(curr_row - num_squares, curr_col + num_squares);
            } else if (dir == Direction.UP_LEFT) {
                new_position = new ChessPosition(curr_row + num_squares, curr_col - num_squares);
            } else { // if (dir == Direction.DOWN_LEFT)
                new_position = new ChessPosition(curr_row - num_squares, curr_col - num_squares);
            }
            var move = new ChessMove(position, new_position, null);

            // check if it's a legal move
            if (outOfBounds(move)) {
                break;
            }
            else if (isOccupiedSquare(board, move)) { // if move square is occupied
                // if the move square has a same-color piece, can't go there
                if (board.getPiece(new_position).getTeamColor() == moving_piece.getTeamColor()) {
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
