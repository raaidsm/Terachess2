package raaidsm.spring.test.models;

import raaidsm.spring.test.models.piece_properties.Colour;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class Square implements Serializable {
    public Piece containedPiece;
    public List<Piece> piecesAttacking;
    public HashMap<String, Boolean> attackingColours;

    public Square(Piece containedPiece) {
        this.containedPiece = containedPiece;
        this.piecesAttacking = new ArrayList<>();
        this.attackingColours = new HashMap<>(Map.ofEntries(
                entry(Colour.WHITE.toString(), false), entry(Colour.BLACK.toString(), false)
        ));
    }

    public boolean isAttacked(Colour clrOfPieceAsking) {
        boolean hasAttackingPieces = !piecesAttacking.isEmpty();
        Colour oppositeColour = clrOfPieceAsking == Colour.WHITE ? Colour.BLACK : Colour.WHITE;

        return hasAttackingPieces && attackingColours.get(oppositeColour);
    }
    public void clearAttacks() {
        piecesAttacking.clear();
        attackingColours.put(Colour.WHITE.toString(), false);
        attackingColours.put(Colour.BLACK.toString(), false);
    }

    @Override
    public String toString() {
        return "Square{" +
                "containedPiece=" + containedPiece +
                '}';
    }
}