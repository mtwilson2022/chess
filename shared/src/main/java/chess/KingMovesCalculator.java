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

    public static Collection<ChessMove> addCastlingMoves(ChessBoard board, MoveHistory history, ChessGame.TeamColor color, ChessGame game) {
        var moves = new ArrayList<ChessMove>();
        int king_row;
        if (color == ChessGame.TeamColor.WHITE) {
            king_row = 1;
        } else {
            king_row = 8;
        }
        var king_start_pos = new ChessPosition(king_row, 5);

        if (canCastleKingside(board, history, color, game)) {
            var castleRight = new ChessMove(king_start_pos, new ChessPosition(king_row, 7), null);
            moves.add(castleRight);
        }
        if (canCastleQueenside(board, history, color, game)) {
            var castleLeft = new ChessMove(king_start_pos, new ChessPosition(king_row, 3), null);
            moves.add(castleLeft);
        }
        return moves;
    }

    private static boolean canCastleKingside(ChessBoard board, MoveHistory history, ChessGame.TeamColor color, ChessGame game) {
        int king_row;
        if (color == ChessGame.TeamColor.WHITE) {
            king_row = 1;
        } else {
            king_row = 8;
        }
        var king_start_pos = new ChessPosition(king_row, 5);
        var rook_start_pos = new ChessPosition(king_row, 8);

        // check that neither the king nor the rook has moved
        for (ChessMove move : history.getMoveHistory()) {
            if (move.getStartPosition().equals(king_start_pos)) { // if king has moved
                return false;
            }
            if (move.getStartPosition().equals(rook_start_pos)) { // if kingside rook has moved
                return false;
            }
        }

        // check that there are no pieces between the rook and the king
        if (board.getPiece(new ChessPosition(king_row, 6)) != null
                || board.getPiece(new ChessPosition(king_row, 7)) != null) {
            return false;
        }

        // make sure the king is never in check
        if (game.isInCheck(color)) {
            return false;
        }
        if (!game.isValidMove(new ChessMove(king_start_pos, new ChessPosition(king_row, 6), null), color)) {
            return false;
        }
        if (!game.isValidMove(new ChessMove(king_start_pos, new ChessPosition(king_row, 7), null), color)) {
            return false;
        }

        return true;
    }

    private static boolean canCastleQueenside(ChessBoard board, MoveHistory history, ChessGame.TeamColor color, ChessGame game) {
        int king_row;
        if (color == ChessGame.TeamColor.WHITE) {
            king_row = 1;
        } else {
            king_row = 8;
        }
        var king_start_pos = new ChessPosition(king_row, 5);
        var rook_start_pos = new ChessPosition(king_row, 1);

        // check that neither the king nor the rook has moved
        for (ChessMove move : history.getMoveHistory()) {
            if (move.getStartPosition().equals(king_start_pos)) { // if king has moved
                return false;
            }
            if (move.getStartPosition().equals(rook_start_pos)) { // if kingside rook has moved
                return false;
            }
        }

        // check that there are no pieces between the rook and the king
        if (board.getPiece(new ChessPosition(king_row, 4)) != null
                || board.getPiece(new ChessPosition(king_row, 3)) != null
                || board.getPiece(new ChessPosition(king_row, 2)) != null) {
            return false;
        }

        // make sure the king is never in check
        if (game.isInCheck(color)) {
            return false;
        }
        if (!game.isValidMove(new ChessMove(king_start_pos, new ChessPosition(king_row, 4), null), color)) {
            return false;
        }
        if (!game.isValidMove(new ChessMove(king_start_pos, new ChessPosition(king_row, 3), null), color)) {
            return false;
        }
        if (!game.isValidMove(new ChessMove(king_start_pos, new ChessPosition(king_row, 2), null), color)) {
            return false;
        }

        return true;
    }

}
