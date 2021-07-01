const Colour = Object.freeze({
    WHITE: Symbol("white"),
    BLACK: Symbol("black")
});

class TurnManager {
    constructor() {
        this.currentTurnColour = Colour.WHITE;
    }

    getCurrentTurnColour() {
        return this.currentTurnColour;
    }
    switchCurrentTurnColour() {
        if (this.currentTurnColour === Colour.WHITE) this.currentTurnColour = Colour.BLACK;
        else if (this.currentTurnColour === Colour.BLACK) this.currentTurnColour = Colour.WHITE;
    }
}

export default TurnManager;