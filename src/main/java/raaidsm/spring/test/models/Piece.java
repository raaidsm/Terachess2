package raaidsm.spring.test.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Piece implements Serializable {
    protected String name = "";
    protected String colour = "";
    protected String location = "";
    protected HashMap<String, Piece> board;
    protected List<String> legalMoves;

    public Piece() {}
    public Piece(String name, String colour, String location) {
        this.name = name;
        this.colour = colour;
        this.location = location;
        this.board = null;
        this.legalMoves = new ArrayList<>();
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getColour() {
        return colour;
    }
    public void setColour(String colour) {
        this.colour = colour;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public HashMap<String, Piece> getBoard() {
        return board;
    }
    public void setBoard(HashMap<String, Piece> board) {
        this.board = board;
    }
    public List<String> getLegalMoves() {
        return legalMoves;
    }
    public void setLegalMoves(List<String> legalMoves) {
        this.legalMoves = legalMoves;
    }

    public void calculateMoves() {}
}