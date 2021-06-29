//region Constants
const boardLength = 8;
const letters = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'];   //Please make this array automatic this is horrendous
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
class Point {
    constructor(squareName) {
        this.x = 0;
        this.y = 0;
        if (squareName == null) return;

        let letterRep = squareName.substring(0, 1);
        let numberRep = parseInt(squareName.substring(1));
        this.x = letters.indexOf(letterRep) + 1;
        this.y = numberRep;
    }

    static getHorizontalDist(squareName1, squareName2) {
        let point1 = new Point(squareName1);
        let point2 = new Point(squareName2);

        if (point1.y === point2.y) {
            return point2.x - point1.x;
        }
        else return null
    }

    getRelativePoint(shiftX, shiftY) {
        if (this.x === 0 && this.y === 0) return null;
        let tempX = this.x + shiftX;
        let numberRep = this.y + shiftY;

        if (boardLength < tempX || boardLength < numberRep) return null;
        if (tempX < 1 || numberRep < 1) return null;

        return letters[tempX - 1] + numberRep;
    }
}
//endregion

//region Functions
const fillGridItem = ($gridItem, iRow, iColumn) => {
    //Set grid item's background properties
    $gridItem.css("background-size", "cover");
    $gridItem.css("background-position", "center");
    $gridItem.css("background-repeat", "no-repeat");

    let pieceDetails = "";
    if (iRow === boardLength) pieceDetails = blackFirstRank[iColumn];
    else if (iRow === boardLength - 1) pieceDetails = blackSecondRank[iColumn];
    else if (iRow === 2) pieceDetails = whiteSecondRank[iColumn];
    else if (iRow === 1) pieceDetails = whiteFirstRank[iColumn];

    if (pieceDetails !== "" && pieceDetails !== "empty") {
        $gridItem.data("piece-colour", pieceDetails.substring(0, 5));
        $gridItem.data("piece-type", pieceDetails.substring(6));
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

export { boardLength, letters, Colour, TurnManager, Point, fillGridItem, rgbToHex };