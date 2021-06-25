package raaidsm.spring.test.models.moves_and_attacks;

import raaidsm.spring.test.models.pieces.King;

import java.util.ArrayList;
import java.util.List;

public class MoveCalcSummaryStruct {
    /*OVERVIEW:
        -checkedKing: Contains a King if a King is checked, otherwise null
        -attackType: Describes type of attack against King if check, otherwise null
        -hasMoves: Records whether any legal moves exist in the collection described
    */
    public King checkedKing;
    public AttackType attackType;
    public boolean hasMoves;
    public List<String> squareNamesOnPathOfCheck;

    //Constructor where hasMoves is set
    public MoveCalcSummaryStruct(King checkedKing, AttackType attackType) {
        init(checkedKing, attackType);
    }
    //Constructor where hasMoves is assumed to be initialized as true
    public MoveCalcSummaryStruct(King checkedKing, AttackType attackType, boolean hasMoves) {
        init(checkedKing, attackType);
        this.hasMoves = hasMoves;
    }
    public void init(King checkedKing, AttackType attackType) {
        this.checkedKing = checkedKing;
        this.attackType = attackType;
        hasMoves = true;
        squareNamesOnPathOfCheck = new ArrayList<>();
    }

    public MoveCalcSummaryStruct setSquareNamesOnPathOfCheck(List<String> squareNamesOnPathOfCheck) {
        this.squareNamesOnPathOfCheck = squareNamesOnPathOfCheck;
        return this;
    }
}