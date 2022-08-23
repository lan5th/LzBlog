if (window.layui) {
    layui.use(['element', 'layer', 'form', 'util', 'flow', 'layedit'], function () {
        var element = layui.element
            , form = layui.form
            , util = layui.util
            , flow = layui.flow
            , $ = layui.jquery
            , layedit = layui.layedit
            , device = layui.device()
            //评论页面和留言页面共用
            , editIndex = layedit.build('remarkEditor', {
                height: 150,
                tool: null,
            });

        //单击事件绑定
        $('*[blog-event]').on('click', function () {
            var eventName = $(this).attr('blog-event');
            typeof blog.events[eventName] == 'function' && blog.events[eventName].call(this);
        });

        //监听评论提交
        form.on('submit(formRemark)', function (data) {
            if ($(data.elem).hasClass('layui-btn-disabled'))
                return false;
            // var index = layer.load(1);
            $.ajax({
                headers: {
                    token: localStorage.getItem('login-token')
                },
                type: 'post',
                url: '/comment/saveComment',
                data: {
                    content: data.field.commentContent,
                    blogId: detail.id,
                },
                success: function (res) {
                    // layer.close(index);
                    if (res.status == 1) {
                        layer.msg('提交评论成功');
                        //重置编辑器
                        $('#remarkEditor').val('');
                        $('.blog-editor .layui-layedit').remove();
                        editIndex = layedit.build('remarkEditor', {
                            height: 150,
                            tool: null,
                        });
                        getComment(1, 10, detail.id);
                        document.getElementById("commentTotal").innerText++;
                    } else {
                        layer.alert(res.message);
                    }
                },
                error: function (e) {
                    layer.alert("内部错误");
                }
            });
            return false;
        });

        //监听留言/评论回复提交
        form.on('submit(formReply)', function (data) {
            if ($(data.elem).hasClass('layui-btn-disabled')) {
                return false;
            }
            if (data.field.content == null || data.field.content.length == 0) {
                layer.msg('回复不能为空');
                return false;
            }
            // var index = layer.load(1);
            $.ajax({
                type: 'post',
                url: '/comment/saveComment',
                headers: {
                    token: window.localStorage.getItem('login-token')
                },
                data: {
                    content: data.field.content,
                    blogId: window.blogId,
                    replyTo: data.field.replyTo,
                },
                success: function (res) {
                    if (res.status == 1) {
                        layer.msg('提交回复成功');
                        getComment(1, 10);
                        document.getElementById("commentTotal").innerText++;
                    } else {
                        layer.alert(res.message);
                    }
                },
                error: function (e) {
                    layer.alert('内部错误')
                }
            });
            return false;
        });

        //监听留言提交
        form.on('submit(formComment)', function (data) {
            // var loading = layer.load(1);
            $.ajax({
                type: 'post',
                url: '/comment/saveComment',
                headers: {
                    token: window.localStorage.getItem('login-token')
                },
                data: {
                    content: data.field.replyContent,
                    replyTo: data.field.replyTo,
                },
                success: function (res) {
                    if (res.status == 1) {
                        layer.msg('提交留言成功');
                        //重置编辑器
                        $('#remarkEditor').val('');
                        $('.blog-editor .layui-layedit').remove();
                        editIndex = layedit.build('remarkEditor', {
                            height: 150,
                            tool: null,
                        });
                        getComment(1, 10);
                    } else {
                        layer.alert(res.message);
                    }
                },
                error: function (e) {
                    layer.alert('内部错误')
                }
            });
            return false;
        });

        //自定义验证规则
        form.verify({
            commentContent: function (value) {
                value = $.trim(layedit.getText(editIndex));
                if (value == "") {
                    return '评论不能为空';
                }
                layedit.sync(editIndex);
            },
            replyContent: function (value) {
                value = $.trim(layedit.getText(editIndex));
                if (value == "") {
                    return '留言不能为空';
                }
                layedit.sync(editIndex);
            }
        });

        // $('.blog-navicon').click(function () {
        //     var sear = new RegExp('layui-hide');
        //     if (sear.test($('.blog-nav-left').attr('class'))) {
        //         leftIn();
        //     } else {
        //         leftOut();
        //     }
        // });
        //
        // $('.blog-mask').click(function () {
        //     leftOut();
        // });
        //
        // function leftIn() {
        //     $('.blog-mask').unbind('webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend');
        //     $('.blog-nav-left').unbind('webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend');
        //
        //     $('.blog-mask').removeClass('maskOut');
        //     $('.blog-mask').addClass('maskIn');
        //     $('.blog-mask').removeClass('layui-hide');
        //     $('.blog-mask').addClass('layui-show');
        //
        //     $('.blog-nav-left').removeClass('leftOut');
        //     $('.blog-nav-left').addClass('leftIn');
        //     $('.blog-nav-left').removeClass('layui-hide');
        //     $('.blog-nav-left').addClass('layui-show');
        // }
        //
        // function leftOut() {
        //     $('.blog-mask').on('webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend', function () {
        //         $('.blog-mask').addClass('layui-hide');
        //     });
        //     $('.blog-nav-left').on('webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend', function () {
        //         $('.blog-nav-left').addClass('layui-hide');
        //     });
        //
        //     $('.blog-mask').removeClass('maskIn');
        //     $('.blog-mask').addClass('maskOut');
        //     $('.blog-mask').removeClass('layui-show');
        //
        //     $('.blog-nav-left').removeClass('leftIn');
        //     $('.blog-nav-left').addClass('leftOut');
        //     $('.blog-nav-left').removeClass('layui-show');
        // }
    });
}


