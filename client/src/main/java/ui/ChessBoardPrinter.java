package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class ChessBoardPrinter {

    public static void main(String[] args) {
        ChessBoard board = new ChessBoard();
        board.resetBoard();

        for (String[] line : boardGenerator(board)) {
            for (String square : line) {
                System.out.print(square);
            }
            System.out.println();
        }

        var b = boardGenerator(board);
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        printRowOfSquares(out, 0, b, ChessGame.TeamColor.WHITE);
        printRowOfSquares(out, 0, b, ChessGame.TeamColor.BLACK);
        printRowOfSquares(out, 7, b, ChessGame.TeamColor.WHITE);
        printRowOfSquares(out, 7, b, ChessGame.TeamColor.BLACK);
    }

    public static void printBoardForWhite(PrintStream out) {

    }

    public static void printBoardForBlack(PrintStream out) {

    }

    private static void printRowOfSquares(PrintStream out, int row, String[][] board, ChessGame.TeamColor color) {
        out.print(SET_TEXT_COLOR_BLACK);

        String[] boardRow = board[row];

        if (color == ChessGame.TeamColor.WHITE) {
            for (int col = 0; col < 8; col++) {
                String squareColor;
                if ((row + col) % 2 == 0) {
                    squareColor = SET_BG_COLOR_DARK_GREEN;
                } else {
                    squareColor = SET_BG_COLOR_LIGHT_GREY;
                }
                out.print(squareColor + boardRow[col]);
            }
        } else {
            for (int col = 7; col >= 0; col--) {
                String squareColor;
                if ((row + col) % 2 == 0) {
                    squareColor = SET_BG_COLOR_DARK_GREEN;
                } else {
                    squareColor = SET_BG_COLOR_LIGHT_GREY;
                }
                out.print(squareColor + boardRow[col]);
            }
        }

        out.print(SET_BG_COLOR_BLACK);
        out.println();
    }

    private static String[][] boardGenerator(ChessBoard chessBoard) {
        String[][] charBoard = new String[8][8];
        var board = chessBoard.getBoardAsGrid();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                var piece = board.get(i).get(j);
                String boardChar = EMPTY;
                if (piece != null) {
                    boardChar = getPieceChar(piece);
                }
                charBoard[i][j] = boardChar;
            }
        }

        return charBoard;
    }

    private static String getPieceChar(ChessPiece piece) {
        String pieceColor;
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            pieceColor = SET_TEXT_COLOR_WHITE;
        } else {
            pieceColor = SET_TEXT_COLOR_BLACK;
        }

        var type = piece.getPieceType();
        String pieceChar;
        if (type == ChessPiece.PieceType.KING) {
            pieceChar = BLACK_KING;
        } else if (type == ChessPiece.PieceType.QUEEN) {
            pieceChar = BLACK_QUEEN;
        } else if (type == ChessPiece.PieceType.BISHOP) {
            pieceChar = BLACK_BISHOP;
        } else if (type == ChessPiece.PieceType.KNIGHT) {
            pieceChar = BLACK_KNIGHT;
        } else if (type == ChessPiece.PieceType.ROOK) {
            pieceChar = BLACK_ROOK;
        } else {
            pieceChar = BLACK_PAWN;
        }

        return pieceColor + pieceChar;
    }
}
