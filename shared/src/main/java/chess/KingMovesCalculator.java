package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        int curr_row = position.getRow();
        int curr_col = position.getColumn();
        ChessPosition new_position;

        // add move going up
        new_position = new ChessPosition(curr_row + 1, curr_col);
        addMove(board, position, new_position, moves);

        // add move going down
        new_position = new ChessPosition(curr_row - 1, curr_col);
        addMove(board, position, new_position, moves);

        // add move going right
        new_position = new ChessPosition(curr_row, curr_col + 1);
        addMove(board, position, new_position, moves);

        // add move going left
        new_position = new ChessPosition(curr_row, curr_col - 1);
        addMove(board, position, new_position, moves);

        // add move along the top-right diagonal
        new_position = new ChessPosition(curr_row + 1, curr_col + 1);
        addMove(board, position, new_position, moves);

        // add move along the bottom-right diagonal
        new_position = new ChessPosition(curr_row - 1, curr_col + 1);
        addMove(board, position, new_position, moves);

        // add move along the top-left diagonal
        new_position = new ChessPosition(curr_row + 1, curr_col - 1);
        addMove(board, position, new_position, moves);

        // add move along the bottom-left diagonal
        new_position = new ChessPosition(curr_row - 1, curr_col - 1);
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
