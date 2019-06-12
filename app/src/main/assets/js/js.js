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
        var fromData = $("form").serialize();
        responseCallback(JSON.stringify({
            type: postType,
            data: fromData
        }));
    });

    bridge.registerHandler("dataBackfill", function(data, responseCallback) {
        setTimeout(function(){
            dataFill(data)
            responseCallback('数据回填完成');
        }, 200)
    });
})


// 数据回填（根据标签类型填入相应数据）
function dataFillByType(els, data){
    var type = $(els[0]).attr('type');
    if(type == 'radio'){
        $(els).each(function(){
            if($(this).val() == data){
                $(this).click()
            }
        })
    }else{
        $(els[0]).val(decodeURIComponent(data))
    }
}

// 数据回填
function dataFill(data){
    data.split('&').forEach(item => {
        var d = item.match(/(.+)=(.+)/)
        if(d && d[1] && d[2]){
            dataFillByType($("[name='"+d[1]+"']"), d[2])
        }
    });
}

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
})



































// 测试用例
function d(data){
    setTimeout(function(){
        dataFill(data)
        console.log('数据回填完成')
    }, 200)
}

function c(){
    var fromData = $("form").serialize();
    console.log({
        type: postType,
        data: fromData
    })
    console.log(fromData)
}


//$(function(){
    //html转图片
    // function htmlCanvas(cb){
    //     var targetDom = $(".page");  
    //     var copyDom = targetDom.clone();  
    //     copyDom.width(targetDom.width() + "px");  
    //     copyDom.height(targetDom.height() + "px");  
    //     $('body').append(copyDom);
    //     html2canvas(copyDom, {
    //         onrendered: function(canvas){
    //             $(".page")[1].remove()
    //             var img = canvas.toDataURL();
    //             cb && cb(img)
    //         }
    //     })
    // }

    //提交
    // $("#submit").click(function(e){
    //     // 序列化表单
    //     var fromData = $("form").serialize();
        
    //     //发送给安卓
    //     // register_js.send(JSON.stringify({
	//     //     type: postType,
	//     //     data: fromData
	//     // }))

	//     // 发送服务器
    //     // $.ajax({
    //     //     url: '',
    //     //     method: 'post',
    //     //     data:{
    //     //     from: fromData,
    //     //     img: base64
    //     //     }
    //     // })

	//     //网页截图转成base64
	// 	htmlCanvas(function(base64){
	// 		console.log("截图base64:")
	// 		console.log(base64)
	// 	})
    // })
//})