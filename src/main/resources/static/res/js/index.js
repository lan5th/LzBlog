function topBlog() {
    $.ajax({
        headers: {
            'token': localStorage.getItem("login-token")
        },
        type:"GET",                //请求方式
        url:"/index/topBlog",                 //路径
        async:false,             //是否异步
        dataType:"json",        //返回数据的格式
        success:function(res) {
            //渲染置顶博客
            let html = "";
            let list = res.data.list;
            let user = JSON.parse(localStorage.getItem('user'));
            if (list == null) {
                html = "<!-- 没有数据 -->\n" +
                    "   <div class=\"emptybox shadow\">\n" +
                    "       <p><i style=\"font-size:50px;color:#5fb878\" class=\"layui-icon\">&#xe69c;</i></p>\n" +
                    "       <p>暂时没有任何数据</p>\n" +
                    "   </div>"
            } else if (user != null && user.isAdmin == true) {
                //管理员列表
                for (let i in list) {
                    let detail = list[i];
                    html += "<div class=\"article shadow clearfix sr-listshow\">\n" +
                        "        <div class=\"article-right\">\n" +
                        "            <div class=\"article-title\">\n" +
                        "                <a href=\"/details/" + detail.id + "\">" + detail.blogName + "</a>\n" +
                        "                <a href=\"javascript:actionConfirm(" + detail.id + ", \'cancelTop\')\" style='font-size: 14px; float: right; color: #009688'>取消置顶</a>\n" +
                        "            </div>\n" +
                        "            <div class=\"article-abstract\">\n"  + detail.shortContent + "\n" +
                        "            </div>\n" +
                        "            <div class=\"article-footer\">\n" +
                        "                <span class=\"layui-hide-xs\"><i class=\"fa fa-tag\" aria-hidden=\"true\"></i>&nbsp;<a\n" +
                        "                        style=\"color:#009688\" href=\"\">" + detail.tagName + "\n" +
                        "                        </a></span>\n" +
                        "                <span><i class=\"fa fa-clock-o\" aria-hidden=\"true\"></i>&nbsp;" + detail.createTime + "</span>\n" +
                        "                <a class=\"read layui-btn layui-btn-xs layui-btn-normal layui-hide-xs\"\n" +
                        "                   href=\"/details/" + detail.id + "\" title=\"" + detail.blogName + "\">阅读全文</a>\n" +
                        "            </div>\n" +
                        "        </div>\n" +
                        "    </div>";
                }
            } else {
                //无权限列表
                for (let i in list) {
                    let detail = list[i];
                    html += "<div class=\"article shadow clearfix sr-listshow\">\n" +
                        "        <div class=\"article-right\">\n" +
                        "            <div class=\"article-title\">\n" +
                        "                <a href=\"/details/" + detail.id + "\">" + detail.blogName + "</a>\n" +
                        "            </div>\n" +
                        "            <div class=\"article-abstract\">\n"  + detail.shortContent + "\n" +
                        "            </div>\n" +
                        "            <div class=\"article-footer\">\n" +
                        "                <span class=\"layui-hide-xs\"><i class=\"fa fa-tag\" aria-hidden=\"true\"></i>&nbsp;<a\n" +
                        "                        style=\"color:#009688\" href=\"\">" + detail.tagName + "\n" +
                        "                        </a></span>\n" +
                        "                <span><i class=\"fa fa-clock-o\" aria-hidden=\"true\"></i>&nbsp;" + detail.createTime + "</span>\n" +
                        "                <a class=\"read layui-btn layui-btn-xs layui-btn-normal layui-hide-xs\"\n" +
                        "                   href=\"/details/" + detail.id + "\" title=\"" + detail.blogName + "\">阅读全文</a>\n" +
                        "            </div>\n" +
                        "        </div>\n" +
                        "    </div>";
                }
            }
            $('#top-blog-show').html(html);

            //渲染计数数据
            html = "";
            let countList = res.data.countList;
            html += "<div class=\"item\">\n" +
                "        <span>" + countList[0] + "</span>\n" +
                "        <p>博文</p>\n" +
                "    </div>\n" +
                "    <div class=\"item\">\n" +
                "        <span>" + countList[1] + "</span>\n" +
                "        <p>分类</p>\n" +
                "    </div>\n" +
                "    <div class=\"item\">\n" +
                "        <span>" + countList[2] + "</span>\n" +
                "        <p>评论</p>\n" +
                "    </div>\n" +
                "    <div class=\"item\">\n" +
                "        <span>" + countList[3] + "</span>\n" +
                "        <p>留言</p>\n" +
                "    </div>";
            document.getElementsByClassName('bloginfo-statistics')[0].innerHTML = html;
            //博客计数信息
            window.blogTotal = res.data.totalCount;

            //渲染/清除登录信息
            if (res.data.user != null) {
                localStorage.setItem('user', JSON.stringify(res.data.user));
            } else {
                localStorage.removeItem('user');
                localStorage.removeItem('login-token');
            }
        },
        error:function () {
            let html = "<!-- 没有数据 -->\n" +
                "<div class=\"emptybox shadow\">\n" +
                "    <p><i style=\"font-size:50px;color:#5fb878\" class=\"layui-icon\">&#xe69c;</i></p>\n" +
                "    <p>暂时没有任何数据</p>\n" +
                "</div>"
            $('#top-blog-show').html(html)
        }
    })
    //分页显示器
    layui.use('laypage', function(){

        var laypage = layui.laypage;

        //执行一个laypage实例
        laypage.render({
            elem: 'layui-page', //注意，这里的 test1 是 ID，不用加 # 号
            count: window.blogTotal, //数据总数，从服务端得到
            limit: 10,
            jump: function(obj, first){
                //obj包含了当前分页的所有参数，比如：
                indexList(obj.curr, obj.limit);
            }
        });
    });
}

