//region Constants
const boardLength = 8;
const blackFirstRank = ["black_rook", "black_knight", "black_bishop", "black_queen", "black_king",
    "black_bishop", "black_knight", "black_rook"];
const blackSecondRank = ["black_pawn", "black_pawn", "black_pawn", "black_pawn", "black_pawn",
    "black_pawn", "black_pawn", "black_pawn"];
const whiteSecondRank = ["white_pawn", "white_pawn", "white_pawn", "white_pawn", "white_pawn",
    "white_pawn", "white_pawn", "white_pawn"];
const whiteFirstRank = ["white_rook", "white_knight", "white_bishop", "white_queen", "white_king",
    "white_bishop", "white_knight", "white_rook"];
//endregion

//region Enums
const Colour = Object.freeze({
    WHITE: Symbol("white"),
    BLACK: Symbol("black")
});
//endregion

//region Classes
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
//endregion

//region Functions
const fillGridItem = ($gridItem, iRow, iColumn) => {
    $gridItem.css("background-size", "cover");
    $gridItem.css("background-position", "center");
    $gridItem.css("background-repeat", "no-repeat");
    let pieceDetails = "";

    if (iRow === boardLength) pieceDetails = blackFirstRank[iColumn];
    else if (iRow === boardLength - 1) pieceDetails = blackSecondRank[iColumn];
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
//endregion

export { boardLength, Colour, TurnManager, fillGridItem, rgbToHex };