//时间格式化
Date.prototype.Format = function (formatStr) {
    var str = formatStr;
    var Week = ['日', '一', '二', '三', '四', '五', '六'];

    str = str.replace(/yyyy|YYYY/, this.getFullYear());
    str = str.replace(/yy|YY/, (this.getYear() % 100) > 9 ? (this.getYear() % 100).toString() : '0' + (this.getYear() % 100));
    str = str.replace(/MM/, (this.getMonth() + 1) > 9 ? (this.getMonth() + 1).toString() : '0' + (this.getMonth() + 1));
    str = str.replace(/M/g, (this.getMonth() + 1));

    str = str.replace(/w|W/g, Week[this.getDay()]);

    str = str.replace(/dd|DD/, this.getDate() > 9 ? this.getDate().toString() : '0' + this.getDate());
    str = str.replace(/d|D/g, this.getDate());

    str = str.replace(/hh|HH/, this.getHours() > 9 ? this.getHours().toString() : '0' + this.getHours());
    str = str.replace(/h|H/g, this.getHours());
    str = str.replace(/mm/, this.getMinutes() > 9 ? this.getMinutes().toString() : '0' + this.getMinutes());
    str = str.replace(/m/g, this.getMinutes());

    str = str.replace(/ss|SS/, this.getSeconds() > 9 ? this.getSeconds().toString() : '0' + this.getSeconds());
    str = str.replace(/s|S/g, this.getSeconds());

    return str;
}

