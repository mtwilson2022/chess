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
        var b = boardGenerator(board);
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        printBoardForWhite(out, b);
        out.println();
        printBoardForBlack(out, b);
    }

    public static void printBoardForWhite(PrintStream out, String[][] board) {
        var white = ChessGame.TeamColor.WHITE;
        printLabelRow(out, white);
        for (int row = 7; row >=0; row--) {
            printRowOfSquares(out, row, board, white);
        }
        printLabelRow(out, white);
    }

    public static void printBoardForBlack(PrintStream out, String[][] board) {
        var black = ChessGame.TeamColor.BLACK;
        printLabelRow(out, black);
        for (int row = 0; row < 8; row++) {
            printRowOfSquares(out, row, board, black);
        }
        printLabelRow(out, black);
    }

    private static void printLabelRow(PrintStream out, ChessGame.TeamColor color) {
        String[] headers = {" A\u2003", " B\u2003", " C\u2003", " D\u2003", " E\u2003", " F\u2003", " G\u2003", " H\u2003"};

        out.print(SET_BG_COLOR_DARK_GREY);
        out.print(SET_TEXT_COLOR_WHITE);
        out.print("   ");

        // for black, the headers need to be printed in reverse order
        if (color == ChessGame.TeamColor.WHITE) {
            for (String header : headers) {
                out.print(header);
            }
        } else {
            String[] headersReversed = new String[8];
            for (int i = 0; i < 8; i++) {
                headersReversed[i] = headers[7-i];
            }
            for (String header : headersReversed) {
                out.print(header);
            }
        }

        out.print("   ");
        out.print(SET_BG_COLOR_BLACK);
        out.println();
    }

    private static void printRowOfSquares(PrintStream out, int row, String[][] board, ChessGame.TeamColor color) {
        printRowNumber(out, row);

        String[] boardRow = board[row];

        // columns need to be printed in same order for the white player, reverse order for the black player
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

        printRowNumber(out, row);

        out.print(SET_BG_COLOR_BLACK);
        out.println();
    }

    private static void printRowNumber(PrintStream out, int row) {
        int num = row + 1; // change to 1-based indexing to match chess board conventions
        out.print(SET_BG_COLOR_DARK_GREY);
        out.print(SET_TEXT_COLOR_WHITE);
        out.print(" " + num + " ");
    }

    public static String[][] boardGenerator(ChessBoard chessBoard) {
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
