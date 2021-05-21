package raaidsm.spring.test.models.utils;

import raaidsm.spring.test.models.Piece;
import raaidsm.spring.test.models.piece_properties.Colour;

public class SquarePreviewStruct {
    public SquareStatus squareStatus;
    public Piece piece;
    public Colour pieceColour;

    public SquarePreviewStruct(SquareStatus squareStatus, Piece piece, Colour pieceColour) {
        this.squareStatus = squareStatus;
        this.piece = piece;
        this.pieceColour = pieceColour;
    }
}