const handleRestControllerResponse = (result, status, xhr) => {
    $("#num").text(result.num);
};

const sendToRestController = () => {
    $.ajax({
        url: "/th-spring-integration/spring/ApplyMove",
        contentType: "application/x-www-form-urlencoded",
        dataType: "json",
        success: handleRestControllerResponse()
    });
};

$(function() {
    $("#submitButton").on("click", sendToRestController);
    //DEBUGGING
    console.log("Javascript runs at least!");
});