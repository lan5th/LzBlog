function initPage() {
    //博客上传相关组件
    layui.use(['form','upload'], function(){
        var form = layui.form;
        var upload = layui.upload;
        window.blogId = null;
        if (window.oldBlog != null) {
            window.blogId = window.oldBlog.id;
        }

        //保存数据
        form.on('submit(saveBlog)', function(data){
            let formData = data.field;
            // layer.msg(JSON.stringify(formData));

            if (formData.tagId != ''  && formData.newTagName != '') {
                layer.alert('创建新分类时分类下拉框必须选择null!');
                return false;
            }

            $.ajax({
                headers: {
                    'token': localStorage.getItem('login-token')
                },
                data: {
                    blogId: window.blogId,
                    title: formData.title,
                    tagId: formData.tagId,
                    shortContent: formData.shortContent,
                    newTagName: formData.newTagName,
                },
                type: "post",                //请求方式
                url: "/details/save",                 //路径
                async: true,             //是否异步
                dataType: "json",        //返回数据的格式
                success: function (res) {
                    // window.blogId = res.data.blogId;
                    if (res.status == 1) {
                        layer.open({
                            content: '保存博客成功'
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
                error: function () {
                    layer.alert('内部错误');
                }
            })
            return false;
        });

        //上传文件
        upload.render({
            headers: {
                token: localStorage.getItem('login-token')
            },
            method: 'POST',
            elem: '#blogFile',
            url: '/file/upload',
            auto: true,//选择文件后自动上传
            // bindAction: '#commit-btn',
            accept: 'file',
            exts: 'md|markdown',
            acceptMime: 'markdown',
            data: {
                'blogId': window.blogId
            },
            //操作成功的回调
            done: function (res, index, upload) {
                if (res.status == 1) {
                    window.blogId = res.data.blogId;
                    document.getElementById("blogFile").innerHTML = '上传成功';
                    document.getElementById("title").value = res.data.blogName;
                    layer.alert('文件上传成功');
                } else {
                    layer.alert(res.message);
                }
            },
            //上传错误回调
            error: function (index, upload) {
                layer.alert('上传失败！' + index);
            }
        });
    });
}

function resetData() {
    let tags = window.allTags;
    let html = '';
    if (window.oldBlog != null) {
        //更新博客需要默认设置之前的数据
        //设置tag
        for (let i in  tags) {
            let tmpTag = tags[i];
            html += "<option value=\"\">null</option>\n";
            if (window.oldBlog.tagId == tmpTag.id) {
                //默认选择加上selected属性
                html = "<option value=\"" + tmpTag.id + "\" selected>" + tmpTag.tagName + "</option>\n" + html;
            } else {
                html += "<option value=\"" + tmpTag.id + "\">" + tmpTag.tagName + "</option>\n";
            }
        }
        document.getElementById("tags").innerHTML = html;
        //设置title
        document.getElementById("title").value = window.oldBlog.blogName;
        //设置shortContent
        document.getElementById("shortContent").value = window.oldBlog.shortContent;
    } else {
        //新建博客仅更新tag数据
        html += "<option value=\"\">null</option>\n";
        for (let i in  tags) {
            let tmpTag = tags[i];
            html += "<option value=\"" + tmpTag.id + "\">" + tmpTag.tagName + "</option>\n";
        }
        document.getElementById("tags").innerHTML = html;
    }
}
