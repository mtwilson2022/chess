package chess;

import java.util.Collection;
import java.util.ArrayList;

public class PawnMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece pawn = board.getPiece(position);

        if (pawn.getTeamColor() == ChessGame.TeamColor.WHITE) {
            pawnMovesWhite(board, position, moves);
        } else { // if pawn.getTeamColor() == ChessGame.TeamColor.BLACK
            pawnMovesBlack(board, position, moves);
        }
        return moves;
    }

    /*
    White pawns can only move in the direction of increasing rows.
    They can move two squares forward if on the 2nd rank.
    They promote only if they are moving to rank 8.
     */
    private void pawnMovesWhite(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        int curr_row = position.getRow();
        int curr_col = position.getColumn();

        // forward motion
        var forward_square = new ChessPosition(curr_row + 1, curr_col);
        var move = new ChessMove(position, forward_square, null); // default move (without promotion)

        if (!outOfBounds(move) && !isOccupiedSquare(board, move)) {
            // if moving to promotion square, add moves with promotion
            if (forward_square.getRow() == 8) {
                addPromoMoves(position, forward_square, moves);
            } else {
                moves.add(move);
                // conditions for moving 2 squares forward
                if (curr_row == 2) {
                    var second_forward_square = new ChessPosition(curr_row + 2, curr_col);
                    var second_move = new ChessMove(position, second_forward_square, null);
                    if (!outOfBounds(second_move) && !isOccupiedSquare(board, second_move)) moves.add(second_move);
                }
            }
        }

        // diagonal attacking motion (to the right)
        var right_square = new ChessPosition(curr_row + 1, curr_col + 1);
        var move_right = new ChessMove(position, right_square, null);

        if (!outOfBounds(move_right) && isOccupiedSquare(board, move_right)) {
            if (board.getPiece(right_square).getTeamColor() == ChessGame.TeamColor.BLACK) {
                // if moving to promotion square, add moves with promotion
                if (right_square.getRow() == 8) {
                    addPromoMoves(position, right_square, moves);
                } else {
                    moves.add(move_right);
                }
            }
        }

        // diagonal attacking motion (to the left)
        var left_square = new ChessPosition(curr_row + 1, curr_col - 1);
        var move_left = new ChessMove(position, left_square, null);

        if (!outOfBounds(move_left) && isOccupiedSquare(board, move_left)) {
            if (board.getPiece(left_square).getTeamColor() == ChessGame.TeamColor.BLACK) {
                // if moving to promotion square, add moves with promotion
                if (left_square.getRow() == 8) {
                    addPromoMoves(position, left_square, moves);
                } else {
                    moves.add(move_left);
                }
            }
        }
    }

    /*
    Black pawns can only move in the direction of decreasing rows.
    They can move two squares forward if on the 7th rank.
    They promote only if they are moving to rank 1.
     */
    private void pawnMovesBlack(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        int curr_row = position.getRow();
        int curr_col = position.getColumn();

        // forward motion
        var forward_square = new ChessPosition(curr_row - 1, curr_col);
        var move = new ChessMove(position, forward_square, null); // default move (without promotion)

        if (!outOfBounds(move) && !isOccupiedSquare(board, move)) {
            // if moving to promotion square, add moves with promotion
            if (forward_square.getRow() == 1) {
                addPromoMoves(position, forward_square, moves);
            } else {
                moves.add(move);
                // conditions for moving 2 squares forward
                if (curr_row == 7) {
                    var second_forward_square = new ChessPosition(curr_row - 2, curr_col);
                    var second_move = new ChessMove(position, second_forward_square, null);
                    if (!outOfBounds(second_move) && !isOccupiedSquare(board, second_move)) moves.add(second_move);
                }
            }
        }

        // diagonal attacking motion (to the right)
        var right_square = new ChessPosition(curr_row - 1, curr_col + 1);
        var move_right = new ChessMove(position, right_square, null);

        if (!outOfBounds(move_right) && isOccupiedSquare(board, move_right)) {
            if (board.getPiece(right_square).getTeamColor() == ChessGame.TeamColor.WHITE) {
                // if moving to promotion square, add moves with promotion
                if (right_square.getRow() == 1) {
                    addPromoMoves(position, right_square, moves);
                } else {
                    moves.add(move_right);
                }
            }
        }

        // diagonal attacking motion (to the left)
        var left_square = new ChessPosition(curr_row - 1, curr_col - 1);
        var move_left = new ChessMove(position, left_square, null);

        if (!outOfBounds(move_left) && isOccupiedSquare(board, move_left)) {
            if (board.getPiece(left_square).getTeamColor() == ChessGame.TeamColor.WHITE) {
                // if moving to promotion square, add moves with promotion
                if (left_square.getRow() == 1) {
                    addPromoMoves(position, left_square, moves);
                } else {
                    moves.add(move_left);
                }
            }
        }
    }

    /*
    This method is called only when a pawn reaches a promotion square and adds all possible promotion moves.
     */
    private void addPromoMoves(ChessPosition start_pos, ChessPosition end_pos, Collection<ChessMove> moves) {
        moves.add(new ChessMove(start_pos, end_pos, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(start_pos, end_pos, ChessPiece.PieceType.KNIGHT));
        moves.add(new ChessMove(start_pos, end_pos, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(start_pos, end_pos, ChessPiece.PieceType.QUEEN));
    }
}
