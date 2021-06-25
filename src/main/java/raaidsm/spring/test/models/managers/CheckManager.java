package raaidsm.spring.test.models.managers;

import raaidsm.spring.test.models.Piece;
import raaidsm.spring.test.models.moves_and_attacks.AttackType;
import raaidsm.spring.test.models.piece_properties.Colour;
import raaidsm.spring.test.models.pieces.King;
import raaidsm.spring.test.models.moves_and_attacks.CheckSummaryStruct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class CheckManager {
    //OVERVIEW:
    // For all methods of this class, it is assumed that they are called during the calculation of moves for the
    // current turn colour.
    // Therefore if clearChecks() is called during white's turn, all checks set against white will be cleared
    private Colour currentTurnColour;
    private final HashMap<Colour, List<CheckSummaryStruct>> checks;         //Key is same colour as checkedKing
    private final HashMap<Colour, List<String>> checkPathSquares;           //Key is same colour as checkedKing

    public CheckManager(Colour currentTurnColour) {
        this.currentTurnColour = currentTurnColour;
        checks = new HashMap<>(Map.ofEntries(
                entry(Colour.WHITE, new ArrayList<>()), entry(Colour.BLACK, new ArrayList<>())
        ));
        checkPathSquares = new HashMap<>(Map.ofEntries(
                entry(Colour.WHITE, new ArrayList<>()), entry(Colour.BLACK, new ArrayList<>())
        ));
    }

    public void setCurrentTurnColour(Colour currentTurnColour) {
        this.currentTurnColour = currentTurnColour;
    }

    public void setCheck(
            King checkedKing, Piece checkingPiece, AttackType attackTypeOfCheck, List<String> checkPathSquares) {
        //OVERVIEW: Set checks made by current turn colour
        Colour colourOfKing = checkedKing.getColour();

        List<CheckSummaryStruct> checksByColour = checks.get(colourOfKing);
        List<String> checkPathSquaresByColour = this.checkPathSquares.get(colourOfKing);
        checksByColour.add(new CheckSummaryStruct(checkedKing, checkingPiece, attackTypeOfCheck));
        checkPathSquaresByColour.addAll(checkPathSquares);
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
        checkPathSquares.get(currentTurnColour).clear();
    }
}