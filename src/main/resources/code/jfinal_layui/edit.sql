/**edit.html代码模板 ${actionKey} 动态路径*/
#sql("edit")
#[[<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8">
    <title class="lang">编辑</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link href="${ctx}/lib/layui/css/layui.css" rel="stylesheet">
</head>

<body style="display: none;">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="row">
        <div class="col-sm-12">
            <div class="ibox float-e-margins">
                <div class="ibox-content"><% include("_form.html"){} %></div>
            </div>
        </div>
    </div>
</div>

<script src="${ctx}/lib/jquery/jquery.min.js" charset="utf-8"></script>
<script src="${ctx}/lib/layui/layui.js" charset="utf-8"></script>
<script src="${ctx}/static/js/common.js" charset="utf-8"></script>
<script src="${ctx}/lib/layui/ext/xm-select.js" charset="utf-8"></script>

</body>
<script>
$("body").css("display", "");
layui.use(['form', 'jquery', 'tree', 'layer', 'util'], function () {
        var form = layui.form, layer = layui.layer, $ = layui.jquery, tree = layui.tree, util = layui.util;
        // 接受response域数据设定到表单
        form.val('mainform', {
            #for(x:columnMetas)
                #if(x.name!=primaryKey)
                    "info": "${info.${x.attrName}}",
                #end
            #end

        });

        form.on('submit(main_submit)', function (data) {
            $.ajax({
                url: "${ctx}${actionKey}/edit?id=${attrs.id}",
                type: 'post',
                data: data.field,
                beforeSend: function () {
                    this.layerIndex = layer.load(0, {
                        shade: [0.5, '#393D49']
                    });
                },
                success: function (data) {
                    if (data.state == "ok") {
                        tip("修改成功！");
                        var index = parent.layer.getFrameIndex(window.name);
                        parent.layer.close(index);
                        parent.table_reload();
                    } else {
                        tip(data.msg);
                    }
                },
                complete: function () {
                    layer.close(this.layerIndex);
                },
            });
            return false;
        });
    });
</script>
</html>
]]#

#end
