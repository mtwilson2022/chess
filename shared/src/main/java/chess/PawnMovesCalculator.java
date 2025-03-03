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
        int currRow = position.getRow();
        int currCol = position.getColumn();

        // forward motion
        var forwardSquare = new ChessPosition(currRow + 1, currCol);
        var move = new ChessMove(position, forwardSquare, null); // default move (without promotion)

        if (!outOfBounds(move) && !isOccupiedSquare(board, move)) {
            // if moving to promotion square, add moves with promotion
            if (forwardSquare.getRow() == 8) {
                addPromoMoves(position, forwardSquare, moves);
            } else {
                moves.add(move);
                // conditions for moving 2 squares forward
                if (currRow == 2) {
                    var secondForwardSquare = new ChessPosition(currRow + 2, currCol);
                    var secondMove = new ChessMove(position, secondForwardSquare, null);
                    if (!outOfBounds(secondMove) && !isOccupiedSquare(board, secondMove)) {
                        moves.add(secondMove);
                    }
                }
            }
        }

        // diagonal attacking motion (to the right)
        addAttackMove(ChessGame.TeamColor.WHITE, Direction.RIGHT, position, board, moves);
        // diagonal attacking motion (to the left)
        addAttackMove(ChessGame.TeamColor.WHITE, Direction.LEFT, position, board, moves);
    }

    /*
    Black pawns can only move in the direction of decreasing rows.
    They can move two squares forward if on the 7th rank.
    They promote only if they are moving to rank 1.
     */
    private void pawnMovesBlack(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        int currRow = position.getRow();
        int currCol = position.getColumn();

        // forward motion
        var forwardSquare = new ChessPosition(currRow - 1, currCol);
        var move = new ChessMove(position, forwardSquare, null); // default move (without promotion)

        if (!outOfBounds(move) && !isOccupiedSquare(board, move)) {
            // if moving to promotion square, add moves with promotion
            if (forwardSquare.getRow() == 1) {
                addPromoMoves(position, forwardSquare, moves);
            } else {
                moves.add(move);
                // conditions for moving 2 squares forward
                if (currRow == 7) {
                    var secondForwardSquare = new ChessPosition(currRow - 2, currCol);
                    var secondMove = new ChessMove(position, secondForwardSquare, null);
                    if (!outOfBounds(secondMove) && !isOccupiedSquare(board, secondMove)) {
                        moves.add(secondMove);
                    }
                }
            }
        }

        // diagonal attacking motion (to the right)
        addAttackMove(ChessGame.TeamColor.BLACK, Direction.RIGHT, position, board, moves);
        // diagonal attacking motion (to the left)
        addAttackMove(ChessGame.TeamColor.BLACK, Direction.LEFT, position, board, moves);
    }

    /*
    Adds a move to a diagonal square after checking that it's a legal move.
     */
    private void addAttackMove(ChessGame.TeamColor color, Direction direction,
                               ChessPosition position, ChessBoard board, Collection<ChessMove> moves) {
        // determine the direction of attack
        ChessPosition moveSquare = getMoveSquare(color, direction, position);
        ChessMove move = new ChessMove(position, moveSquare, null);

        if (!outOfBounds(move) && isOccupiedSquare(board, move)) {
            if (color == ChessGame.TeamColor.WHITE &&
                    board.getPiece(move.getEndPosition()).getTeamColor() == ChessGame.TeamColor.BLACK) {
                // if moving to promotion square, add moves with promotion
                if (moveSquare.getRow() == 8) {
                    addPromoMoves(position, moveSquare, moves);
                } else {
                    moves.add(move);
                }
            } else if (color == ChessGame.TeamColor.BLACK &&
                    board.getPiece(move.getEndPosition()).getTeamColor() == ChessGame.TeamColor.WHITE) {
                if (moveSquare.getRow() == 1) {
                    addPromoMoves(position, moveSquare, moves);
                } else {
                    moves.add(move);
                }
            }
        }
    }

    private static ChessPosition getMoveSquare(ChessGame.TeamColor color, Direction direction, ChessPosition position) {
        int currRow = position.getRow();
        int currCol = position.getColumn();
        ChessPosition moveSquare;
        if (color == ChessGame.TeamColor.WHITE) {
            if (direction == Direction.RIGHT) {
                moveSquare = new ChessPosition(currRow + 1, currCol + 1);
            } else { // direction == LEFT
                moveSquare = new ChessPosition(currRow + 1, currCol - 1);
            }
        } else { // color == BLACK
            if (direction == Direction.RIGHT) {
                moveSquare = new ChessPosition(currRow - 1, currCol + 1);
            } else { // direction == LEFT
                moveSquare = new ChessPosition(currRow - 1, currCol - 1);
            }
        }
        return moveSquare;
    }

    /*
    This method is called only when a pawn reaches a promotion square and adds all possible promotion moves.
     */
    private void addPromoMoves(ChessPosition startPos, ChessPosition endPos, Collection<ChessMove> moves) {
        moves.add(new ChessMove(startPos, endPos, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(startPos, endPos, ChessPiece.PieceType.KNIGHT));
        moves.add(new ChessMove(startPos, endPos, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(startPos, endPos, ChessPiece.PieceType.QUEEN));
    }


    public static ChessMove addEnPassantMove(MoveHistory history, ChessBoard board, ChessPosition pawnPosition) {
        int row = pawnPosition.getRow();
        int col = pawnPosition.getColumn();

        if (findEnPassantMove(history, board, pawnPosition) == null) {
            return null;
        } else if (findEnPassantMove(history, board, pawnPosition) == Direction.UP_RIGHT) {
            return new ChessMove(pawnPosition, new ChessPosition(row - 1, col + 1), null);
        } else if (findEnPassantMove(history, board, pawnPosition) == Direction.UP_LEFT) {
            return new ChessMove(pawnPosition, new ChessPosition(row - 1, col - 1), null);
        } else if (findEnPassantMove(history, board, pawnPosition) == Direction.DOWN_RIGHT) {
            return new ChessMove(pawnPosition, new ChessPosition(row + 1, col + 1), null);
        } else if (findEnPassantMove(history, board, pawnPosition) == Direction.DOWN_LEFT) {
            return new ChessMove(pawnPosition, new ChessPosition(row + 1, col - 1), null);
        }
        return null;
    }

    /**
     * This method assumes that the user is finding the moves for a pawn and is called only by addEnPassantMove.
     * @return the direction in which the pawn can move, or null if it cannot en passant
     */
    private static PieceMovesCalculator.Direction findEnPassantMove(MoveHistory history, ChessBoard board, ChessPosition position) {
        // first see if the pawn to be moved is on the right rank for en passant
        ChessGame.TeamColor thisTeam = board.getPiece(position).getTeamColor();
        int pawnRow = position.getRow();
        int pawnCol = position.getColumn();

        if (thisTeam == ChessGame.TeamColor.WHITE) {
            if (pawnRow != 5) {
                return null;
            }
        } else { // if it's black to move
            if (pawnRow != 4) {
                return null;
            }
        }

        // next see if the last move was a pawn moving two squares forward
        var lastMove = history.getLastMove();

        ChessGame.TeamColor lastTeam;
        if (thisTeam == ChessGame.TeamColor.WHITE) {
            lastTeam = ChessGame.TeamColor.BLACK;
        } else {
            lastTeam = ChessGame.TeamColor.WHITE;
        }

        if (board.getPiece(lastMove.getEndPosition()).getPieceType() == ChessPiece.PieceType.PAWN) {
            if (lastTeam == ChessGame.TeamColor.WHITE
                    && lastMove.getStartPosition().getRow() == 2
                    && lastMove.getEndPosition().getRow() == 4) {

                // last, see if the pawn that moved is next to the current pawn
                if (lastMove.getEndPosition().getColumn() == pawnCol + 1) {
                    return Direction.UP_RIGHT;
                } else if (lastMove.getEndPosition().getColumn() == pawnCol - 1) {
                    return Direction.UP_LEFT;
                } else {
                    return null;
                }
            }
            else if (lastTeam == ChessGame.TeamColor.BLACK
                    && lastMove.getStartPosition().getRow() == 7
                    && lastMove.getEndPosition().getRow() == 5) {

                // last, see if the pawn that moved is next to the current pawn
                if (lastMove.getEndPosition().getColumn() == pawnCol + 1) {
                    return Direction.DOWN_RIGHT;
                } else if (lastMove.getEndPosition().getColumn() == pawnCol - 1) {
                    return Direction.DOWN_LEFT;
                } else {
                    return null;
                }
            }
        }

        return null;
    }

}
