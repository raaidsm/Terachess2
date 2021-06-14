package raaidsm.spring.test.models.utils;

import raaidsm.spring.test.models.Piece;
import raaidsm.spring.test.models.piece_properties.AttackType;

public class AttackOnSquareStruct {
    public Piece piece;
    public AttackType attackType;
    public String attackedSquareName;

    public AttackOnSquareStruct(Piece piece, AttackType attackType, String attackedSquareName) {
        this.piece = piece;
        this.attackType = attackType;
        this.attackedSquareName = attackedSquareName;
    }
}