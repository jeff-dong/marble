<!-- Get jQuery from Google CDN -->
<!--[if !IE]> -->
<script type="text/javascript"> window.jQuery || document.write('<script src="${base.contextPath}/resources/js/jquery.min.js">'+"<"+"/script>"); </script>
<!-- <![endif]-->
<!--[if lte IE 9]>
<script type="text/javascript"> window.jQuery || document.write('<script src="${base.contextPath}/resources/js/jquery.min.js">'+"<"+"/script>"); </script>
<![endif]-->

<!-- Pixel Admin's javascripts -->
<script src="${basePath}/resources/assets/javascripts/bootstrap.min.js"></script>
<script src="${basePath}/resources/assets/javascripts/pixel-admin.min.js"></script>
<#--<script src="${basePath}/resources/js/bootstrap-modal.js"></script>-->
<#--<script src="${basePath}/resources/js/bootstrap-modalmanager.js"></script>-->
<script type="text/javascript">
    var datatable;
    function getProcessStatusName(statusId){
        var statusDiv = "未知";
        if(statusId != null){
            switch(statusId){
                case 0:
                    statusDiv = "处理完成";break;
                case 1:
                    statusDiv = "处理中";break;
            }
        }
        return statusDiv;
    }

    //取得安全字符串
    function safeString(origStr, replaceWith, maxLength){
        var safeStr = '';
        if(origStr != null && origStr!== 'null'){
            var length = origStr.length;
            if(maxLength!= null && maxLength > 0 && length > maxLength){
                safeStr = origStr.substr(0, maxLength) + '...';
            }else{
                safeStr = origStr;
            }
        }else{
            safeStr = (replaceWith == null?"":replaceWith);
        }
        return safeStr;
    }
    function clearSelect2(select2Id1, select2Id2){
        if(select2Id1!=null){
            $("#"+select2Id1+"").val(null).trigger("change");
        }
        if(select2Id2!=null){
            $("#"+select2Id2+"").val(null).trigger("change");
        }
    }

    init.push(function () {
        window.addEventListener("load", function () {

            $('#menu-content-demo .close').click(function () {
                var $p = $(this).parents('.menu-content');
                $p.addClass('fadeOut');
                setTimeout(function () {
                    $p.css({ height: $p.outerHeight(), overflow: 'hidden' }).animate({'padding-top': 0, height: $('#main-navbar').outerHeight()}, 500, function () {
                        $p.remove();
                    });
                }, 300);
                return false;
            });
        });
    });
    window.PixelAdmin.start(init);

    function logout(){
        $.ajax({
            "url":"${basePath}/account/logout",
            "type":"GET",
            "error":function(){
            },
            "success":function(res){
            }
        });
    }

    /*
    function ssoLogoutRequest(ssoURL){
        $.ajax({
            url: ssoURL,
            type: 'GET',
            dataType: 'JSONP',//here
            success: function () {
            }
        });
        window.location.href="";
    }
    */
</script>
