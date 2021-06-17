package raaidsm.spring.test.models.moves_and_attacks;

import raaidsm.spring.test.models.Piece;

public class AttackOnSquareStruct {
    public Piece piece;
    public AttackType attackType;
    public AttackDir attackDir;
    public String attackedSquareName;

    public AttackOnSquareStruct(
            Piece piece, AttackType attackType, AttackDir attackDir, String attackedSquareName
    ) {
        this.piece = piece;
        this.attackType = attackType;
        this.attackDir = attackDir;
        this.attackedSquareName = attackedSquareName;
    }
}