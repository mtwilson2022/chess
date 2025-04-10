package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard board;
    private final MoveHistory moveHistory;
    private boolean gameOver;

    public ChessGame() {
        // settings for starting a new game
        this.teamTurn = TeamColor.WHITE;
        this.board = new ChessBoard();
        board.resetBoard();
        this.moveHistory = new MoveHistory();
        this.gameOver = false;
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
        var allMoves = piece.pieceMoves(this.board, startPosition);

        // add any special moves that may apply (castling, en passant)
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            var enPassantMove = PawnMovesCalculator.addEnPassantMove(this.moveHistory, this.board, startPosition);
            if (enPassantMove != null) {
                allMoves.add(enPassantMove);
            }
        }

        // next, filter out the moves that end with the king in check
        var legalMoves = new ArrayList<ChessMove>();

        for (ChessMove move : allMoves) {
            if (isValidMove(move, piece.getTeamColor())) {
                legalMoves.add(move);
            }
        }

        return legalMoves;
    }

    /**
     *
     * @param move the move to test for validity
     * @param color the team's color
     * @return true if the move is legal, false otherwise
     */
    private boolean isValidMove(ChessMove move, TeamColor color) {
        // make a deep copy of the board, change the chess game's board to the copy
        var gameBoard = getBoard();
        var tempBoard = gameBoard.clone();
        setBoard(tempBoard);

        // make the hypothetical move in the copied board
        moveThePiece(move, tempBoard);

        // after the move, see if the king is in check. Only if he isn't can the move be added
        // regardless, go back to the original board
        boolean valid = !isInCheck(color);
        setBoard(gameBoard);
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

        // en passant
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            int endCol = move.getEndPosition().getColumn();
            int startCol = move.getStartPosition().getColumn();
            if (board.getPiece(move.getEndPosition()) == null && endCol != startCol) {
                // en passant is occurring. There is a piece either to the left or right of the pawn
                board.addPiece(new ChessPosition(move.getStartPosition().getRow(), endCol), null);
            }
        }

        board.addPiece(move.getEndPosition(), piece);
        board.addPiece(move.getStartPosition(), null);
    }


    // is en_passanting()

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
        var movingColor = this.board.getPiece(move.getStartPosition()).getTeamColor();
        if (movingColor != this.teamTurn) {
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
        if (teamColor == TeamColor.WHITE) {
            enemyColor = TeamColor.BLACK;
        } else {
            enemyColor = TeamColor.WHITE;
        }

        var kingPosition = findKingPosition(this.board, teamColor);

        // for each square, if it contains an enemy piece, find that piece's possible moves
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                var position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);

                if (pieceIsCheckingKing(piece, enemyColor, position, kingPosition)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean pieceIsCheckingKing(ChessPiece piece, TeamColor enemyColor,
                                        ChessPosition piecePosition, ChessPosition kingPosition) {
        if (piece != null) {
            if (piece.getTeamColor() == enemyColor) {
                var possibleMoves = piece.pieceMoves(this.board, piecePosition);
                // if the king's square is in the moves list, it is in check
                for (ChessMove move : possibleMoves) {
                    if (move.getEndPosition().equals(kingPosition)) {
                        return true;
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
        var allMoves = new ArrayList<ChessMove>();

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                var position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);

                if (piece != null) {
                    if (piece.getTeamColor() == color) {
                        var possibleMoves = validMoves(position);
                        allMoves.addAll(possibleMoves);
                    }
                }
            }
        }

        return allMoves;
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

    public boolean isGameOver() {
        return gameOver;
    }

    public void markGameAsOver() {
        gameOver = true;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board) && Objects.equals(moveHistory, chessGame.moveHistory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board, moveHistory);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "teamTurn=" + teamTurn +
                ", board=" + board +
                ", moveHistory=" + moveHistory +
                '}';
    }
}
