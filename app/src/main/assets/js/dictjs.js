$(document).ready(function(){
$(".contactAddress").click(function(){
        $(".taskList").css("display", "block");
        $("#modal").css("display", "block");
        $("#cover").css("display", "block");
        $(".title").text("地区选择");
        $(".dictContent").css("display", "block");
        $(".tableTask").empty();
        $("#detailAddress").bind('input propertychange',function () {
                      var detailAddress=$(this).val();
                      $(".addressName").text(detailAddress);
                        });

})

})