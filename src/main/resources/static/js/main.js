//region Global Constants
const gridItemTemplate = "<div class='gridItem'></div>";
const gridLength = 8;
const letters = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'];   //Please make this array automatic this is horrendous
const lightSquareColour = "#EEEED2";
const darkSquareColour = "#769656";
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
//endregion

//region Event Handlers
const onClickBoardSquare = (event) => {
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
}
const onClickSubmitNum = () => {
    let num = $("#num").val();
    $.ajax({
        url: "/AffectNum",
        contentType: "application/x-www-form-urlencoded",
        dataType: "json",
        type: "POST",
        data: { num: num },
        success: function(response) {
            $("#num").val(response.num);
            localStorage.setItem("num", response.num);
        }
    });
};
//endregion
//region Other Functions
const fillRows = (gridItem, iRow, iColumn) => {
    if (iRow === gridLength) {
        gridItem.css("background-image", `url(../images/${blackFirstRank[iColumn]}.png)`);
        gridItem.css("background-size", "cover");
        gridItem.css("background-position", "center");
        gridItem.css("background-repeat", "no-repeat");
    }
    if (iRow === gridLength - 1) {
        gridItem.css("background-image", `url(../images/${blackSecondRank[iColumn]}.png)`);
        gridItem.css("background-size", "cover");
        gridItem.css("background-position", "center");
        gridItem.css("background-repeat", "no-repeat");
    }
    if (iRow === 2) {
        //`../images/${blackFirstRank[j]}.png`
        gridItem.css("background-image", `url(../images/${whiteSecondRank[iColumn]}.png)`);
        gridItem.css("background-size", "cover");
        gridItem.css("background-position", "center");
        gridItem.css("background-repeat", "no-repeat");
    }
    if (iRow === 1) {
        //`../images/${blackFirstRank[j]}.png`
        gridItem.css("background-image", `url(../images/${whiteFirstRank[iColumn]}.png)`);
        gridItem.css("background-size", "cover");
        gridItem.css("background-position", "center");
        gridItem.css("background-repeat", "no-repeat");
    }
};
//endregion

$(function() {
    //region Initialize page properties
    $("#submitButton").on("click", onClickSubmitNum);
    $("#mainGrid").css("grid-template-columns", `repeat(${gridLength}, 1fr)`);
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
        //Flips the colour
        doLightSquare = doLightSquare === false;
        for (let j = 0; j < gridLength; j++) {
            let $gridItem = $(gridItemTemplate);
            $gridItem.prop("id", letters[j] + i);
            $gridItem.css("background-color", doLightSquare ? lightSquareColour : darkSquareColour);
            //Flips the colour
            doLightSquare = doLightSquare === false;
            //Assign click handler onClickBoardSquare to grid items
            $gridItem.on("click", onClickBoardSquare);
            //Add pieces according to rows
            fillRows($gridItem, i, j);
            //Test text to make it not empty
            $("#mainGrid").append($gridItem);
        }
    }
    //endregion
});