package chess;

import java.util.Collection;
import java.util.ArrayList;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard board;

    public ChessGame() {
        // settings for starting a new game
        this.teamTurn = TeamColor.WHITE;
        this.board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        // begin by finding the piece and get all its possible moves
        var piece = this.board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        var all_moves = piece.pieceMoves(this.board, startPosition);

        // next, filter out the moves that end with the king in check
        var legal_moves = new ArrayList<ChessMove>();
        var king_position = findKingPosition(this.board, piece.getTeamColor());
        for (ChessMove move : all_moves) {
            // make a deep copy of the board

            // make the hypothetical move in the copied board

            // after the move, see if the king is in check. Only if he isn't can the move be added
            if ( !isInCheck(piece.getTeamColor()) ) {
                legal_moves.add(move);
            }
        }

        return legal_moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        TeamColor enemyColor;
        if (teamColor == TeamColor.WHITE) enemyColor = TeamColor.BLACK;
        else enemyColor = TeamColor.WHITE;

        var king_position = findKingPosition(this.board, teamColor);

        // for each square, if it contains an enemy piece, find that piece's possible moves
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                var position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);

                if (piece != null) {
                    if (piece.getTeamColor() == enemyColor) {
                        var possible_moves = piece.pieceMoves(this.board, position);
                        // if the king's square is in the moves list, it is in check
                        for (ChessMove move : possible_moves) {
                            if (move.getEndPosition().equals(king_position)) return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     *
     * @param board the current chessboard
     * @param color which color of king you want to find
     * @return The position of the king of the specified color
     */
    private ChessPosition findKingPosition(ChessBoard board, TeamColor color) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                var position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null) {
                    if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == color) {
                        return position;
                    }
                }
            }
        }
        return null; // this is just to make the compiler happy; if it can't find the king then the game falls apart
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
