const handleRestControllerResponse = (response, status, xhr) => {
    console.log("handleRestControllerResponse() runs");
    $("#num").text(response.num);
};

const sendToRestController = () => {
    alert("sentToRestController() runs");
    $.ajax({
        url: "/th-spring-integration/spring/ApplyMove",
        contentType: "application/x-www-form-urlencoded",
        dataType: "json",
        success: handleRestControllerResponse,
        error: function(xhr, status, error) {
            alert("Bruh why does the rest controller request not work");
        }
    });
};

$(function() {
    $("#submitButton").on("click", sendToRestController);
    //DEBUGGING
    console.log("Javascript runs at least!");
});