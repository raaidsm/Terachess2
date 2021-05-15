package raaidsm.spring.test.models.utils;

public enum Colour {
    WHITE("white"), BLACK("black");
    private final String text;

    Colour(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
