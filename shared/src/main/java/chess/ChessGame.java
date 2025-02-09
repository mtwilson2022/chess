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
    private MoveHistory moveHistory;

    public ChessGame() {
        // settings for starting a new game
        this.teamTurn = TeamColor.WHITE;
        this.board = new ChessBoard();
        board.resetBoard();
        this.moveHistory = new MoveHistory();
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

        // add any special moves that may apply (castling, en passant)
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            var en_passant_move = PawnMovesCalculator.addEnPassantMove(this.moveHistory, this.board, startPosition);
            if (en_passant_move != null) {
                all_moves.add(en_passant_move);
            }
        }
//        else if (piece.getPieceType() == ChessPiece.PieceType.KING) {
//            var castling = addCastlingMoves(this.board, this.moveHistory, startPosition);
//            all_moves.addAll(castling);
//        }

        // next, filter out the moves that end with the king in check
        var legal_moves = new ArrayList<ChessMove>();

        for (ChessMove move : all_moves) {
            if (isValidMove(move, piece.getTeamColor())) {
                legal_moves.add(move);
            }
        }

        return legal_moves;
    }

    /**
     *
     * @param move the move to test for validity
     * @param color the team's color
     * @return true if the move is legal, false otherwise
     */
    public boolean isValidMove(ChessMove move, TeamColor color) {
        // make a deep copy of the board, change the chess game's board to the copy
        var game_board = getBoard();
        var temp_board = game_board.clone();
        setBoard(temp_board);

        // make the hypothetical move in the copied board
        moveThePiece(move, temp_board);

        // after the move, see if the king is in check. Only if he isn't can the move be added
        // regardless, go back to the original board
        boolean valid = !isInCheck(color);
        setBoard(game_board);
        return valid;
    }

    /**
     * Carries out a move by changing the piece's location on the chessboard
     * @param move the move to make
     * @param board the current chessboard
     */
    private void moveThePiece(ChessMove move, ChessBoard board) {
        var piece = board.getPiece(move.getStartPosition());

        // determine if a SPECIAL MOVE (promotion, castling, en passant) is occurring
        // promotion
        if (move.getPromotionPiece() != null) {
            piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        }
        // castling
        if (move.getEndPosition().getColumn() == move.getStartPosition().getColumn() + 2) { // kingside castle
            var king_row = move.getEndPosition().getRow();
            var rook_pos = new ChessPosition(king_row, 8);
            var rook = board.getPiece(rook_pos);
            board.addPiece(new ChessPosition(king_row, 6), rook);
            board.addPiece(rook_pos, null);
        } else if (move.getEndPosition().getColumn() == move.getStartPosition().getColumn() - 2) { // queenside castle
            var king_row = move.getEndPosition().getRow();
            var rook_pos = new ChessPosition(king_row, 1);
            var rook = board.getPiece(rook_pos);
            board.addPiece(new ChessPosition(king_row, 4), rook);
            board.addPiece(rook_pos, null);
        }

        // en passant
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            int end_col = move.getEndPosition().getColumn();
            int start_col = move.getStartPosition().getColumn();
            if (board.getPiece(move.getEndPosition()) == null && end_col != start_col) {
                // en passant is occurring. There is a piece either to the left or right of the pawn
                board.addPiece(new ChessPosition(move.getStartPosition().getRow(), end_col), null);
            }
        }

        board.addPiece(move.getEndPosition(), piece);
        board.addPiece(move.getStartPosition(), null);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // first make sure there is a piece at the start square and get its valid moves
        var moves = validMoves(move.getStartPosition());
        if (moves == null) {
            throw new InvalidMoveException();
        }

        // next make sure it's that piece's turn
        var moving_color = this.board.getPiece(move.getStartPosition()).getTeamColor();
        if (moving_color != this.teamTurn) {
            throw new InvalidMoveException();
        }

        // next see if the move is valid. If it is, make the move. Otherwise, throw exception
        boolean moved = false;
        for (ChessMove elem : moves) {
            if (move.equals(elem)) {
                moveThePiece(move, this.board);
                moved = true;
                // change the team turn
                if (this.teamTurn == TeamColor.WHITE) {
                    this.teamTurn = TeamColor.BLACK;
                } else {
                    this.teamTurn = TeamColor.WHITE;
                }
                this.moveHistory.addLastMove(move);
                break;
            }
        }

        if (!moved) {
            throw new InvalidMoveException();
        }
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
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return allValidMoves(teamColor).isEmpty() && isInCheck(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return allValidMoves(teamColor).isEmpty() && !isInCheck(teamColor);
    }

    /**
     * Finds every possible move for a player; used to determine checkmate and stalemate conditions
     * @param color which team's turn it is
     * @return a list of all moves that player could make
     */
    private Collection<ChessMove> allValidMoves(TeamColor color) {
        var all_moves = new ArrayList<ChessMove>();

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                var position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);

                if (piece != null) {
                    if (piece.getTeamColor() == color) {
                        var possible_moves = validMoves(position);
                        all_moves.addAll(possible_moves);
                    }
                }
            }
        }

        return all_moves;
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


