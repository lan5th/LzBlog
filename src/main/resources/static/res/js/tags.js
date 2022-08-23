function blogListForTag(pageNum, pageSize, tag) {
    if (pageNum == null)
        pageNum = 1;
    if (pageSize == null)
        pageSize = 10;
    if (tag.id == null)
        return;
    $.ajax({
        headers: {
            'token':localStorage.getItem('login-token')
        },
        data: {
            pageNum:pageNum,
            pageSize:pageSize,
            tagId:tag.id
        },
        type:"GET",                //请求方式
        url:"/index/list",                 //路径
        async:true,             //是否异步
        dataType:"json",        //返回数据的格式
        success:function(res){  //成功的回调函数
            //具体内容
            let html = "";
            let list = res.data.list;
            let user = res.data.user;
            let isAdmin = false;
            if (user != null && user.isAdmin == true)
                isAdmin = true;
            if (isAdmin) {
                //管理按钮
                $("#tag-info").html("<span id=\"tag-name\">" + tag.tagName + "</span>\n" +
                "                    <a href=\"javascript:updateTag(" + tag.id + ")\" style=\"color:#009688\">更新分类名称</a>\n" +
                "                    <a href=\"/details/create/\" style=\"float:right; color:#009688\">新建博客</a>\n");

                //管理员界面
                for (let i in list) {
                    let detail = list[i];
                    html += "<div class=\"article shadow clearfix sr-listshow\">\n" +
                        "        <div class=\"article-right\">\n" +
                        "            <div class=\"article-title\">\n" +
                        "                <a href=\"/details/" + detail.id + "\">" + detail.blogName + "</a>\n" +
                        "                <a href=\"javascript:actionConfirm(" + detail.id + ", 'setTop')\" style='float:right; color:#009688; font-size: 14px'>置顶</a>" +
                        "            </div>\n" +
                        "            <div class=\"article-abstract\">\n"  + detail.shortContent + "\n" +
                        "            </div>\n" +
                        "            <div class=\"article-footer\">\n" +
                        "                <span class=\"layui-hide-xs\"><i class=\"fa fa-tag\" aria-hidden=\"true\"></i>&nbsp;<a\n" +
                        "                        style=\"color:#009688\" href=\"\">" + detail.tagName + "\n" +
                        "                        </a></span>\n" +
                        "                <span><i class=\"fa fa-clock-o\" aria-hidden=\"true\"></i>&nbsp;" + detail.createTime + "</span>\n" +
                        "                <a class=\"read layui-btn layui-btn-xs layui-btn-normal layui-hide-xs\"\n style='margin-left: 10px'" +
                        "                   href=\"/details/update/" + detail.id + "\" title=\"" + detail.blogName + "\">更新博文</a>\n" +
                        "                <a class=\"read layui-btn layui-btn-xs layui-btn-normal layui-hide-xs\"\n" +
                        "                   href=\"javascript:actionConfirm(" + detail.id + ", 'deleteBlog')\" title=\"" + detail.blogName + "\">删除博文</a>\n" +
                        "                <a class=\"read layui-btn layui-btn-xs layui-btn-normal layui-hide-xs\"\n" +
                        "                   href=\"/details/" + detail.id + "\" title=\"" + detail.blogName + "\">阅读全文</a>\n" +
                        "            </div>\n" +
                        "        </div>\n" +
                        "    </div>";
                }
            } else {
                //访客和一般用户界面
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
            elem: 'layui-page',
            count: targetTag.blogCount, //数据总数，从服务端得到
            limit: 10,
            jump: function(obj, first){
                //obj包含了当前分页的所有参数，比如：
                blogListForTag(obj.curr, obj.limit, targetTag);
            }
        });
    });
}

function getAllTags() {
    getLinks();
    $.ajax({
        headers: {
            'token': localStorage.getItem('login-token')
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

function updateTag(id) {
    layer.prompt({title: '请输入新分类名', formType: 2}, function(tagName, index){
        layer.close(index);
        $.ajax({
            headers: {
                'token': localStorage.getItem('login-token')
            },
            data: {
                id: id,
                tagName: tagName
            },
            type:"POST",                //请求方式
            url:"/index/updateTag",                 //路径
            async:false,             //是否异步
            dataType:"json",        //返回数据的格式
            success:function(res){  //成功的回调函数
                //渲染所有tag
                if (res.status == 1) {
                    layer.open({
                        content: '更新分类成功'
                        ,btn: ['确认']
                        ,yes: function(index, layero){
                            window.location.href = '/index/tags';
                        }
                        ,cancel: function(){
                            return false; //开启该代码可禁止点击该按钮关闭
                        }
                    });
                } else {
                    layer.alert(res.message);
                }
            },
            error:function () {
                layer.alert('内部错误');
            }
        })
    });
}
