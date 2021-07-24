/**index.html代码模板*/
#sql("index")
#[[<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8">
    <title class="lang">${tableComment}</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="${ctx}/lib/layui/css/layui.css" media="all">
    <style type="text/css">
        .layui-table-nodata {
            text-align: center;
            display: block;
            color: #cccccc;
        }
    </style>
</head>

<body style="display: none;">
<div style="border-style: solid; border-width: 1px; border-color: #eee;">
    <form class="layui-form layui-form-pane">
        <div class="layui-form-item">
            <label class="layui-form-label" style="margin-top: 5px; margin-left: 1%; border-radius: 2px">模块</label>
            <div class="layui-input-inline" style="margin-top: 5px">
                <select id="name" name="name" lay-filter="category" lay-search="">
                    <option value="">请选择</option>
                    <option value="name1">name1</option>
                </select>
            </div>
            <div class="layui-btn-group" style="margin-left: 5px; margin-top: 5px">
                <button type="button" class="layui-btn" data-type="search">
                    <i class="layui-icon">&#xe615;</i>
                </button>
                <button type="button" class="layui-btn" data-type="add">
                    <i class="layui-icon">&#xe654;</i>
                </button>
            </div>
        </div>
    </form>
</div>
<div class="layui-fluid">
    <div class="layui-row layui-col-space15">
        <div class="layui-col-md12">
            <div class="layui-card">
                <div class="layui-card-body">
                    <table class="layui-hide" id="${lowercaseModelName}_table" lay-filter="${lowercaseModelName}_table" lay-skin="row"></table>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="${ctx}/lib/jquery/jquery.min.js" charset="utf-8"></script>
<script src="${ctx}/lib/layui/layui.js" charset="utf-8"></script>
<script src="${ctx}/static/js/common.js" charset="utf-8"></script>
<script type="text/html" id="statusbar">

    {{# if(d.enable==0){ }}
    <input type="checkbox" name="lockCom" title="禁用" lay-filter="lock" value="{{d.id}}">
    {{#}}}
    {{# if(d.enable==1){ }}
    <input type="checkbox" name="lockCom" title="启用" lay-filter="lock" value="{{d.id}}" checked>
    {{#}}}
</script>
<script type="text/html" id="toolbar">
    <a class="layui-btn layui-btn-xs" lay-event="edit">编辑</a>
    <a class="layui-btn layui-btn-danger layui-btn-xs lang" type="button" lay-event="delete">删除</a>
</script>
</body>
<script>
    layui.use(['laypage', 'layer', 'table', 'form', 'util'], function () {
        var laypage = layui.laypage, layer = layui.layer, table = layui.table, form = layui.form, util = layui.util;
        table.render({
            elem: "#${lowercaseModelName}_table",
            url: '${ctx}${actionKey}/list',
            text: {
                none: "没有数据"
            },
            cellMinWidth: 100,
            loading: true,
            even: false,
            page: {
                prev: "上一页",
                next: "下一页",
                first: "首页",
                last: "最后一页",
                groups: 6
            },
            limit: 50,
            limits: [30, 50, 100],
            parseData: function (res) {
                return {
                    "code": res.state == "ok" ? 0 : -1,
                    "msg": res.msg,
                    "count": res.data.totalRow,
                    "data": res.data.list
                }
            },
            cols: [[
            ${tableCols}
                ]]
        });

        table.on('tool(${lowercaseModelName}_table)', function (obj) {
            var data = obj.data;
            var layEvent = obj.event;
            switch (obj.event) {
                case 'delete': {
                    ajax_confirm({
                        title: "确定删除" + data.name,
                        url: '${ctx}${actionKey}/delete?id=' + data.id,
                        success: callback_delete
                    });
                    break;
                }
                case 'edit': {
                    open_url({
                        title: "确定编辑" + data.name,
                        url: '${ctx}${actionKey}/editHtml?id=' + data.id,
                        area: ["35%", "40%"]
                    });
                    break;
                }
            }
        });

        //监听锁定操作
        form.on('checkbox(lock)', function (obj) {
            console.log(obj)
            $.ajax({
                url: "${ctx}${actionKey}/enable?id=" + obj.value + "&enable=" + obj.elem.checked,
                type: 'get',
                success: function (data) {
                    if (data.state == "ok") {
                        tip("修改成功！");
                        table_reload()
                    } else {
                        tip(data.msg);
                    }
                },
            });
        });


        $('.layui-btn').on('click', function () {
            var type = $(this).data('type');
            switch (type) {
                case 'search': {
                    table_reload({
                        page: {
                            curr: 1
                        },
                        where: {
                            section: $('#name').val()
                        }
                    });
                    break;
                }
                case 'add': {
                    open_url({
                        title: "新增预警",
                        url: '${ctx}${actionKey}/addHtml',
                    });
                    break;
                }
            }
        });
        var callback_setting_enable = function (data, status) {
            if (data.state == "ok") {
                tip("设置成功！");
                table_reload();
            } else {
                layer.msg(data.msg);
            }
        };
    });

    function table_reload(search) {
        if (search) {
            layui.table.reload('${lowercaseModelName}_table', search);
            return;
        }
        layui.table.reload('${lowercaseModelName}_table');
    }
</script>

</html>]]#

#end