function indexList(pageNum, pageSize) {
    if (pageNum == null)
        pageNum = 1;
    if (pageSize == null)
        pageSize = 10;
    $.ajax({
        data: {
            pageNum:pageNum,
            pageSize:pageSize
        },
        type:"GET",                //请求方式
        url:"/index/list",                 //路径
        async:true,             //是否异步
        dataType:"json",        //返回数据的格式
        success:function(res){  //成功的回调函数
            //具体内容
            let html = "";
            let list = res.data.list;
            for (let i in list) {
                let detail = list[i];
                html += "<div class=\"article shadow clearfix sr-listshow\">\n" +
                    "        <div class=\"article-right\">\n" +
                    "            <div class=\"article-title\">\n" +
                    "                <a href=\"/details/" + detail.id + "\">" + detail.blogName + "</a>\n" +
                    "            </div>\n" +
                    "            <div class=\"article-abstract\">\n"  + detail.shortContent + "\n" +
                    "            </div>\n" +
                    "            <div class=\"article-footer\">\n" +
                    "                <span class=\"layui-hide-xs\"><i class=\"fa fa-tag\" aria-hidden=\"true\"></i>&nbsp;<a\n" +
                    "                        style=\"color:#009688\" href=\"\">" + detail.tagName + "\n" +
                    "                        </a></span>\n" +
                    "                <span><i class=\"fa fa-clock-o\" aria-hidden=\"true\"></i>&nbsp;" + detail.createTime + "</span>\n" +
                    "                <a class=\"read layui-btn layui-btn-xs layui-btn-normal layui-hide-xs\"\n" +
                    "                   href=\"/details/" + detail.id + "\" title=\"" + detail.blogName + "\">阅读全文</a>\n" +
                    "            </div>\n" +
                    "        </div>\n" +
                    "    </div>";
            }
            $('#newest-blog-show').html(html);
        },
        error:function () {
            let html = "<!-- 没有数据 -->\n" +
                "<div class=\"emptybox shadow\">\n" +
                "    <p><i style=\"font-size:50px;color:#5fb878\" class=\"layui-icon\">&#xe69c;</i></p>\n" +
                "    <p>暂时没有任何数据</p>\n" +
                "</div>"
            $('#newest-blog-show').html(html)
        }
    })
}
