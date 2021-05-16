package raaidsm.spring.test.models.utils;

import javassist.compiler.ast.Pair;
import raaidsm.spring.test.models.pieces.King;

public class MoveCalcResultsStruct {
    public King kingChecked;
    public boolean hasMoves;

    public MoveCalcResultsStruct(King kingChecked, boolean hasMoves) {
        this.kingChecked = kingChecked;
        this.hasMoves = hasMoves;
    }
}