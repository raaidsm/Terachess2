//Enums
const Colour = Object.freeze({
    WHITE: Symbol("white"),
    BLACK: Symbol("black")
});

//Classes
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

//Functions
const fillRows = ($gridItem, iRow, iColumn) => {
    $gridItem.css("background-size", "cover");
    $gridItem.css("background-position", "center");
    $gridItem.css("background-repeat", "no-repeat");
    let pieceDetails = "";

    if (iRow === gridLength) pieceDetails = blackFirstRank[iColumn];
    else if (iRow === gridLength - 1) pieceDetails = blackSecondRank[iColumn];
    else if (iRow === 2) pieceDetails = whiteSecondRank[iColumn];
    else if (iRow === 1) pieceDetails = whiteFirstRank[iColumn];

    if (pieceDetails !== "") {
        $gridItem.data("colour", pieceDetails.substring(0, 5));
        $gridItem.data("type", pieceDetails.substring(6));
        $gridItem.css("background-image", `url(../images/${pieceDetails}.png)`);
    }
};
const rgbToHex = (col) => {
    if (col.charAt(0) === 'r')
    {
        col = col.replace('rgb(','').replace(')','').split(',');
        let r = parseInt(col[0], 10).toString(16);
        let g = parseInt(col[1], 10).toString(16);
        let b = parseInt(col[2], 10).toString(16);
        r = r.length === 1 ? '0' + r : r;
        g = g.length === 1 ? '0' + g : g;
        b = b.length === 1 ? '0' + b : b;
        return '#' + r + g + b;
    }
};

export { Colour, TurnManager, fillRows, rgbToHex };