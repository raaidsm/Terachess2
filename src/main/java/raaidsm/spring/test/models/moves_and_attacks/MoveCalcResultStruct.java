package raaidsm.spring.test.models.moves_and_attacks;

import raaidsm.spring.test.models.pieces.King;

public class MoveCalcResultStruct {
    public King checkedKing;
    public String attackedSquareName;
    public AttackType attackType;
    public AttackDir attackDir;
    public boolean hasMoves;

    //region Constructors
    //Constructor where hasMoves is assumed to be initialized as true
    public MoveCalcResultStruct(
            King checkedKing, String attackedSquareName, AttackType attackType, AttackDir attackDir
    ) {
        init(checkedKing, attackedSquareName, attackType, attackDir);
    }
    //Constructor where hasMoves is set
    public MoveCalcResultStruct(
            King checkedKing, String attackedSquareName, AttackType attackType, AttackDir attackDir, boolean hasMoves
    ) {
        init(checkedKing, attackedSquareName, attackType, attackDir);
        this.hasMoves = hasMoves;
    }
    private void init(
            King checkedKing, String attackedSquareName, AttackType attackType, AttackDir attackDir
    ) {
        this.checkedKing = checkedKing;
        this.attackedSquareName = attackedSquareName;
        this.attackType = attackType;
        this.attackDir = attackDir;
        hasMoves = true;
    }
    //endregion
}