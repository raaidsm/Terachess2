package raaidsm.spring.test.models.managers;

import raaidsm.spring.test.models.Piece;
import raaidsm.spring.test.models.piece_properties.AttackType;
import raaidsm.spring.test.models.piece_properties.Colour;
import raaidsm.spring.test.models.pieces.King;
import raaidsm.spring.test.models.utils.AttackingPieceStruct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CheckManager {
    private HashMap<Colour, King> kings;
    private boolean isCheck;
    private King checkedKing;
    private List<AttackingPieceStruct> checkingPieces;

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
    public List<AttackingPieceStruct> getCheckingPieces() {
        return checkingPieces;
    }

    public void setCheck(Piece piece, AttackType attackType, King checkedKing) {
        isCheck = true;
        this.checkedKing = checkedKing;
        checkingPieces.add(new AttackingPieceStruct(piece, attackType));
    }
    public void clearChecks() {
        isCheck = false;
        checkedKing = null;
        checkingPieces.clear();
    }
}