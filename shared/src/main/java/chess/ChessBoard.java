package chess;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Cloneable {

    private ArrayList<ArrayList<ChessPiece>> board;

    /*
    Creates a 2D 8x8 array populated with nulls.
    The outer array consists of the rows (inner arrays), which consist of squares (contents of inner arrays)
     */
    public ChessBoard() {
        this.board = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            var row = new ArrayList<ChessPiece>();
            for (int j = 0; j < 8; j++) {
                row.add(null);
            }
            this.board.add(row);
        }
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        // convert row and col from 1-based indexing to 0-based
        int row = position.getRow() - 1;
        int col = position.getColumn() - 1;
        board.get(row).set(col, piece);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        // convert row and col from 1-based indexing to 0-based
        int row = position.getRow() - 1;
        int col = position.getColumn() - 1;
        return board.get(row).get(col);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // first, clear the chess board
        for (int i = 0; i < 8; i++) {
            board.get(i).clear();
            for (int j = 0; j < 8; j++) {
                board.get(i).add(null);
            }
        }

        // then add the pieces
        addPieces(ChessGame.TeamColor.WHITE);
        addPieces(ChessGame.TeamColor.BLACK);
    }

    /**
     * Adds the pieces for a given color to the starting board
     */
    private void addPieces(ChessGame.TeamColor color) {
        // get rows to add to
        int backRow, pawnRow;
        if (color == ChessGame.TeamColor.WHITE) {
            backRow = 1;
            pawnRow = 2;
        } else { // if color == ChessGame.TeamColor.BLACK
            backRow = 8;
            pawnRow = 7;
        }

        // add the back row
        addPiece(new ChessPosition(backRow, 1), new ChessPiece(color, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(backRow, 2), new ChessPiece(color, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(backRow, 3), new ChessPiece(color, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(backRow, 4), new ChessPiece(color, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(backRow, 5), new ChessPiece(color, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(backRow, 6), new ChessPiece(color, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(backRow, 7), new ChessPiece(color, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(backRow, 8), new ChessPiece(color, ChessPiece.PieceType.ROOK));

        // add the pawns
        for (int i = 1; i <= 8; i++) {
            addPiece(new ChessPosition(pawnRow, i), new ChessPiece(color, ChessPiece.PieceType.PAWN));
        }
    }

    @Override
    public ChessBoard clone() {
        try {
            ChessBoard clone = (ChessBoard) super.clone();
            ArrayList<ArrayList<ChessPiece>> copyBoard = new ArrayList<>();

            for (int i = 1; i <= 8; i++) {
                ArrayList<ChessPiece> row = new ArrayList<>();
                for (int j = 1; j <= 8; j++) {
                    var square = new ChessPosition(i, j);
                    if (getPiece(square) == null) {
                        row.add(j-1, null);
                    } else {
                        ChessPiece piece = getPiece(square).clone();
                        row.add(j-1, piece);
                    }
                }
                copyBoard.add(i-1, row);
            }

            clone.board = copyBoard;
            return clone;

        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.equals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(board);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "board=" + board +
                '}';
    }
}
