//region Global Constants
const gridItemTemplate = "<div class='gridItem'></div>";
const gridLength = 8;
const letters = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'];   //Please make this array automatic this is horrendous
//region Colours
const lightSquareColour = "#EEEED2";
const darkSquareColour = "#769656";
const lightSquareRedColour = "#F43E42";
const darkSquareRedColour = "#E83536";
const lightSquareSelectedColour = "#0073FF";
const darkSquareSelectedColour = "#005FD4";
//endregion
//region Ranks of Pieces
const blackFirstRank = ["black_rook", "black_knight", "black_bishop", "black_queen", "black_king",
    "black_bishop", "black_knight", "black_rook"];
const blackSecondRank = ["black_pawn", "black_pawn", "black_pawn", "black_pawn", "black_pawn",
    "black_pawn", "black_pawn", "black_pawn"];
const whiteSecondRank = ["white_pawn", "white_pawn", "white_pawn", "white_pawn", "white_pawn",
    "white_pawn", "white_pawn", "white_pawn"];
const whiteFirstRank = ["white_rook", "white_knight", "white_bishop", "white_queen", "white_king",
    "white_bishop", "white_knight", "white_rook"];
//endregion
//region Game-Tracking Variables
let isFirstSquareClicked = false;
let $clickedSquare = null;
let clickedSquareColour = null;
//endregion
//endregion

//region Event Handlers
const onClickBoardSquare = (event) => {
    let $target = $(event.target);
    let targetColour = rgbToHex($target.css("background-color")).toUpperCase();
    if (isFirstSquareClicked === false) {
        //Guard clause for if first square clicked not having a piece on it to move
        if ($target.css("background-image") === "none") return;
        clickedSquareColour = targetColour;
        if (clickedSquareColour === lightSquareColour) $target.css("background-color", lightSquareSelectedColour);
        if (clickedSquareColour === darkSquareColour) $target.css("background-color", darkSquareSelectedColour);
        $clickedSquare = $target;
        isFirstSquareClicked = true;
    }
    else if (isFirstSquareClicked === true) {
        //Guard clause for if piece-to-be-captured is of same colour a piece capturing
        if ($clickedSquare.data("colour") === $target.data("colour")) return;
        //Change back the colour of the first clicked square
        clickedSquareColour = rgbToHex($clickedSquare.css("background-color")).toUpperCase();
        if (clickedSquareColour === lightSquareSelectedColour) $clickedSquare.css("background-color", lightSquareColour);
        if (clickedSquareColour === darkSquareSelectedColour) $clickedSquare.css("background-color", darkSquareColour);
        //Execute move
        executePieceMove($clickedSquare, $target);
        isFirstSquareClicked = false;
    }
};
const onDoubleClickBoardSquare = (event) => {
    let $target = $(event.target);
    let targetColour = rgbToHex($target.css("background-color")).toUpperCase();
    //Redden
    if (targetColour === lightSquareColour) $target.css("background-color", lightSquareRedColour);
    if (targetColour === darkSquareColour) $target.css("background-color", darkSquareRedColour);
    //Un-Redden
    if (targetColour === lightSquareRedColour) $target.css("background-color", lightSquareColour);
    if (targetColour === darkSquareRedColour) $target.css("background-color", darkSquareColour);
    $.ajax({
        url: "/ReadSquare",
        contentType: "application/x-www-form-urlencoded",
        dataType: "json",
        type: "POST",
        data: { squareName: event.target.id },
        success: function(response) {
            $("#squareNameDisplay").val(response.squareName);
        }
    });
};
//endregion
//region Other Functions
const fillRows = ($gridItem, iRow, iColumn) => {
    $gridItem.css("background-size", "cover");
    $gridItem.css("background-position", "center");
    $gridItem.css("background-repeat", "no-repeat");
    let pieceDetails = "";

    if (iRow === gridLength) pieceDetails = blackFirstRank[iColumn];
    else if (iRow === gridLength - 1) pieceDetails = blackSecondRank[iColumn];
    else if (iRow === 2) pieceDetails = whiteSecondRank[iColumn];
    else if (iRow === 1) pieceDetails = whiteFirstRank[iColumn];

    $gridItem.data("colour", pieceDetails.substring(0, 5));
    $gridItem.data("type", pieceDetails.substring(6));
    $gridItem.css("background-image", `url(../images/${pieceDetails}.png)`);
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
const executePieceMove = ($firstSquare, $secondSquare) => {
    //Transfer piece image
    $secondSquare.css("background-image", $firstSquare.css("background-image"));
    $firstSquare.css("background-image", "none");
    //Transfer piece data properties
    $secondSquare.data("colour", $firstSquare.data("colour"));
    $secondSquare.data("type", $firstSquare.data("type"));
    $firstSquare.removeData();
    //Communicate the two selected squares to rest controller
    $.ajax({
        url: "/ReadMove",
        contentType: "application/x-www-form-urlencoded",
        dataType: "json",
        type: "POST",
        data: { firstSquare: $firstSquare.prop("id"), secondSquare: $secondSquare.prop("id") },
        success: function(response) {
            $("#squareNameDisplay").val(`${response.firstSquare} - ${response.secondSquare}`);
        }
    });
};
//endregion

$(function() {
    //region Initialize page properties
    let $mainGrid = $("#mainGrid");
    $mainGrid.css("grid-template-columns", `repeat(${gridLength}, 1fr)`);
    //endregion

    //region Initialize Localbase database and initialize default entry
    let db = new Localbase("db");
    db.collection("games").add({ id: 0 });
    const savedNum = localStorage.getItem("num");
    if (savedNum !== null) {
        $("#num").val(savedNum);
    }
    //endregion

    //region Dynamically insert elements
    let doLightSquare = false;
    for (let i = gridLength; 0 < i; i--) {
        //Flip the colour for the next square
        doLightSquare = doLightSquare === false;
        for (let j = 0; j < gridLength; j++) {
            //Initialize square
            let $gridItem = $(gridItemTemplate);
            //Set square coordinate name and colour
            $gridItem.prop("id", letters[j] + i);
            $gridItem.css("background-color", doLightSquare ? lightSquareColour : darkSquareColour);
            //Flip the colour for the next square
            doLightSquare = doLightSquare === false;
            //Assign event handlers to grid items
            $gridItem.on("click", onClickBoardSquare);
            $gridItem.on("dblclick", onDoubleClickBoardSquare);
            //Set background image properties to square and add pieces as images
            fillRows($gridItem, i, j);
            //Add square to main grid (chess board)
            $mainGrid.append($gridItem);
        }
    }
    //endregion
});