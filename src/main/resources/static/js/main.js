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
        //Guard clause for first square clicked not having a piece on it to move
        if ($target.css("background-image") === "none") return;
        //Save selected square data to game-tracking variables
        $clickedSquare = $target;
        clickedSquareColour = targetColour;
        //Apply selected colour
        if (clickedSquareColour === lightSquareColour) $target.css("background-color", lightSquareSelectedColour);
        if (clickedSquareColour === darkSquareColour) $target.css("background-color", darkSquareSelectedColour);
        //Send first selected square to Java code
        $.ajax({
            url: "/ReadFirstPieceSelection",
            contentType: "application/x-www-form-urlencoded",
            dataType: "json",
            type: "POST",
            data: { firstSquare: $target.prop("id"), secondSquare: null },
            success: function(response) {
                //TODO: Display all the legal moves on the board
            }
        });
        //First square has been successfully clicked
        isFirstSquareClicked = true;
    }
    else if (isFirstSquareClicked === true) {
        //Execute move only if both squares don't have the same colour of piece
        if ($clickedSquare.data("colour") !== $target.data("colour")) executePieceMove($clickedSquare, $target);
        resetFirstSquareSelection();
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
    $("#squareNameDisplay").val($target.prop("id"));
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
const executePieceMove = ($firstSquare, $secondSquare) => {
    //Transfer piece image
    $secondSquare.css("background-image", $firstSquare.css("background-image"));
    $firstSquare.css("background-image", "none");
    //Transfer piece data properties
    $secondSquare.data("colour", $firstSquare.data("colour"));
    $secondSquare.data("type", $firstSquare.data("type"));
    $firstSquare.removeData("colour");
    $firstSquare.removeData("type");
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
const resetFirstSquareSelection = () => {
    //Clear selection colour
    clickedSquareColour = rgbToHex($clickedSquare.css("background-color")).toUpperCase();
    if (clickedSquareColour === lightSquareSelectedColour) $clickedSquare.css("background-color", lightSquareColour);
    if (clickedSquareColour === darkSquareSelectedColour) $clickedSquare.css("background-color", darkSquareColour);
    //TODO: Clear legal move colours off all squares
    //Clear game-tracking variables
    $clickedSquare = null;
    clickedSquareColour = null;
    isFirstSquareClicked = false;
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
    for (let y = gridLength; 0 < y; y--) {
        //Flip the colour for the next square
        doLightSquare = doLightSquare === false;
        for (let x = 0; x < gridLength; x++) {
            //Initialize square
            let $gridItem = $(gridItemTemplate);
            //Set square coordinate name and colour
            $gridItem.prop("id", letters[x] + y);
            $gridItem.data("coordinate", `${x}-${y-1}`);
            $gridItem.css("background-color", doLightSquare ? lightSquareColour : darkSquareColour);
            //Flip the colour for the next square
            doLightSquare = doLightSquare === false;
            //Assign event handlers to grid items
            $gridItem.on("click", onClickBoardSquare);
            $gridItem.on("dblclick", onDoubleClickBoardSquare);
            //Set background image properties to square and add pieces as images
            fillRows($gridItem, y, x);
            //Add square to main grid (chess board)
            $mainGrid.append($gridItem);
        }
    }
    //endregion
});