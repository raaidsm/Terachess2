package raaidsm.spring.test.models;

import raaidsm.spring.test.models.piece_properties.Colour;
import raaidsm.spring.test.models.utils.Direction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class Square implements Serializable {
    private String squareName;
    private Piece containedPiece;
    private final List<Piece> piecesAttacking;
    private final HashMap<String, Boolean> attackingColours;

    public static Direction getTwoSquareDistAndDir(Square square1, Square square2) {
        //OVERVIEW: The two squares must be on the same horizontal or vertical plane
        return Point.getTwoPointDistAndDir(square1.squareName, square2.squareName);
    }
    public static Direction getTwoSquareDistAndDir(String squareName1, String squareName2) {
        //OVERVIEW: The two squares must be on the same horizontal or vertical plane
        return Point.getTwoPointDistAndDir(squareName1, squareName2);
    }

    public Square(String squareName, Piece containedPiece) {
        this.squareName = squareName;
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