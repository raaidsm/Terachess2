package raaidsm.spring.test.models;

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
                entry("white", false), entry("black", false)
        ));
    }
}