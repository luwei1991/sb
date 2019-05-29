//jsBridge
//注册事件监听
function connectWebViewJavascriptBridge(callback) {
    if (window.WebViewJavascriptBridge) {
        callback(WebViewJavascriptBridge)
    } else {
        document.addEventListener(
            'WebViewJavascriptBridgeReady'
            , function() {
                callback(WebViewJavascriptBridge)
            },
            false
        );
    }
}
//注册回调函数，第一次连接时调用 初始化函数
connectWebViewJavascriptBridge(function(bridge) {
    bridge.init(function(message, responseCallback) {
        alert('默认接收收到来自Java数据： ' + message);
        var responseData = '默认回传数据';
        responseCallback(responseData);
    });

    bridge.registerHandler("functionInJs", function(data, responseCallback) {
        bridgeLog('指定接收收到来自Java数据： ' + data);
        var responseData = '指定接收收到来自Java的数据，回传数据给你';
        responseCallback(responseData);
    });
})



// function hideSubmit(){
// 	document.getElementById("submit").style.display = "none"
// }

// function showSubmit(){
// 	document.getElementById("submit").style.display = "block"
// }



$(function(){
    
    //显示当前时间
    var date = new Date();
    var dateParse = {
        year: date.getFullYear(),
        month: date.getMonth(),
        day: date.getDate()
    }
    $('.year').val(dateParse.year)
    $('.month').val(dateParse.month)
    $('.day').val(dateParse.day)
    

    //html转图片
    function htmlCanvas(cb){
        var targetDom = $(".page");  
        var copyDom = targetDom.clone();  
        copyDom.width(targetDom.width() + "px");  
        copyDom.height(targetDom.height() + "px");  
        $('body').append(copyDom);
        html2canvas(copyDom, {
            onrendered: function(canvas){
                $(".page")[1].remove()
                var img = canvas.toDataURL();
                cb && cb(img)
            }
        })
    }

    // var register_js = {
    //     send: function(str){
    //         console.log(JSON.parse(str))
    //     }
    // }

    //提交
    $("#submit").click(function(e){
        // 序列化表单
        var fromData = $("form").serialize();
        
        //发送给安卓
        // register_js.send(JSON.stringify({
	    //     type: postType,
	    //     data: fromData
	    // }))

	    // 发送服务器
        // $.ajax({
        //     url: '',
        //     method: 'post',
        //     data:{
        //     from: fromData,
        //     img: base64
        //     }
        // })

	    //网页截图转成base64
		htmlCanvas(function(base64){
			console.log("截图base64:")
			console.log(base64)
		})
    })
})