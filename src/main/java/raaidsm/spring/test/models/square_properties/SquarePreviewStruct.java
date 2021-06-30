package raaidsm.spring.test.models.square_properties;

import raaidsm.spring.test.models.Piece;
import raaidsm.spring.test.models.piece_properties.Colour;
import raaidsm.spring.test.models.pieces.Pawn;

public class SquarePreviewStruct {
    public SqrStat squareStatus;
    public String squareName;
    public Piece piece;
    public Colour pieceColour;
    public Pawn shadowPawn;

    public SquarePreviewStruct(SqrStat squareStatus, String squareName, Piece piece, Colour pieceColour) {
        this.squareStatus = squareStatus;
        this.squareName = squareName;
        this.piece = piece;
        this.pieceColour = pieceColour;
        shadowPawn = null;
    }
    public SquarePreviewStruct(SqrStat squareStatus, String squareName, Piece piece, Colour pieceColour,
                               Pawn shadowPawn) {
        this.squareStatus = squareStatus;
        this.squareName = squareName;
        this.piece = piece;
        this.pieceColour = pieceColour;
        this.shadowPawn = shadowPawn;
    }

    @Override
    public String toString() {
        return "SquarePreviewStruct{" +
                "squareStatus=" + squareStatus +
                ", piece=" + piece +
                ", pieceColour=" + pieceColour +
                '}';
    }
}