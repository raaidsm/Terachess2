package raaidsm.spring.test.models.moves_and_attacks;

import raaidsm.spring.test.models.pieces.King;

public class MoveCalcResultsStruct {
    /*OVERVIEW:
    -checkedKing: Contains a King if a King is checked, otherwise null
    -squareName: Contains name of the square attacked, is null if square doesn't exist or there are multiple squares
    -attackType:
      -Contains type of attack for a collection of attacks of the same type (e.x. a rook's attacks in a straight line)
      -When describing a collection of different types of attacks, contains attack on the king or null
    -hasMoves: Records whether any legal moves exist in the collection described
    */
    public King checkedKing;
    public String squareName;
    public AttackType attackType;
    public boolean hasMoves;

    public MoveCalcResultsStruct(King checkedKing, String squareName, AttackType attackType, boolean hasMoves) {
        this.checkedKing = checkedKing;
        this.squareName = squareName;
        this.attackType = attackType;
        this.hasMoves = hasMoves;
    }
}