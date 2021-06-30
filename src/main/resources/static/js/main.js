//region Imports
import { boardLength, letters, TurnManager, Point, Square, rgbToHex, fillGridItem } from "./utils.js";
//endregion

//region Global Constants
//region Board-Square Colours
const lightSquareColour = "#EEEED2";
const darkSquareColour = "#769656";
const lightSquareRedColour = "#F43E42";
const darkSquareRedColour = "#E83536";
const lightSquareSelectedColour = "#0073FF";
const darkSquareSelectedColour = "#005FD4";
//endregion
//region Game-Tracking Variables
const turnManager = new TurnManager();
let isFirstSquareClicked = false;
let $clickedSquare = null;
let clickedSquareColour = null;
let selectedSquares = null;
let highlightedSquaresNames = [];
//endregion
//endregion

//region Event Handlers
const onClickBoardSquare = (event) => {
    if (event.button === 0) {
        onLeftClickBoardSquare(event);
    }
    else if (event.button === 2) {
        onRightClickBoardSquare(event);
    }
};
const onLeftClickBoardSquare = (event) => {
    resetAllSquareRedHighlighting();
    let $target = $(event.target);
    let targetColour = rgbToHex($target.css("background-color")).toUpperCase();
    if (isFirstSquareClicked === false) onPieceSelect($target, targetColour);
    else if (isFirstSquareClicked === true) onMoveSelect($target);
};
const onPieceSelect = ($target, targetColour) => {
    //Guard clause for first square clicked not having a piece on it to move
    if ($target.css("background-image") === "none") return;

    //Guard clause for first square clicked having a piece not allowed to move this turn
    if ($target.data("piece-colour") !== turnManager.getCurrentTurnColour().description) return;

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
};
const onMoveSelect = ($target) => {
    //Execute move only if both squares have opposite colours of pieces and the move is legal
    if ($clickedSquare.data("piece-colour") !== $target.data("piece-colour") &&
        selectedSquares.includes($target.prop("id"))) {
        let clickedSquareName = $clickedSquare.prop("id");
        let clickedSquarePieceType = $clickedSquare.data("piece-type");
        let targetSquareName = $target.prop("id");

        //Check if this is a castling move
        let kingMoveHorizontalDist = Point.getHorizontalDistOnSamePlane(clickedSquareName, targetSquareName);
        if ($clickedSquare.data("piece-type") === "king" && Math.abs(kingMoveHorizontalDist) === 2) {
            performCastling(clickedSquareName, kingMoveHorizontalDist / Math.abs(kingMoveHorizontalDist));
        }

        //Check if this is an en passant move
        let pawnMoveVerticalDist = Point.getVerticalDist(clickedSquareName, targetSquareName);
        let pawnMoveHorizontalDist = Point.getHorizontalDist(clickedSquareName, targetSquareName);
        if (Math.abs(pawnMoveVerticalDist) === 1 && Math.abs(pawnMoveHorizontalDist) === 1) {
            //Then this pawn move is a diagonal capture
            if ($target.data("piece-type") === undefined && $target.data("piece-colour") === undefined) {
                //Then there is no actual piece to capture at this square, meaning en passant was performed
                performEnPassant($clickedSquare, pawnMoveHorizontalDist);
            }
        }

        //Check if this is a promotion move
        //TODO: For now, only promoting to queen
        let targetSquareRowNum = new Square(targetSquareName).getSquareNameNumber();
        if (clickedSquarePieceType === "pawn" && (targetSquareRowNum === 1 || targetSquareRowNum === boardLength)) {
            performPromotion($clickedSquare, "queen");
        }

        //Execute move
        executePieceMove($clickedSquare, $target);
    }
    resetFirstSquareSelection();
};
const onRightClickBoardSquare = (event) => {
    let $target = $(event.target);
    let targetColour = rgbToHex($target.css("background-color")).toUpperCase();

    //Redden
    if (targetColour === lightSquareColour) {
        $target.css("background-color", lightSquareRedColour);
        addToHighlightedSquares($target);
    }
    if (targetColour === darkSquareColour) {
        $target.css("background-color", darkSquareRedColour);
        addToHighlightedSquares($target);
    }

    //Un-Redden
    if (targetColour === lightSquareRedColour) {
        $target.css("background-color", lightSquareColour);
        removeFromHighlightedSquares($target);
    }
    if (targetColour === darkSquareRedColour) {
        $target.css("background-color", darkSquareColour);
        removeFromHighlightedSquares($target);
    }

    //Display in text box
    $("#squareNameDisplay").val($target.prop("id"));
};
//endregion
//region Square Selection Functions
const executeFirstPieceSelection = (listOfLegalMovesForPiece) => {
    selectedSquares = listOfLegalMovesForPiece;
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
const executePieceMove = ($firstSquare, $secondSquare, moveDirectlyMade = true) => {
    //Transfer piece image
    $secondSquare.css("background-image", $firstSquare.css("background-image"));
    $firstSquare.css("background-image", "none");

    //Transfer piece data properties
    $secondSquare.data("piece-colour", $firstSquare.data("piece-colour"));
    $secondSquare.data("piece-type", $firstSquare.data("piece-type"));
    $firstSquare.removeData("piece-colour");
    $firstSquare.removeData("piece-type");

    if (moveDirectlyMade) {
        //Display the square moved from and the square moved to
        $("#squareNameDisplay").val(`${$firstSquare.prop("id")} - ${$secondSquare.prop("id")}`);

        //Switch turn colour
        turnManager.switchCurrentTurnColour();

        //Communicate the two selected squares to rest controller
        $.ajax({
            url: "/ReadMove",
            contentType: "application/x-www-form-urlencoded",
            dataType: "text",
            type: "POST",
            data: { firstSquare: $firstSquare.prop("id"), secondSquare: $secondSquare.prop("id") },
            success: displayGameStatusMessage
        });
    }
};
//endregion
//region Side Effect Functions
const performCastling = (squareNameOfKing, dir) => {
    //OVERVIEW: direction is positive if king is castling to the right, and negative if castling to the left

    //For iterating through squares until rook is found
    let kingLocation = new Point(squareNameOfKing);
    let magnitude = 0;
    let squareNameToMoveRook = kingLocation.getRelativeSquareName(++magnitude * dir, 0);
    let $squareToMoveRook = $(`#${squareNameToMoveRook}`);

    //Find rook to castle with and move it
    let rookMoved = false;
    while (!rookMoved) {
        //Get current square details
        let currentSquareName = kingLocation.getRelativeSquareName(++magnitude * dir, 0);
        let $currentSquare = $(`#${currentSquareName}`);
        let currentPieceType = $currentSquare.data("piece-type");

        //Check if current square has rook to castle with
        if (currentPieceType === "rook") {
            executePieceMove($currentSquare, $squareToMoveRook, false);
            rookMoved = true;
        }
    }
};
const performEnPassant = ($capturingPawnSquare, pawnMoveHorizontalDist) => {
    //Find adjacent square with pawn that was captured
    let capturingPawnSquareName = $capturingPawnSquare.prop("id");
    let capturingPawnPoint = new Point(capturingPawnSquareName);
    let capturedPawnSquareName = capturingPawnPoint.getRelativeSquareName(pawnMoveHorizontalDist, 0);
    let $capturedPawnSquare = $(`#${capturedPawnSquareName}`);

    //Remove piece image
    $capturedPawnSquare.css("background-image", "none");

    //Remove piece data properties
    $capturedPawnSquare.removeData("piece-colour");
    $capturedPawnSquare.removeData("piece-type");
};
const performPromotion = ($pawnSquare, toPromoteTo) => {
    //Declare details
    let colour = $pawnSquare.data("piece-colour");
    let pieceDetails = `${colour}_${toPromoteTo}`;

    //Change square data and background image to reflect the new piece in it
    $pawnSquare.data("piece-colour", colour);
    $pawnSquare.data("piece-type", toPromoteTo);
    $pawnSquare.css("background-image", `url(../images/${pieceDetails}.png)`);
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
const displayGameStatusMessage = (gameStatus) => {
    let message = "";
    switch (gameStatus) {
        case "Checkmate":
            message = "Checkmate!";
            break;
        case "Stalemate":
            message = "Stalemate...";
            break;
        case "Draw":
            message = "Draw...";
            break;
        default:
            message = "Game Continues";
            break;
    }
    $("#gameResult").text(message);
};
const addToHighlightedSquares = ($target) => {
    //Append to list of red-highlighted squares
    highlightedSquaresNames.push($target.prop("id"));
};
const removeFromHighlightedSquares = ($target) => {
    //Remove from list of red-highlighted squares
    highlightedSquaresNames = highlightedSquaresNames.filter((squareName) => squareName !== $target.prop("id"));
};
const resetAllSquareRedHighlighting = () => {
    //Iterate through list of red-highlighted square names and un-redden the corresponding square on the board
    for (let i = 0; i < highlightedSquaresNames.length; i++) {
        let squareName = highlightedSquaresNames[i];
        let $highlightedSquare = $(`#${squareName}`);
        let highlightedSquareColour = rgbToHex($highlightedSquare.css("background-color")).toUpperCase();
        if (highlightedSquareColour === lightSquareRedColour) {
            $highlightedSquare.css("background-color", lightSquareColour);
        }
        if (highlightedSquareColour === darkSquareRedColour) {
            $highlightedSquare.css("background-color", darkSquareColour);
        }
    }
    //Clear the list of highlighted squares (because they're no longer highlighted, obviously)
    highlightedSquaresNames = [];
}
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
    //Initialize board's column property and disable right-click context menu
    let $mainGrid = $("#mainGrid");
    $mainGrid.css("grid-template-columns", `repeat(${boardLength}, 1fr)`);
    $mainGrid.on("contextmenu", e => e.preventDefault());
    //endregion

    //region Dynamically insert board squares
    let doLightSquare = true;
    for (let y = boardLength; 0 < y; y--) {
        for (let x = 0; x < boardLength; x++) {
            //Initialize square
            const gridItemTemplate = "<div class='gridItem'></div>";
            let $gridItem = $(gridItemTemplate);

            //Set square name and colour
            $gridItem.prop("id", letters[x] + y);
            $gridItem.css("background-color", doLightSquare ? lightSquareColour : darkSquareColour);

            //Flip the colour for the next square
            doLightSquare = doLightSquare === false;

            //Assign event handlers to grid items
            $gridItem.on("mousedown", onClickBoardSquare);

            //Set background image properties to square and add pieces as images
            fillGridItem($gridItem, y, x);

            //Add square to main grid (chess board)
            $mainGrid.append($gridItem);
        }
        //Flip the colour for the next square
        doLightSquare = doLightSquare === false;
    }
    //endregion
});