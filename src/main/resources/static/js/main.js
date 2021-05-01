//Global constants
const gridItemTemplate = "<div class='border border-3 border-dark m-1 bg-success'></div>";
const gridLength = 8;
const letters = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'];   //Please make this array automatic this is horrendous

const sendRequest = () => {
    let num = $("#num").val();
    $.ajax({
        url: "/AffectNum",
        contentType: "application/x-www-form-urlencoded",
        dataType: "json",
        type: "POST",
        data: { num: num },
        success: handleResponse,
        error: function(xhr, status, error) {
            console.log("Rest controller request does not work");
        }
    });
};
const handleResponse = (response, status, xhr) => {
    $("#num").val(response.num);
    localStorage.setItem("num", response.num);
};

$(function() {
    //Initialize page properties
    $("#submitButton").on("click", sendRequest);
    $("#mainGrid").css("grid-template-columns", `repeat(${gridLength}, 1fr)`);

    //Initialize Localbase database and initialize default entry
    let db = new Localbase("db");
    db.collection("games").add({ id: 0 });
    const savedNum = localStorage.getItem("num");
    if (savedNum !== null) {
        $("#num").val(savedNum);
    }

    //Dynamically insert elements
    for (let i = gridLength; 0 < i; i--) {
        for (let j = 0; j < gridLength; j++) {
            let $gridItem = $(gridItemTemplate);
            $gridItem.text(letters[j] + i);
            $("#mainGrid").append($gridItem);
        }
    }
});