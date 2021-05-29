package raaidsm.spring.test.models.piece_properties;

public enum Colour {
    WHITE("white"),
    BLACK("black");

    private final String text;

    Colour(String text) {
        this.text = text;
    }

    public Colour oppositeColour() {
        if (this == Colour.WHITE) return Colour.BLACK;
        else if (this == Colour.BLACK) return Colour.WHITE;
        return null;
    }

    @Override
    public String toString() {
        return text;
    }
}