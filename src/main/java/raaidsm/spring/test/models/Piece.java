package raaidsm.spring.test.models;

import java.io.Serializable;

public class Piece implements Serializable {
    private String name = "";
    private String colour = "";
    private String location = "";

    private Piece() {}

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
}