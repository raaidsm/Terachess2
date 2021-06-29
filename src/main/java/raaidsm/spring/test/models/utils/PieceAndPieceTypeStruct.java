package raaidsm.spring.test.models.utils;

import raaidsm.spring.test.models.Piece;
import raaidsm.spring.test.models.piece_properties.PieceType;

public class PieceAndPieceTypeStruct {
    public Piece piece;
    public PieceType pieceType;

    public PieceAndPieceTypeStruct(Piece piece, PieceType pieceType) {
        this.piece = piece;
        this.pieceType = pieceType;
    }
}