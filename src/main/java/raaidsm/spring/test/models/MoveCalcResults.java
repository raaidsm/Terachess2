package raaidsm.spring.test.models;

import javassist.compiler.ast.Pair;
import raaidsm.spring.test.models.pieces.King;

public class MoveCalcResults {
    public King kingChecked;
    public boolean hasMoves;

    public MoveCalcResults(King kingChecked, boolean hasMoves) {
        this.kingChecked = kingChecked;
        this.hasMoves = hasMoves;
    }
}