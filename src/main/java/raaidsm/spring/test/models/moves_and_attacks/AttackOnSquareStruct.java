package raaidsm.spring.test.models.moves_and_attacks;

import raaidsm.spring.test.models.Piece;

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