function getComment(pageNum, pageSize) {
    let data = {};
    data.pageNum = pageNum;
    data.pageSize = pageSize;
    if (window.blogId != null)
        data.blogId = window.blogId;
    //博客详情页评论
    $.ajax({
        headers: {
            'token': localStorage.getItem('login-token')
        },
        data: data,
        type: "GET",                //请求方式
        url: "/comment/getComment",                 //路径
        async: true,             //是否异步
        dataType: "json",        //返回数据的格式
        success: function (res) {  //成功的回调函数
            //具体内容
            let html = "";
            let list = res.data.commentList;
            let user = res.data.user;
            let isAdmin = false;
            if (user != null && user.isAdmin == true)
                isAdmin = true;
            if (list == null || list.length == 0) {
                document.getElementsByClassName('blog-comment')[0].innerHTML =
                    "<div class=\"emptybox\">\n" +
                    "    <p><i style=\"font-size:50px;color:#5fb878\" class=\"layui-icon\"></i></p>\n" +
                    "    <p>暂无评论，大侠不妨来一发？</p>\n" +
                    "</div>";
            } else {
                //管理员界面
                for (let i in list) {
                    let comment = list[i];
                    html += "<li>\n" +
                        "        <div class=\"comment-parent\">\n" +
                        "            <a name=\"remark-@item.Id\"></a>\n" +
                        "            <img src=\"" + comment.userAvatar + "\" alt=\"" + comment.userName + "\" />\n" +
                        "            <div class=\"info\">\n";

                    html += "           <span class=\"username\">" + comment.userName + "</span>\n";
                    if (comment.replyTo != null) {
                        //回复信息
                        html += "       <span> 回复给 </span>\n";
                        html += "       <span class=\"username\">" + comment.replyToUserName + "</span>\n";
                    }
                    if (isAdmin) {
                        html += "       <a href=\"javascript:actionConfirm(" + comment.id + ", 'delComment')\" style='color: #009688; float: right'> 删除 </a>\n"
                    }

                    html += "        </div>\n" +
                        "            <div class=\"content\">" + comment.content + "</div>\n" +
                        "            <p class=\"info info-footer\"><span class=\"time\">" + comment.createTime + "</span><a href=\"javascript:openReply(" + i + ")\" class=\"btn-reply\">回复</a></p>\n" +
                        "        </div>\n" +
                        "        <!-- 回复编辑器 -->\n" +
                        "        <div class=\"replycontainer layui-hide\">\n" +
                        "            <form class=\"layui-form\" action=\"\">\n" +
                        "                <div class=\"layui-form-item\">" +
                        "                    <textarea name=\"content\" placeholder=\"请输入回复内容\" class=\"layui-textarea\" style=\"min-height:80px;\"></textarea>\n" +
                        "                    <textarea name=\"replyTo\" class=\"layui-textarea layui-hide\" style=\"min-height:80px;\">" + comment.userId + "</textarea>\n" +
                        "                </div>\n" +
                        "                <div class=\"layui-form-item\">\n" +
                        "                    <button class=\"layui-btn layui-btn-xs\" lay-submit=\"formReply\" lay-filter=\"formReply\">提交</button>\n" +
                        "                </div>\n" +
                        "            </form>\n" +
                        "        </div>\n" +
                        "    </li>";
                }
                document.getElementsByClassName('blog-comment')[0].innerHTML = html;
            }
        },
        error: function () {
            layer.alert("内部错误");
        }
    })
}

function openReply(index) {
    let openButton = document.getElementsByClassName('btn-reply')[index];
    let replyContainer = document.getElementsByClassName('replycontainer')[index]
    if (openButton.text == '回复') {
        replyContainer.classList.remove('layui-hide');
        openButton.innerText = '收起';
    } else {
        replyContainer.classList.add('layui-hide');
        openButton.innerText = '回复';
    }
}

function actionConfirm(id, action) {
    switch (action) {
        case 'delComment':
            layer.confirm('确认取消删除该评论？', {
                btn: ['确认', '取消'] //可以无限个按钮
            }, function(index){
                delComment(id);
            }, function(index){
                layer.close(index);
            });
            break;
        case 'deleteBlog':
            layer.confirm('确认要删除该博客？', {
                btn: ['删除', '取消'] //可以无限个按钮
            }, function(index){
                deleteBlog(id);
            }, function(index){
                layer.close(index);
            });
            break;
        case 'setTop':
            layer.confirm('确认置顶该博客？', {
                btn: ['置顶', '取消'] //可以无限个按钮
            }, function(index){
                setTopBlog(id);
            }, function(index){
                layer.close(index);
            });
            break;
        case 'cancelTop':
            layer.confirm('确认取消置顶该博客？', {
                btn: ['确认', '取消'] //可以无限个按钮
            }, function(index){
                cancelTop(id);
            }, function(index){
                layer.close(index);
            });
            break;
        case 'delLink':
            layer.confirm('确认删除该链接？',{
                btn: ['删除', '取消'] //可以无限个按钮
            }, function(index){
                delLink(id);
            }, function(index){
                layer.close(index);
            });
            break;
    }
}

