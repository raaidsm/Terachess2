const Colour = Object.freeze({
    WHITE: Symbol("white"),
    BLACK: Symbol("black")
});

class TurnManager {
    constructor() {
        this.colour = Colour.WHITE;
    }

    getColour() {
        return this.colour;
    }
    switchColour() {
        if (this.colour === Colour.WHITE) this.colour = Colour.BLACK;
        else if (this.colour === Colour.BLACK) this.colour = Colour.WHITE;
    }
}

export { TurnManager, Colour };