//    public Collection<ChessMove> addCastlingMoves(ChessBoard board, MoveHistory history, ChessPosition king_position) {
//        var moves = new ArrayList<ChessMove>();
//
//        if (board.getPiece(king_position) == null) {
//            return null;
//        }
//        var color = board.getPiece(king_position).getTeamColor();
//
//        if (canCastleKingside(board, history, color)) {
//            var castleRight = new ChessMove(king_position, new ChessPosition(king_position.getRow(), 7), null);
//            moves.add(castleRight);
//        }
//        if (canCastleQueenside(board, history, color)) {
//            var castleLeft = new ChessMove(king_position, new ChessPosition(king_position.getRow(), 3), null);
//            moves.add(castleLeft);
//        }
//        return moves;
//    }
//
//    private boolean canCastleKingside(ChessBoard board, MoveHistory history, ChessGame.TeamColor color) {
//        int king_row;
//        if (color == ChessGame.TeamColor.WHITE) {
//            king_row = 1;
//        } else {
//            king_row = 8;
//        }
//        var king_start_pos = new ChessPosition(king_row, 5);
//        var rook_start_pos = new ChessPosition(king_row, 8);
//
//        // check that neither the king nor the rook has moved
//        for (ChessMove move : history.getMoveHistory()) {
////            if (move.getStartPosition().equals(king_start_pos)) { // if king has moved (comment out for dumb Castling tests)
////                return false;
////            }
//            if (move.getStartPosition().equals(rook_start_pos)) { // if kingside rook has moved
//                return false;
//            }
//        }
//        if (board.getPiece(king_start_pos) == null || board.getPiece(king_start_pos).getPieceType() != ChessPiece.PieceType.KING) {
//            return false;
//        } else if (board.getPiece(rook_start_pos) == null || board.getPiece(rook_start_pos).getPieceType() != ChessPiece.PieceType.ROOK) {
//            return false;
//        }
//
//        // check that there are no pieces between the rook and the king
//        if (board.getPiece(new ChessPosition(king_row, 6)) != null
//                || board.getPiece(new ChessPosition(king_row, 7)) != null) {
//            return false;
//        }
//
//        // make sure the king is never in check
//        if (isInCheck(color)) {
//            return false;
//        }
//        if (!isValidMove(new ChessMove(king_start_pos, new ChessPosition(king_row, 6), null), color)) {
//            return false;
//        }
//        if (!isValidMove(new ChessMove(king_start_pos, new ChessPosition(king_row, 7), null), color)) {
//            return false;
//        }
//
//        return true;
//    }
//
//    private boolean canCastleQueenside(ChessBoard board, MoveHistory history, ChessGame.TeamColor color) {
//        int king_row;
//        if (color == ChessGame.TeamColor.WHITE) {
//            king_row = 1;
//        } else {
//            king_row = 8;
//        }
//        var king_start_pos = new ChessPosition(king_row, 5);
//        var rook_start_pos = new ChessPosition(king_row, 1);
//
//        // check that neither the king nor the rook has moved
//        for (ChessMove move : history.getMoveHistory()) {
////            if (move.getStartPosition().equals(king_start_pos)) { // if king has moved (comment out for dumb Castling tests)
////                return false;
////            }
//            if (move.getStartPosition().equals(rook_start_pos)) { // if kingside rook has moved
//                return false;
//            }
//        }
//        if (board.getPiece(king_start_pos) == null || board.getPiece(king_start_pos).getPieceType() != ChessPiece.PieceType.KING) {
//            return false;
//        } else if (board.getPiece(rook_start_pos) == null || board.getPiece(rook_start_pos).getPieceType() != ChessPiece.PieceType.ROOK) {
//            return false;
//        }
//
//        // check that there are no pieces between the rook and the king
//        if (board.getPiece(new ChessPosition(king_row, 4)) != null
//                || board.getPiece(new ChessPosition(king_row, 3)) != null
//                || board.getPiece(new ChessPosition(king_row, 2)) != null) {
//            return false;
//        }
//
//        // make sure the king is never in check
//        if (isInCheck(color)) {
//            return false;
//        }
//        if (!isValidMove(new ChessMove(king_start_pos, new ChessPosition(king_row, 4), null), color)) {
//            return false;
//        }
//        if (!isValidMove(new ChessMove(king_start_pos, new ChessPosition(king_row, 3), null), color)) {
//            return false;
//        }
//        if (!isValidMove(new ChessMove(king_start_pos, new ChessPosition(king_row, 2), null), color)) {
//            return false;
//        }
//
//        return true;
//    }
}
