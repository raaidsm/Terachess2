package raaidsm.spring.test.models.forms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LegalMovesForm implements Serializable {
    private List<String> legalMoves = new ArrayList<String>();

    public LegalMovesForm() {}

    public List<String> getLegalMoves() {
        return legalMoves;
    }
    public void setLegalMoves(List<String> legalMoves) {
        this.legalMoves = legalMoves;
    }
}