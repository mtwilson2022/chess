package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;

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
    }

    public void printBoardForWhite() {

    }

    public void printBoardForBlack() {

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
        var type = piece.getPieceType();
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return getStr(type, WHITE_KING, WHITE_QUEEN, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK, WHITE_PAWN);
        } else {
            return getStr(type, BLACK_KING, BLACK_QUEEN, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK, BLACK_PAWN);
        }
    }

    private static String getStr(ChessPiece.PieceType type, String king, String queen, String bishop, String knight, String rook, String pawn) {
        if (type == ChessPiece.PieceType.KING) {
            return king;
        } else if (type == ChessPiece.PieceType.QUEEN) {
            return queen;
        } else if (type == ChessPiece.PieceType.BISHOP) {
            return bishop;
        } else if (type == ChessPiece.PieceType.KNIGHT) {
            return knight;
        } else if (type == ChessPiece.PieceType.ROOK) {
            return rook;
        } else {
            return pawn;
        }
    }

}
