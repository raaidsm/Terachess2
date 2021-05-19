package raaidsm.spring.test.models.utils;

import raaidsm.spring.test.models.Piece;
import raaidsm.spring.test.models.piece_properties.AttackType;

public class CheckingPieceStruct {
    public Piece piece;
    public AttackType attackType;

    public CheckingPieceStruct(Piece piece, AttackType attackType) {
        this.piece = piece;
        this.attackType = attackType;
    }
}