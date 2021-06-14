package raaidsm.spring.test.models.utils;

import raaidsm.spring.test.models.Piece;
import raaidsm.spring.test.models.piece_properties.AttackType;
import raaidsm.spring.test.models.piece_properties.Colour;
import raaidsm.spring.test.models.pieces.King;

import static java.util.Map.entry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckManagerTest {
    //OVERVIEW:
    // This is a test class that is not to be implemented anywhere.
    // Its purpose is just to safely build the CheckManager class from the ground up.
    // For all methods of this class, it is assumed that they are called during the calculation of moves for the
    // current turn colour.
    // Therefore if clearChecks() is called during white's turn, all checks set against white will be cleared
    private Colour currentTurnColour;
    private final HashMap<Colour, List<CheckSummaryStruct>> checks;     //Key is same colour as checkedKing

    public CheckManagerTest(Colour currentTurnColour) {
        this.currentTurnColour = currentTurnColour;
        checks = new HashMap<>(Map.ofEntries(
                entry(Colour.WHITE, new ArrayList<>()), entry(Colour.BLACK, new ArrayList<>())
        ));
    }

    public void setCurrentTurnColour(Colour currentTurnColour) {
        this.currentTurnColour = currentTurnColour;
    }

    public void setCheck(King checkedKing, Piece checkingPiece, AttackType attackTypeOfCheck) {
        //OVERVIEW: Set checks made by current turn colour
        List<CheckSummaryStruct> checksByColour = checks.get(checkedKing.getColour());
        checksByColour.add(new CheckSummaryStruct(checkedKing, checkingPiece, attackTypeOfCheck));
    }
    public boolean isCheck() {
        //OVERVIEW: Return true if current turn colour is under check
        return !checks.get(currentTurnColour).isEmpty();
    }
    public int numOfChecks() {
        //OVERVIEW: Return number of checks against the current turn colour
        return checks.get(currentTurnColour).size();
    }
    public void clearChecks() {
        //OVERVIEW: Clear checks against the current turn colour
        checks.get(currentTurnColour).clear();
    }
}