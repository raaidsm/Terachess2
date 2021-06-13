package raaidsm.spring.test.models;

import raaidsm.spring.test.models.piece_properties.Colour;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class Square implements Serializable {
    private Piece containedPiece;
    private final List<Piece> piecesAttacking;
    private final HashMap<String, Boolean> attackingColours;

    public Square(Piece containedPiece) {
        this.containedPiece = containedPiece;
        this.piecesAttacking = new ArrayList<>();
        this.attackingColours = new HashMap<>(Map.ofEntries(
                entry(Colour.WHITE.toString(), Boolean.valueOf("false")),
                entry(Colour.BLACK.toString(), Boolean.valueOf("false"))
        ));
    }

    public Piece getContainedPiece() {
        return containedPiece;
    }
    public void setContainedPiece(Piece containedPiece) {
        this.containedPiece = containedPiece;
    }

    public boolean isAttacked(Colour clrOfPieceAsking) {
        boolean hasAttackingPieces = !piecesAttacking.isEmpty();
        Colour oppositeColour = clrOfPieceAsking == Colour.WHITE ? Colour.BLACK : Colour.WHITE;

        return hasAttackingPieces && attackingColours.get(oppositeColour.toString());
    }
    public void setAttack(Piece piece, Colour colour) {
        piecesAttacking.add(piece);
        attackingColours.put(colour.toString(), Boolean.valueOf("true"));
    }
    public void clearAllAttacks() {
        piecesAttacking.clear();
        attackingColours.put(Colour.WHITE.toString(), Boolean.valueOf("false"));
        attackingColours.put(Colour.BLACK.toString(), Boolean.valueOf("false"));
    }

    @Override
    public String toString() {
        return "Square{" +
                "containedPiece=" + containedPiece +
                '}';
    }
}