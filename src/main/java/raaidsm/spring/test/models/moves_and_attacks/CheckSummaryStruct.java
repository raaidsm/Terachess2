package raaidsm.spring.test.models.moves_and_attacks;

import raaidsm.spring.test.models.Piece;
import raaidsm.spring.test.models.pieces.King;

public class CheckSummaryStruct {
    //TODO: Add a parameter for tracking the direction of check (horizontal, vertical, etc)
    public King checkedKing;
    public Piece checkingPiece;
    public AttackType attackType;

    public CheckSummaryStruct(King checkedKing, Piece checkingPiece, AttackType attackType) {
        this.checkedKing = checkedKing;
        this.checkingPiece = checkingPiece;
        this.attackType = attackType;
    }
}