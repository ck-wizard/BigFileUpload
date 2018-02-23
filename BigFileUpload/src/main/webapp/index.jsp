<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="head.jsp" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="utf-8"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <link rel="stylesheet" href="bootstrap-3.3.7-dist/css/bootstrap.css"/>
    <script type="text/javascript" src="jquery-1.11.3/jquery.js"></script>
    <script type="text/javascript" src="bootstrap-3.3.7-dist/js/bootstrap.js"></script>

    <script type="text/javascript" src="webuploader-0.1.5/webuploader.js"></script>

    <style type="text/css">
        .bottom-20px {
            margin-top: 20px;
            margin-bottom: 20px;
        }
    </style>

</head>
<body>
<%--<div class="container">--%>
<%--<div class="row" style="padding-top: 70px;"></div>--%>
<%--<div class="row">--%>
<%--<!--用来存放文件信息-->--%>
<%--<div class="form-group">--%>
<%--<label for="picker">File input</label>--%>
<%--<input type="file" id="picker" multiple="multiple">--%>
<%--<p class="help-block">Example block-level help text here.</p>--%>
<%--</div>--%>
<%--<div class="form-group">--%>
<%--<button class="btn btn-default" onclick="btnClick()">提交</button>--%>
<%--</div>--%>
<%--</div>--%>

<%--<div id="showInfo" class="row">--%>
<%--<p>目前的进度如下:</p>--%>
<%--<div class="progress">--%>
<%--<div class="progress-bar" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100">--%>
<%--<span class="sr-only">60% Complete</span>--%>
<%--</div>--%>
<%--</div>--%>
<%--</div>--%>
<%--</div>--%>

<div class="container">

    <div class="row bottom-20px">
            <div id="picker" class="webuploader-container">
                <label>选择文件</label>
            </div>

    </div>
    <div id="thelist" class="row bottom-20px">

    </div>
    <div class="row bottom-20px">
        <button id="ctlBtn" class="btn btn-default">开始上传</button>
    </div>
</div>


<script type="text/javascript">
    var $ = jQuery,
        $list = $('#thelist'),
        $btn = $('#ctlBtn'),
        state = 'pending',
        uploader;

    var uploader = WebUploader.create({

        // swf文件路径
        swf: '${ctx}/webuploader-0.1.5/Uploader.swf',

        // 文件接收服务端。
        server: '${ctx}/upload.do',
        //文件上传请求的参数表，每次发送都会发送此对象中的参数
        formData: {
            md5: ''
        },

        // 选择文件的按钮。可选。
        // 内部根据当前运行是创建，可能是input元素，也可能是flash.
        pick: '#picker',

        // 不压缩image, 默认如果是jpeg，文件上传前会压缩一把再上传！
        resize: false,

        chunked: true, // 分块
        chunkSize: 1 * 1024 * 1024, // 字节 1M分块
        threads: 3, //开启线程
        auto: false,

        // 禁掉全局的拖拽功能。这样不会出现图片拖进页面的时候，把图片打开。
        disableGlobalDnd: true,
        fileNumLimit: 1024,
        fileSizeLimit: 200 * 1024 * 1024,    // 200 M
        fileSingleSizeLimit: 100 * 1024 * 1024    // 100 M
    });

    // 当有文件被添加进队列的时候
    uploader.on('fileQueued', function (file) {
        console.log("文件队列事件被触发..");
        $list.append('<div id="' + file.id + '" class="item">' +
            '<h4 class="info">' + file.name + '</h4>' +
            '<p class="state"></p>' +
            '</div>');

        var _file = $("#" + file.id);
        uploader.md5File( file )
        // 及时显示进度
            .progress(function(percentage) {
                //console.log('Percentage:', percentage);
                _file.find("p").html("准备中:"+ percentage * 100 + "%");
            })

            // 完成
            .then(function(val) {
                uploader.options.formData.md5 = val;
                _file.find("p").html("准备完成,等待上传.");
            });

    });

    // 文件上传过程中创建进度条实时显示。
    uploader.on('uploadProgress', function (file, percentage) {
        var $li = $('#' + file.id),
            $percent = $li.find('.progress .progress-bar');

        // 避免重复创建
        if (!$percent.length) {
            $percent = $('<div class="progress progress-striped active">' +
                '<div class="progress-bar" role="progressbar" style="width: 0%">' +
                '</div>' +
                '</div>').appendTo($li).find('.progress-bar');
        }

        $li.find('p.state').text('上传中');

        $percent.css('width', percentage * 100 + '%');
    });

    uploader.on('uploadSuccess', function (file) {
        $('#' + file.id).find('p.state').text('已上传');
    });

    uploader.on('uploadError', function (file) {
        $('#' + file.id).find('p.state').text('上传出错');
    });

    uploader.on('uploadComplete', function (file) {
        $('#' + file.id).find('.progress').fadeOut();
    });


    $btn.on('click', function () {
        if (state === 'uploading') {
            uploader.stop();
        } else {
            uploader.upload();
        }
    });

</script>
</body>
</html>