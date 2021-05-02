//region Global Constants
const gridItemTemplate = "<div class='gridItem'></div>";
const gridLength = 8;
const letters = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'];   //Please make this array automatic this is horrendous
let lightSquareColour = "#EEEED2";
let darkSquareColour = "#769656";
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
            //Test text to make it not empty
            $("#mainGrid").append($gridItem);
        }
    }
    //endregion
});