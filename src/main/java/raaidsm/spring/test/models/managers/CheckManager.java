package raaidsm.spring.test.models.managers;

import raaidsm.spring.test.models.Piece;
import raaidsm.spring.test.models.piece_properties.AttackType;
import raaidsm.spring.test.models.piece_properties.Colour;
import raaidsm.spring.test.models.pieces.King;
import raaidsm.spring.test.models.utils.CheckingPieceStruct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckManager {
    private HashMap<Colour, King> kings;
    private boolean isCheck;
    private King checkedKing;
    private List<CheckingPieceStruct> checkingPieces;

    public CheckManager(King whiteKing, King blackKing) {
        //Guard clause for if kings to initialize are null
        if (whiteKing == null || blackKing == null) throw new IllegalArgumentException("Can't set kings to null");

        this.kings = new HashMap<>();
        kings.put(Colour.WHITE, whiteKing);
        kings.put(Colour.BLACK, blackKing);
        this.isCheck = false;
        this.checkedKing = null;
        this.checkingPieces = new ArrayList<>();
    }

    public HashMap<Colour, King> getKings() {
        return kings;
    }
    public boolean isCheck() {
        return isCheck;
    }
    public King getCheckedKing() {
        return checkedKing;
    }
    public List<CheckingPieceStruct> getCheckingPieces() {
        return checkingPieces;
    }

    public void setCheck(Piece piece, AttackType attackType, King checkedKing) {
        //Change isCheck
        isCheck = true;
        //Change checkedKing
        this.checkedKing = checkedKing;
        //Change checkingPieces
        checkingPieces.add(new CheckingPieceStruct(piece, attackType));
    }
    public void clearChecks() {
        kings.clear();
        isCheck = false;
        checkedKing = null;
        checkingPieces.clear();
    }
}