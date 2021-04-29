const handleResponse = (response, status, xhr) => {
    $("#num").val(response.num);
};

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

$(function() {
    //Initialize Localbase database and initialize default entry
    let db = new Localbase("db");
    db.collection("games").add({ id: 0 });

    $("#submitButton").on("click", sendRequest);
});