<div id="main-navbar" class="navbar navbar-inverse" role="navigation">
    <!-- Main menu toggle -->
    <button type="button" id="main-menu-toggle"><i class="navbar-icon fa fa-bars icon"></i><span class="hide-menu-text">HIDE MENU</span></button>

    <div class="navbar-inner">
        <!-- Main navbar header -->
        <div class="navbar-header">

            <!-- Logo -->
            <div class="navbar-brand">
                <div><img alt="Pixel Admin" src="${base.contextPath}/resources/assets/images/pixel-admin/main-navbar-logo.png"></div>
                MARBLE OFFLINE
            </div>

            <!-- Main navbar toggle -->
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#main-navbar-collapse"><i class="navbar-icon fa fa-bars"></i></button>

        </div> <!-- / .navbar-header -->

        <div id="main-navbar-collapse" class="collapse navbar-collapse main-navbar-collapse">
            <div>

                <div class="right clearfix">
                    <ul class="nav navbar-nav pull-right right-navbar-nav">
                        <li>
                            <form class="navbar-form pull-left">
                                <input type="text" class="form-control" placeholder="Search">
                            </form>
                        </li>

                        <li class="dropdown">
                            <a href="#" class="dropdown-toggle user-menu" data-toggle="dropdown">
                                <span>${LoginAccount.displayName}</span>
                            </a>
                            <ul class="dropdown-menu">
                                <li><a href="${basePath}/account/logout"><i class="dropdown-icon fa fa-sign-out"></i>&nbsp;&nbsp;退出</a></li>
                            </ul>
                        </li>
                    </ul> <!-- / .navbar-nav -->
                </div> <!-- / .right -->
            </div>
        </div> <!-- / #main-navbar-collapse -->
    </div> <!-- / .navbar-inner -->

    <div id="modals-alerts-danger" class="modal modal-alert modal-danger fade" aria-hidden="true" style="display: none;">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <i class="fa fa-times-circle" style="font-size:20px;"><span style="margin-left:10px;">OOPS! 出错啦！</span></i>
                </div>
                <div class="modal-title" style="margin-bottom: 30px;">
                    <p style="font-size: 16px;">尝试刷新页面再试</p>
                    <a style="cursor:pointer;font-size: 20px;" onclick="javascript:location.reload();return true;">点我刷新</a>
                </div>
            </div> <!-- / .modal-content -->
        </div> <!-- / .modal-dialog -->
    </div>
</div> <!-- / #main-navbar -->