"use strict";

//region Imports
import {boardLength, letters, rgbToHex, fillGridItem} from "./utils.js";
import TurnManager from "./turn_manager.js";
import Point from "./point.js";
import Square from "./square.js";
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
//region SockJS and Stomp Variables
let stompClient = null;
let username = null;
let userColour = null;
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

//region SockJS and StompJS Communication Functions
const connect = (event) => {
    username = $("#username").val().trim();

    if (username) {
        const socket = new SockJS("/generic-endpoint");
        stompClient = Stomp.over(socket);
        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}
const onConnected = () => {
    stompClient.subscribe('/topic/public', onMessageReceived)
    stompClient.send("/app/new-user",
        {},
        JSON.stringify({sender: username, type: "CONNECT"})
    );
}
const onError = (error) => {
    alert("Something went wrong with the websockets..." + error);
}
const sendMessage = (event) => {
    const messageInput = $("#sendMessage");
    const messageContent = messageInput.val().trim();

    if (messageContent && stompClient) {
        const chatMessage = {
            type: "CHAT",
            sender: username,
            content: messageContent
        }
        stompClient.send("/app/message", {}, JSON.stringify(chatMessage));
    }
    event.preventDefault();
}
const onMessageReceived = (payload) => {
    const receivedMessage = JSON.parse(payload.body);
    const messageContent = receivedMessage.content;

    //Execute the communicated move only if sent by another user
    if (receivedMessage.sender !== username && receivedMessage.type === "MOVE") {
        let moves = messageContent.split("-");
        makeMove(...moves);
    }

    //Display messages from opponents
    if (receivedMessage.sender !== username && (
        receivedMessage.type === "CHAT" ||
        receivedMessage.type === "MOVE")) {
        $("#receiveMessage").val(messageContent);
    }

    //Display connection messages
    if (
        receivedMessage.type === "CONNECT" ||
        receivedMessage.type === "REJECT" ||
        receivedMessage.type === "DISCONNECT") {
        $("#receiveMessage").val(messageContent);
    }

    //If the current user was just connected, save their assigned colour
    if (receivedMessage.sender === username && receivedMessage.type === "CONNECT") {
        userColour = receivedMessage.userColour;
    }
}
const makeMove = (move1, move2) => {
    //This function should ideally be made to integrate with the existing ways to execute a move at some point
    let $move1Square = $("#" + move1);
    let $move2Square = $("#" + move2);
    executePieceMove($move1Square, $move2Square, false);
};
//endregion
//region Board Event Handlers
const onClickBoardSquare = (event) => {
    if (event.button === 0) {
        //Guard clause for users not being able to move pieces this turn
        //DEBUGGING: Commented out
        //if (turnManager.currentTurnColour.description !== userColour) return;
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
const executePieceMove = ($firstSquare, $secondSquare,
                          isDirectlyMadeMove = true, isMainEffect = true) => {
    if (isMainEffect) {
        let clickedSquareName = $firstSquare.prop("id");
        let clickedSquarePieceType = $firstSquare.data("piece-type");
        let targetSquareName = $secondSquare.prop("id");

        //Check if this is a castling move
        let kingMoveHorizontalDist = Point.getHorizontalDistOnSamePlane(clickedSquareName, targetSquareName);
        if ($firstSquare.data("piece-type") === "king" && Math.abs(kingMoveHorizontalDist) === 2) {
            performCastling(clickedSquareName, kingMoveHorizontalDist / Math.abs(kingMoveHorizontalDist));
        }

        //Check if this is an en passant move
        let pawnMoveVerticalDist = Point.getVerticalDist(clickedSquareName, targetSquareName);
        let pawnMoveHorizontalDist = Point.getHorizontalDist(clickedSquareName, targetSquareName);
        if ($firstSquare.data("piece-type") === "pawn" &&
            Math.abs(pawnMoveVerticalDist) === 1 && Math.abs(pawnMoveHorizontalDist) === 1) {
            //Then this pawn move is a diagonal capture
            if ($secondSquare.data("piece-type") === undefined && $secondSquare.data("piece-colour") === undefined) {
                //Then there is no actual piece to capture at this square, meaning en passant was performed
                performEnPassant($firstSquare, pawnMoveHorizontalDist);
            }
        }

        //Check if this is a promotion move
        //TODO: For now, only promoting to queen
        let targetSquareRowNum = new Square(targetSquareName).getSquareNameNumber();
        if (clickedSquarePieceType === "pawn" && (targetSquareRowNum === 1 || targetSquareRowNum === boardLength)) {
            performPromotion($firstSquare, "queen");
        }

        //Switch turn colour
        turnManager.switchCurrentTurnColour();
    }

    //Transfer piece image
    $secondSquare.css("background-image", $firstSquare.css("background-image"));
    $firstSquare.css("background-image", "none");

    //Transfer piece data properties
    $secondSquare.data("piece-colour", $firstSquare.data("piece-colour"));
    $secondSquare.data("piece-type", $firstSquare.data("piece-type"));
    $firstSquare.removeData("piece-colour");
    $firstSquare.removeData("piece-type");

    if (isDirectlyMadeMove) {
        //Display the square moved from and the square moved to
        $("#squareNameDisplay").val(`${$firstSquare.prop("id")} - ${$secondSquare.prop("id")}`);

        //Communicate the two selected squares to the other player
        if (stompClient) {
            const moveMade = {
                type: "MOVE",
                sender: username,
                content: `${$firstSquare.prop("id")}-${$secondSquare.prop("id")}`
            }
            stompClient.send("/app/move", {}, JSON.stringify(moveMade));
        }

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
            executePieceMove($currentSquare, $squareToMoveRook, false, false);
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
    $("#gameResult").val(message);
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

    //region Set event handlers for WebSocket communication
    $("#usernameForm").on("submit", connect);
    $("#messageForm").on("submit", sendMessage);
    //endregion
});