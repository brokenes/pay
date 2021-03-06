$.ajaxSetup({
    timeout : 30000,
    contentType : "application/x-www-form-urlencoded;charset=utf-8",
    complete : function(XMLHttpRequest, textStatus) {
        var sessionstatus = XMLHttpRequest.getResponseHeader("sessionstatus"); // 通过XMLHttpRequest取得响应头，sessionstatus，
        if (sessionstatus == "timeout") {
            var locationURL = XMLHttpRequest.getResponseHeader("locationURL"); // 通过XMLHttpRequest取得响应头，转向登陆页面locationURL，
            // alert("登陆过期，请重新登录");
            // 如果超时就处理 ，指定要跳转的页面
            // layer.open({
            //     type: 1
            //     ,offset: 't' //具体配置参考：offset参数项
            //     ,content: '<div style="padding: 15px 30px;">登陆超时，请重新登陆</div>'
            //     ,btn: '确定'
            //     ,btnAlign: 'c' //按钮居中
            //     ,shade: 0 //不显示遮罩
            //     ,yes: function(){
            //         layer.closeAll();
            //     }
            // });
            top.location.href = locationURL;
        }
    },
    error : function(jqXHR, textStatus, errorThrown) {
        switch (jqXHR.status) {
        case (500):
            alert("服务器系统内部错误");
            break;
        case (401):
            alert("未登录");
            break;
        case (403):
            alert("无权限执行此操作");
            break;
        case (408):
            alert("请求超时");
            break;
        default:
            alert("未知错误");
        }
    }
})
