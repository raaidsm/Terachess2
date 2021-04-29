const handleRestControllerResponse = (response, status, xhr) => {
    console.log("handleRestControllerResponse() runs");
    $("#num").text(response.num);
};

const sendToRestController = () => {
    let num = $("#num").val();
    $.ajax({
        url: "/th-spring-integration/spring/ApplyMove",
        contentType: "application/x-www-form-urlencoded",
        dataType: "json",
        type: "post",
        data: { num: num },
        success: handleRestControllerResponse,
        error: function(xhr, status, error) {
            console.log("Rest controller request does not work");
        }
    });
};

$(function() {
    $("#submitButton").on("click", sendToRestController);
    //DEBUGGING
    console.log("Javascript runs at least!");
});