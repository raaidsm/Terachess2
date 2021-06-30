package raaidsm.spring.test.models.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import raaidsm.spring.test.models.piece_properties.Colour;

import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;

public class TurnManager {
    private final Logger logger = LoggerFactory.getLogger(TurnManager.class);
    private Colour currentTurnColour;
    private final HashMap<Colour, Integer> enPassantTimersByColour;

    public TurnManager() {
        this.currentTurnColour = Colour.WHITE;
        enPassantTimersByColour = new HashMap<>(Map.ofEntries(
                entry(Colour.WHITE, 0), entry(Colour.BLACK, 0)
        ));
    }

    public Colour getCurrentTurnColour() {
        return currentTurnColour;
    }
    public void switchCurrentTurnColour() {
        if (currentTurnColour == Colour.WHITE) currentTurnColour = Colour.BLACK;
        else if (currentTurnColour == Colour.BLACK) currentTurnColour = Colour.WHITE;
    }
    public void setEnPassant() {
        enPassantTimersByColour.put(currentTurnColour, 1);
    }
    public boolean isTurnToRemoveEnPassant() {
        int enPassantTimer = enPassantTimersByColour.get(currentTurnColour);
        if (enPassantTimer == 0) return false;
        if (enPassantTimer == 1) {
            enPassantTimersByColour.put(currentTurnColour, 0);
            return true;
        }
        else throw new RuntimeException("En passant timer cannot be longer than 1 turn long");
    }
}