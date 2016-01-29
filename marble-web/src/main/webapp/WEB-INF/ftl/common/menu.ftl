<div id="main-menu" role="navigation">
    <div id="main-menu-inner">
        <div class="menu-content top" id="menu-content-demo">
            <!-- Menu custom content demo
                     CSS:        styles/pixel-admin-less/demo.less or styles/pixel-admin-scss/_demo.scss
                     Javascript: html/${base.contextPath}/resources/assets/demo/demo.js
                 -->
            <div>
                <div class="text-bg" style="font-size: 13px;"><span class="text-slim">欢迎,</span> <span class="text-semibold">${LoginAccount.displayName}</span></div>

                <img src="${basePath}/resources/assets/demo/avatars/1.jpg" alt="" class="">
                <div class="btn-group">
                    <a href="#" class="btn btn-xs btn-primary btn-outline dark"><i class="fa fa-envelope"></i></a>
                    <a href="#" class="btn btn-xs btn-primary btn-outline dark"><i class="fa fa-user"></i></a>
                    <a href="#" class="btn btn-xs btn-primary btn-outline dark"><i class="fa fa-cog"></i></a>
                    <a style="cursor:pointer;" href="${basePath}/account/logout" class="btn btn-xs btn-danger btn-outline dark">
                        <i class="fa fa-sign-out"></i>
                    </a>
                </div>
                <a href="#" class="close">&times;</a>
            </div>
        </div>
        <ul class="navigation">
            <#if LoginAccount.hasAdminRole>
            <li>
                <a href="${basePath}/monitor"><i class="menu-icon fa fa-dashboard"></i><span class="mm-text">监控</span></a>
            </li>
            </#if>
            <li>
                <a href="${basePath}/app"><i class="menu-icon fa  fa-check-square-o"></i><span class="mm-text">计划任务管理</span></a>
            </li>
            <#if LoginAccount.hasAdminRole>
            <li>
                <a href="${basePath}/configure"><i class="menu-icon fa fa-gear"></i><span class="mm-text">Key-Value 配置</span></a>
            </li>
            <li class="mm-dropdown mm-dropdown-root open">
                <a href="#"><i class="menu-icon fa fa-th-list"></i><span class="mm-text">数据管理</span></a>
                <ul class="mmc-dropdown-delay animated fadeInLeft">
                    <li>
                        <a tabindex="-1" href="${basePath}/dataManage/trigger"><span class="mm-text">Triggers</span></a>
                    </li>
                    <li>
                        <a tabindex="-1" href="${basePath}/dataManage/cronTrigger"><span class="mm-text">Cron Triggers</span></a>
                    </li>
                    <li>
                        <a tabindex="-1" href="${basePath}/dataManage/job"><span class="mm-text">Job Details</span></a>
                    </li>
                    <li>
                        <a tabindex="-1" href="${basePath}/dataManage/lock"><span class="mm-text">Locks</span></a>
                    </li>
                    <li>
                        <a tabindex="-1" href="${basePath}/dataManage/pausedTriggerGroup"><span class="mm-text">Paused Trigger Groups</span></a>
                    </li>
                </ul>
            </li>
            </#if>
            <li>
                <a href="${basePath}/helper"><i class="menu-icon fa fa-question-circle"></i><span class="mm-text">使用帮助</span></a>
            </li>
        </ul> <!-- / .navigation -->
    </div> <!-- / #main-menu-inner -->
</div> <!-- / #main-menu -->