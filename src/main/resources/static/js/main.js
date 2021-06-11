//region Imports
import { boardLength, Colour, TurnManager, rgbToHex, fillRows } from "./utils.js";
//endregion

//region Global Constants
//region Board Details
const gridItemTemplate = "<div class='gridItem'></div>";
const letters = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'];   //Please make this array automatic this is horrendous
//endregion
//region Colours
const lightSquareColour = "#EEEED2";
const darkSquareColour = "#769656";
const lightSquareRedColour = "#F43E42";
const darkSquareRedColour = "#E83536";
const lightSquareSelectedColour = "#0073FF";
const darkSquareSelectedColour = "#005FD4";
//endregion
//region Game-Tracking Variables
let isFirstSquareClicked = false;
let $clickedSquare = null;
let clickedSquareColour = null;
let selectedSquares = null;
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
            success: executeFirstPieceSelection
        });
        //First square has been successfully clicked
        isFirstSquareClicked = true;
    }
    else if (isFirstSquareClicked === true) {
        //Execute move only if both squares don't have the same colour of piece
        if ($clickedSquare.data("colour") !== $target.data("colour") &&
            selectedSquares.includes($target.prop("id")))
        {
            executePieceMove($clickedSquare, $target);
        }
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
//region Square Selection Functions
const executeFirstPieceSelection = (response) => {
    selectedSquares = response;
    for (let i = 0; i < selectedSquares.length; i++) {
        let $legalMoveSquare = $(`#${selectedSquares[i]}`);
        let legalMoveSquareColour = rgbToHex($legalMoveSquare.css("background-color")).toUpperCase();
        if (legalMoveSquareColour === lightSquareColour) {
            $legalMoveSquare.css("background-color", lightSquareSelectedColour);
        }
        else if (legalMoveSquareColour === darkSquareColour) {
            $legalMoveSquare.css("background-color", darkSquareSelectedColour);
        }
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
    //Display the square moved from and the square moved to
    $("#squareNameDisplay").val(`${$firstSquare.prop("id")} - ${$secondSquare.prop("id")}`);
    //Communicate the two selected squares to rest controller
    $.ajax({
        url: "/ReadMove",
        contentType: "application/x-www-form-urlencoded",
        dataType: "json",
        type: "POST",
        data: { firstSquare: $firstSquare.prop("id"), secondSquare: $secondSquare.prop("id") }
    });
};
//endregion
//region Helper Functions
const resetFirstSquareSelection = () => {
    //Clear selection colour off first selected piece
    clickedSquareColour = rgbToHex($clickedSquare.css("background-color")).toUpperCase();
    if (clickedSquareColour === lightSquareSelectedColour) $clickedSquare.css("background-color", lightSquareColour);
    if (clickedSquareColour === darkSquareSelectedColour) $clickedSquare.css("background-color", darkSquareColour);
    //Clear selection colour off all legal move squares
    for (let i = 0; i < selectedSquares.length; i++) {
        let $legalMoveSquare = $(`#${selectedSquares[i]}`);
        let legalMoveSquareColour = rgbToHex($legalMoveSquare.css("background-color")).toUpperCase();
        if (legalMoveSquareColour === lightSquareSelectedColour) {
            $legalMoveSquare.css("background-color", lightSquareColour);
        }
        if (legalMoveSquareColour === darkSquareSelectedColour) {
            $legalMoveSquare.css("background-color", darkSquareColour);
        }
    }
    //Clear game-tracking variables
    $clickedSquare = null;
    clickedSquareColour = null;
    isFirstSquareClicked = false;
};
//endregion

$(function() {
    //region Initialize Localbase database and initialize default entry (currently inactive)
    /* let db = new Localbase("db");
    db.collection("games").add({ id: 0 });
    const savedNum = localStorage.getItem("num");
    if (savedNum !== null) {
        $("#num").val(savedNum);
    } */
    //endregion

    //region Initialize page properties
    //Initialize board (mainGrid)
    let $mainGrid = $("#mainGrid");
    $mainGrid.css("grid-template-columns", `repeat(${boardLength}, 1fr)`);

    //Initialize TurnManager instance
    const turnManager = new TurnManager();
    //endregion

    //region Dynamically insert board squares
    let doLightSquare = true;
    for (let y = boardLength; 0 < y; y--) {
        for (let x = 0; x < boardLength; x++) {
            //Initialize square
            let $gridItem = $(gridItemTemplate);

            //Set square name and colour
            $gridItem.prop("id", letters[x] + y);
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
        //Flip the colour for the next square
        doLightSquare = doLightSquare === false;
    }
    //endregion
});