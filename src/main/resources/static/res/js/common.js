function logout() {
    let token = localStorage.getItem("login-token");
    console.log(token);
    $.ajax({
        headers: {
            'token': token
        },
        type:"GET",                //请求方式
        url:"/user/logout",                 //路径
        async:false,             //是否异步
        dataType:"json",        //返回数据的格式
        success:function(res){  //成功的回调函数
            console.log("跳转页面");
        }
    })
}