function delComment(id) {
    $.ajax({
        headers: {
            'token': localStorage.getItem('login-token')
        },
        data: {
            commentId: id,
            blogId: window.blogId
        },
        type: "DELETE",                //请求方式
        url: "/comment/delComment",                 //路径
        async: false,             //是否异步
        dataType: "json",        //返回数据的格式
        success: function (res) {  //成功的回调函数
            if (res.status == 1) {
                layer.msg('删除成功');
                getComment(1, 10);
            } else {
                layer.alert(res.message);
            }
        },
        error: function () {
            layer.alert("内部错误");
        }
    })
}

function deleteBlog(blogId) {
    $.ajax({
        headers: {
            'token': localStorage.getItem('login-token')
        },
        type:"DELETE",                //请求方式
        url:"/details/delete/" + blogId,                 //路径
        async:true,             //是否异步
        dataType:"json",        //返回数据的格式
        success:function(res){  //成功的回调函数
            if (res.status == 1) {
                layer.open({
                    content: '删除成功'
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
}

function setTopBlog(id) {
    $.ajax({
        headers: {
            'token': localStorage.getItem('login-token')
        },
        type:"POST",                //请求方式
        url:"/index/setTop/" + id,                 //路径
        async:true,             //是否异步
        dataType:"json",        //返回数据的格式
        success:function(res){  //成功的回调函数
            if (res.status == 1) {
                layer.open({
                    content: '置顶成功'
                    ,btn: ['确认']
                    ,yes: function(index, layero){
                        window.location.href = '/';
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
}

function cancelTop(id) {
    $.ajax({
        headers: {
            'token': localStorage.getItem('login-token')
        },
        type:"POST",                //请求方式
        url:"/index/cancelTop/" + id,                 //路径
        async:true,             //是否异步
        dataType:"json",        //返回数据的格式
        success:function(res){  //成功的回调函数
            if (res.status == 1) {
                layer.open({
                    content: '取消置顶成功'
                    ,btn: ['确认']
                    ,yes: function(index, layero){
                        window.location.href = '/';
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
}

function getLinks() {
    //博主推荐
    $.ajax({
        headers: {
            'token': localStorage.getItem("login-token")
        },
        type:"GET",                //请求方式
        url:"/index/getLinks",                 //路径
        data: {
            type: 1
        },
        async:true,             //是否异步
        dataType:"json",        //返回数据的格式
        success:function(res) {
            let html = "";
            let links = res.data.links;
            let user = JSON.parse(localStorage.getItem('user'));
            if (user != null && user.isAdmin == true) {
                let tmpNo = 1;
                for (let i in links) {
                    let link = links[i];
                    html += "<li>\n" +
                        "        <span class=\"layui-badge \">" + tmpNo + "</span>" +
                        "        <a href=\"" + link.url + "\" title=\"" + link.linkName + "\">" + link.linkName + "</a>\n" +
                        "        <a href=\"javascript:actionConfirm(" + link.id + ", 'delLink')\" style=\"float: right\">删除链接</a>" +
                        "    </li>";
                    tmpNo++;
                }
                let btns = document.getElementsByClassName('link-btn');
                btns[0].removeAttribute('hidden');
                btns[1].removeAttribute('hidden');
            } else {
                for (let i in links) {
                    let link = links[i];
                    html += "<li>\n" +
                        "        <span class=\"layui-badge \">1</span><a href=\"" + link.url + "\"\n" +
                        "             title=\"" + link.linkName + "\">" + link.linkName + "</a>\n" +
                        "    </li>";
                }
            }
            document.getElementById("link-space-recommend").innerHTML = html;
        },
        error:function () {
            alert('内部错误');
        }
    })
    //友链
    $.ajax({
        headers: {
            'token': localStorage.getItem("login-token")
        },
        type:"GET",                //请求方式
        url:"/index/getLinks",                 //路径
        data: {
            type: 2
        },
        async:true,             //是否异步
        dataType:"json",        //返回数据的格式
        success:function(res) {
            let html = "";
            let links = res.data.links;
            let user = JSON.parse(localStorage.getItem('user'));
            if (user != null && user.isAdmin == true) {
                for (let i in links) {
                    let link = links[i];
                    html += "<li><a target=\"_blank\" href=\"" + link.url + "\" " +
                        "title=\"" + link.linkName + "\">" + link.linkName + "</a></li>";
                }
            } else {
                for (let i in links) {
                    let link = links[i];
                    html += "<li><a target=\"_blank\" href=\"" + link.url + "\" " +
                        "title=\"" + link.linkName + "\">" + link.linkName + "</a></li>";
                }
            }
            document.getElementById("link-space-friend").innerHTML = html;
        },
        error:function () {
            layer.alert('内部错误');
        }
    })
}

function addLink(type) {
    layer.prompt({title: '请输入链接名', formType: 3}, function(linkName, index){
        layer.close(index);
        layer.prompt({title: '请输入链接url', formType: 3}, function(url, index) {
            layer.close(index);
            $.ajax({
                headers: {
                    'token': localStorage.getItem('login-token')
                },
                data: {
                    linkName: linkName,
                    url: url,
                    type: type
                },
                type: "POST",                //请求方式
                url: "/index/saveLink",                 //路径
                async: true,             //是否异步
                dataType: "json",        //返回数据的格式
                success: function (res) {  //成功的回调函数
                    //渲染所有tag
                    if (res.status == 1) {
                        layer.open({
                            content: '新增链接成功'
                            , btn: ['确认']
                            , yes: function (index, layero) {
                                window.location.href = '/';
                            }
                            , cancel: function () {
                                return false; //开启该代码可禁止点击该按钮关闭
                            }
                        });
                    } else {
                        layer.alert(res.message);
                    }
                },
                error: function () {
                    layer.alert('内部错误');
                }
            })
        });
    });
}

function delLink(id) {
    $.ajax({
        headers: {
            'token': localStorage.getItem("login-token")
        },
        type:"delete",                //请求方式
        url:"/index/delLink",                 //路径
        data: {
            id: id
        },
        async:true,             //是否异步
        dataType:"json",        //返回数据的格式
        success:function(res) {
            layer.open({
                content: '删除成功'
                ,btn: ['确认']
                ,yes: function(index, layero){
                    location.reload();
                }
                ,cancel: function(){
                    return false; //开启该代码可禁止点击该按钮关闭
                }
            });
        },
        error:function () {
            layer.alert('内部错误');
        }
    })
}

function getFriendLinks4About() {
    $.ajax({
        headers: {
            'token': localStorage.getItem("login-token")
        },
        type:"GET",                //请求方式
        url:"/index/getLinks",                 //路径
        data: {
            type: 2
        },
        async:true,             //是否异步
        dataType:"json",        //返回数据的格式
        success:function(res) {
            let html = "";
            let links = res.data.links;
            let user = JSON.parse(localStorage.getItem('user'));
            if (user != null && user.isAdmin == true) {
                for (let i in links) {
                    let link = links[i];
                    html += "<div class=\"layui-col-lg3 layui-col-md4 layui-col-sm6\">\n" +
                        "        <a href=\"" + link.url + "\" target=\"_blank\" class=\"friendlink-item\">\n" +
                        "            <h2>" + link.linkName + "</h2>\n" +
                        "            <p>" + link.url + "</p>\n" +
                        "        </a>\n" +
                        "        <a href=\"javascript:actionConfirm(" + link.id + ", 'delLink')\" style=\"text-align: center; color: #009688\">删除</a>" +
                        "    </div>";
                }
            } else {
                for (let i in links) {
                    let link = links[i];
                    html += "<div class=\"layui-col-lg3 layui-col-md4 layui-col-sm6\">\n" +
                        "        <a href=\"" + link.url + "\" target=\"_blank\" class=\"friendlink-item\">\n" +
                        "            <h2>" + link.linkName + "</h2>\n" +
                        "            <p>" + link.url + "</p>\n" +
                        "        </a>\n" +
                        "    </div>";
                }
            }
            document.getElementById("friend-link-about").innerHTML = html;
        },
        error:function () {
            layer.alert('内部错误');
        }
    })
}
