package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        int curr_row = position.getRow();
        int curr_col = position.getColumn();
        ChessPosition new_position;

        // add move going up-up-right
        new_position = new ChessPosition(curr_row + 2, curr_col + 1);
        addMove(board, position, new_position, moves);

        // add move going up-right-right
        new_position = new ChessPosition(curr_row + 1, curr_col + 2);
        addMove(board, position, new_position, moves);

        // add move going down-right-right
        new_position = new ChessPosition(curr_row - 1, curr_col + 2);
        addMove(board, position, new_position, moves);

        // add move going down-down-right
        new_position = new ChessPosition(curr_row - 2, curr_col + 1);
        addMove(board, position, new_position, moves);

        // add move going down-down-left
        new_position = new ChessPosition(curr_row - 2, curr_col - 1);
        addMove(board, position, new_position, moves);

        // add move going down-left-left
        new_position = new ChessPosition(curr_row - 1, curr_col - 2);
        addMove(board, position, new_position, moves);

        // add move going up-left-left
        new_position = new ChessPosition(curr_row + 1, curr_col - 2);
        addMove(board, position, new_position, moves);

        // add move going up-up-left
        new_position = new ChessPosition(curr_row + 2, curr_col - 1);
        addMove(board, position, new_position, moves);

        return moves;
    }

    private void addMove(ChessBoard board, ChessPosition position,
                         ChessPosition new_position, Collection<ChessMove> moves) {

        ChessPiece moving_piece = board.getPiece(position);
        var move = new ChessMove(position, new_position, null);

        // if the desired move is valid, add it to the moves list
        if (!outOfBounds(move)) {
            if (board.getPiece(new_position) == null) {
                moves.add(move);
            } else if (board.getPiece(new_position).getTeamColor() != moving_piece.getTeamColor()) {
                moves.add(move);
            }
        }
    }

}

