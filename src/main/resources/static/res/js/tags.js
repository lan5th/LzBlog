function blogListForTag(pageNum, pageSize, tagId) {
    if (pageNum == null)
        pageNum = 1;
    if (pageSize == null)
        pageSize = 10;
    if (tagId == null)
        return;
    $.ajax({
        data: {
            pageNum:pageNum,
            pageSize:pageSize,
            tagId:tagId
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
            $('#newest-blog-show').html(html);
        }
    })
}

window.allTags;

function changeTag(tagId) {
    let allTag = window.allTags;
    let targetTag;
    for (let i in  allTag) {
        if (allTag[i].id == tagId) {
            targetTag = allTag[i];
            break;
        }
    }
    layui.use('laypage', function(){

        var laypage = layui.laypage;

        //执行一个laypage实例
        laypage.render({
            elem: 'layui-page', //注意，这里的 test1 是 ID，不用加 # 号
            count: targetTag.blogCount, //数据总数，从服务端得到
            limit: 10,
            jump: function(obj, first){
                //obj包含了当前分页的所有参数，比如：
                blogListForTag(obj.curr, obj.limit, tagId);
            }
        });
    });
}

function getAllTags() {
    let token = localStorage.getItem("login-token");
    $.ajax({
        headers: {
            'token': token
        },
        type:"GET",                //请求方式
        url:"/index/allTags",                 //路径
        async:true,             //是否异步
        dataType:"json",        //返回数据的格式
        success:function(res){  //成功的回调函数
            //渲染所有tag
            let html = "";
            let allTags = res.data.allTags;
            for (let i in allTags) {
                let tag = allTags[i];
                html += "<button type=\"button\" class=\"layui-btn\" id='" + tag.id + "' onclick='changeTag(" + tag.id + ")'>" + tag.tagName + "</button>"
            }
            document.getElementsByClassName("layui-btn-container")[0].innerHTML = html;

            //渲染默认展示tag博客
            window.allTags = allTags;
            changeTag(allTags[0].id);
        },
        error:function () {
            let html = "<!-- 没有数据 -->\n" +
                "<div class=\"emptybox shadow\">\n" +
                "    <p><i style=\"font-size:50px;color:#5fb878\" class=\"layui-icon\">&#xe69c;</i></p>\n" +
                "    <p>暂时没有任何数据</p>\n" +
                "</div>"
            $('#newest-blog-show').html(html);
        }
    })
}

function confirm(id, action) {
    switch (action) {
        case 'updateBlog':
            break;
        case 'deleteBlog':
            break;
        case 'setTop':
            break;
        case 'cancelTop':
            break;
    }
}
