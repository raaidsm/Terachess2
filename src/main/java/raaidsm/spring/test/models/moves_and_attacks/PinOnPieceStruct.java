package raaidsm.spring.test.models.moves_and_attacks;

import raaidsm.spring.test.models.Piece;

public class PinOnPieceStruct {
    public Piece pinningPiece;
    public AttackDir attackDirOfPin;

    public PinOnPieceStruct(Piece pinningPiece, AttackDir attackDirOfPin) {
        this.pinningPiece = pinningPiece;
        this.attackDirOfPin = attackDirOfPin;
    }
}