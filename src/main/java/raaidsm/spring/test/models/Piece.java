package raaidsm.spring.test.models;

import raaidsm.spring.test.models.utils.Colour;
import raaidsm.spring.test.models.utils.MoveCalcResults;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Piece implements Serializable {
    protected String name;
    protected Colour colour;
    protected String location;
    protected List<String> legalMoves;
    protected boolean isPinned;
    protected Piece pinningPiece;
    protected List<String> promotion;
    protected HashMap<String, Square> board;

    public Piece() {}
    public Piece(String name, Colour colour, String location) {
        this.name = name;
        this.colour = colour;
        this.location = location;
        this.legalMoves = new ArrayList<>();
        this.isPinned = false;
        this.pinningPiece = null;
        this.promotion = new ArrayList<>();
        this.board = null;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Colour getColour() {
        return colour;
    }
    public void setColour(Colour colour) {
        this.colour = colour;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public List<String> getLegalMoves() {
        return legalMoves;
    }
    public void setLegalMoves(List<String> legalMoves) {
        this.legalMoves = legalMoves;
    }
    public boolean isPinned() {
        return isPinned;
    }
    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }
    public List<String> getPromotion() {
        return promotion;
    }
    public void setPromotion(List<String> promotion) {
        this.promotion = promotion;
    }
    public HashMap<String, Square> getBoard() {
        return board;
    }
    public void setBoard(HashMap<String, Square> board) {
        this.board = board;
    }

    public MoveCalcResults calculateMoves() {
        //OVERVIEW:
        //Return checked king (null if none) and whether piece has any legal moves
        //TODO: For now, returning default value
        return new MoveCalcResults(null, false);
    }
    public MoveCalcResults reduceMovesDueToPin() {
        //OVERVIEW:
        //Return whether piece has any legal moves
        //TODO: For now, returning default value
        return new MoveCalcResults(null, false);
    }
    public MoveCalcResults reduceMovesDueToCheck() {
        //OVERVIEW:
        //Return whether piece has any legal moves
        //TODO: For now, returning default value
        return new MoveCalcResults(null, false);
    }
    public void clearAllMoves() {}
}