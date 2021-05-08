package raaidsm.spring.test.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Piece implements Serializable {
    private String name = "";
    private String colour = "";
    private String location = "";
    private List<String> legalMoves = new ArrayList<String>();

    public Piece() {}
    public Piece(String name, String colour, String location) {
        this.name = name;
        this.colour = colour;
        this.location = location;
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
    public List<String> getLegalMoves() {
        return legalMoves;
    }
    public void setLegalMoves(List<String> legalMoves) {
        this.legalMoves = legalMoves;
    }
}