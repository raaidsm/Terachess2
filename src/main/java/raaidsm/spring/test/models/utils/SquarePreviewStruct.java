package raaidsm.spring.test.models.utils;

import raaidsm.spring.test.models.Piece;
import raaidsm.spring.test.models.piece_properties.Colour;

public class SquarePreviewStruct {
    public SqrStat squareStatus;
    public Piece piece;
    public Colour pieceColour;

    public SquarePreviewStruct(SqrStat squareStatus, Piece piece, Colour pieceColour) {
        this.squareStatus = squareStatus;
        this.piece = piece;
        this.pieceColour = pieceColour;
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