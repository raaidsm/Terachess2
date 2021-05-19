package raaidsm.spring.test.models.utils;

import raaidsm.spring.test.models.piece_properties.AttackType;
import raaidsm.spring.test.models.pieces.King;

public class MoveCalcResultsStruct {
    public King checkedKing;
    public AttackType attackType;
    public boolean hasMoves;

    public MoveCalcResultsStruct(King checkedKing, AttackType attackType, boolean hasMoves) {
        this.checkedKing = checkedKing;
        this.attackType = attackType;
        this.hasMoves = hasMoves;
    }
}