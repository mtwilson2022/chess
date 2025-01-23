package chess;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

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
        for (int i = 0; i < 8; i++) {
            board.get(i).clear();
            for (int j = 0; j < 8; j++) {
                board.get(i).add(null);
            }
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
}
