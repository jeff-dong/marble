

(function ($) {

    var resultsName = "";
    var inputElement;
    var displayElement;

    $.fn.extend({
        cronGen: function () {
            //create top menu
            var cronContainer = $("<div/>", { id: "CronContainer", style: "display:none;width:300px;height:300px;" });
            var mainDiv = $("<div/>", { id: "CronGenMainDiv", style: "" });
            var topMenu = $("<ul/>", { "class": "nav nav-tabs", id: "CronGenTabs" });
            $('<li/>', { 'class': 'active' }).html($('<a id="MinutesTab" href="#Minutes">分钟</a>')).appendTo(topMenu);
            $('<li/>').html($('<a id="HourlyTab" href="#Hourly">小时</a>')).appendTo(topMenu);
            $('<li/>').html($('<a id="DailyTab" href="#Daily">天</a>')).appendTo(topMenu);
            $('<li/>').html($('<a id="WeeklyTab" href="#Weekly">周</a>')).appendTo(topMenu);
            $('<li/>').html($('<a id="MonthlyTab" href="#Monthly">月</a>')).appendTo(topMenu);
            $('<li/>').html($('<a id="YearlyTab" href="#Yearly">年</a>')).appendTo(topMenu);
            $(topMenu).appendTo(mainDiv);

            //create what's inside the tabs
            var container = $("<div/>", { "class": "", "style": "" });
            var row = $("<div/>", { "class": "" });
            var span12 = $("<div/>", { "class": "tab-content tab-content-bordered" });
            //定义操作按钮
            var cronOpera = $("<div style='text-align:center;'/>", { "class": "cron-op-btn" });
            cronOpera.html('<div class="btn-group btn-group-justified" >'+
                                '<a id="cron-gen-btn" href="#" class="btn btn-success">生成Cron表达式</a>'+
                                '<a id="cron-explain-btn" href="#" class="btn btn-info">解析表达式</a>'+
                                '<a id="cron-clear-btn" href="#" class="btn">清除</a>'+
                            '</div>');
            //cronOpera.html('<button type="button" id="cron-gen-btn" class="btn">生成Cron表达式</button> &nbsp;&nbsp;&nbsp;<a id="cron-clear-btn" style="cursor: pointer;">清除</a>');
            //定义cron表达式解释区域
            var cronDescArea = $("<div style='text-align:center;border: 1px solid #d7d7d7;margin-top:20px;'/>", { "class": "cron-desc" });
            cronDescArea.html('<table id="cron-explain-tb" class="table" style="margin-bottom:0px;"><thead><tr><th>下次最近5次执行时间&nbsp;<span style="color: #138613;"></span></th></tr></thead><tbody><tr><td style="font-style: italic;">no results</td></tr></tbody></table>');

            var tabContent = $("<div/>", { "class": "tab-content" });

            //creating the minutesTab
            var minutesTab = $("<div/>", { "class": "tab-pane active", id: "Minutes" });
            $(minutesTab).append("每&nbsp;");
            $("<input/>", { id: "MinutesInput", type: "text", value: "1", style: "width: 40px" }).appendTo(minutesTab);
            $(minutesTab).append("&nbsp;分钟");
            $(minutesTab).appendTo(tabContent);

            //creating the hourlyTab
            var hourlyTab = $("<div/>", { "class": "tab-pane", id: "Hourly" });

            var hourlyOption1 = $("<div/>", { "class": "well well-small" });
            $("<input/>", { type: "radio", value: "1", name: "HourlyRadio", checked: "checked" }).appendTo(hourlyOption1);
            $(hourlyOption1).append("&nbsp;每&nbsp;");
            $("<input/>", { id: "HoursInput", type: "text", value: "1", style: "width: 40px" }).appendTo(hourlyOption1);
            $(hourlyOption1).append("&nbsp;小时");
            $(hourlyOption1).appendTo(hourlyTab);

            var hourlyOption2 = $("<div/>", { "class": "well well-small" });
            $("<input/>", { type: "radio", value: "2", name: "HourlyRadio" }).appendTo(hourlyOption2);
            $(hourlyOption2).append("&nbsp;具体时间点：&nbsp;");
            $(hourlyOption2).append('<select id="AtHours" class="hours" style="width: 60px"></select>');
            $(hourlyOption2).append('<select id="AtMinutes" class="minutes" style="width: 60px"></select>');
            $(hourlyOption2).appendTo(hourlyTab);

            $(hourlyTab).appendTo(tabContent);

            //creating the dailyTab
            var dailyTab = $("<div/>", { "class": "tab-pane", id: "Daily" });

            var dailyOption1 = $("<div/>", { "class": "well well-small" });
            $("<input/>", { type: "radio", value: "1", name: "DailyRadio", checked: "checked" }).appendTo(dailyOption1);
            $(dailyOption1).append("&nbsp;每&nbsp;");
            $("<input/>", { id: "DaysInput", type: "text", value: "1", style: "width: 40px" }).appendTo(dailyOption1);
            $(dailyOption1).append("&nbsp;天");
            $(dailyOption1).appendTo(dailyTab);

            var dailyOption2 = $("<div/>", { "class": "well well-small" });
            $("<input/>", { type: "radio", value: "2", name: "DailyRadio" }).appendTo(dailyOption2);
            $(dailyOption2).append("&nbsp;每个工作日&nbsp;");
            $(dailyOption2).appendTo(dailyTab);

            $(dailyTab).append("开始时间：&nbsp;");
            $(dailyTab).append('<select id="DailyHours" class="hours" style="width: 60px"></select>');
            $(dailyTab).append('<select id="DailyMinutes" class="minutes" style="width: 60px"></select>');

            $(dailyTab).appendTo(tabContent);

            //craeting the weeklyTab
            var weeklyTab = $("<div/>", { "class": "tab-pane", id: "Weekly" });
            var weeklyWell = $("<div/>", { "class": "well well-small" });

            var span31 = $("<div/>", { "class": "span6 col-sm-6" });
            $("<input/>", { type: "checkbox", value: "MON" }).appendTo(span31);
            $(span31).append("&nbsp; 星期一<br />");
            $("<input/>", { type: "checkbox", value: "WED" }).appendTo(span31);
            $(span31).append("&nbsp; 星期三<br />");
            $("<input/>", { type: "checkbox", value: "FRI" }).appendTo(span31);
            $(span31).append("&nbsp; 星期五<br />");
            $("<input/>", { type: "checkbox", value: "SUN" }).appendTo(span31);
            $(span31).append("&nbsp; 星期天");

            var span32 = $("<div/>", { "class": "span6 col-sm-6" });
            $("<input/>", { type: "checkbox", value: "TUE" }).appendTo(span32);
            $(span32).append("&nbsp; 星期二<br />");
            $("<input/>", { type: "checkbox", value: "THU" }).appendTo(span32);
            $(span32).append("&nbsp; 星期四<br />");
            $("<input/>", { type: "checkbox", value: "SAT" }).appendTo(span32);
            $(span32).append("&nbsp; 星期六");

            $(span31).appendTo(weeklyWell);
            $(span32).appendTo(weeklyWell);
            //Hack to fix the well box
            $("<br /><br /><br /><br />").appendTo(weeklyWell);

            $(weeklyWell).appendTo(weeklyTab);

            $(weeklyTab).append("开始时间：&nbsp;");
            $(weeklyTab).append('<select id="WeeklyHours" class="hours" style="width: 60px"></select>');
            $(weeklyTab).append('<select id="WeeklyMinutes" class="minutes" style="width: 60px"></select>');

            $(weeklyTab).appendTo(tabContent);

            //craeting the monthlyTab
            var monthlyTab = $("<div/>", { "class": "tab-pane", id: "Monthly" });

            var monthlyOption1 = $("<div/>", { "class": "well well-small" });
            $("<input/>", { type: "radio", value: "1", name: "MonthlyRadio", checked: "checked" }).appendTo(monthlyOption1);
            $(monthlyOption1).append("&nbsp;每个&nbsp;");
            $("<input/>", { id: "MonthInput", type: "text", value: "1", style: "width: 40px" }).appendTo(monthlyOption1);
            $(monthlyOption1).append("&nbsp;月份的&nbsp;");
            $("<input/>", { id: "DayOfMOnthInput", type: "text", value: "1", style: "width: 40px" }).appendTo(monthlyOption1);
            $(monthlyOption1).append("&nbsp;号");
            $(monthlyOption1).appendTo(monthlyTab);

            var monthlyOption2 = $("<div/>", { "class": "well well-small" });
            $("<input/>", { type: "radio", value: "2", name: "MonthlyRadio" }).appendTo(monthlyOption2);
            $(monthlyOption2).append("&nbsp;每个&nbsp;");
            $("<input/>", { id: "EveryMonthInput", type: "text", value: "1", style: "width: 40px" }).appendTo(monthlyOption2);
            $(monthlyOption2).append("&nbsp;月的&nbsp;");
            $(monthlyOption2).append('<select id="WeekDay" class="day-order-in-month" style="width: 80px"></select>');
            $(monthlyOption2).append('<select id="DayInWeekOrder" class="week-days" style="width: 100px"></select>');
            $(monthlyOption2).appendTo(monthlyTab);

            $(monthlyTab).append("开始时间：&nbsp;");
            $(monthlyTab).append('<select id="MonthlyHours" class="hours" style="width: 60px"></select>：');
            $(monthlyTab).append('<select id="MonthlyMinutes" class="minutes" style="width: 60px"></select>');

            $(monthlyTab).appendTo(tabContent);

            //craeting the yearlyTab
            var yearlyTab = $("<div/>", { "class": "tab-pane", id: "Yearly" });

            var yearlyOption1 = $("<div/>", { "class": "well well-small" });
            $("<input/>", { type: "radio", value: "1", name: "YearlyRadio", checked: "checked" }).appendTo(yearlyOption1);
            $(yearlyOption1).append("&nbsp;每个&nbsp");
            $(yearlyOption1).append('<select id="MonthsOfYear" class="months" style="width: 150px"></select>');
            $(yearlyOption1).append("&nbsp;.&nbsp;");
            $("<input/>", { id: "YearInput", type: "text", value: "1", style: "width: 40px" }).appendTo(yearlyOption1);
            $(yearlyOption1).append("&nbsp;号");
            $(yearlyOption1).appendTo(yearlyTab);

            var yearlyOption2 = $("<div/>", { "class": "well well-small" });
            $("<input/>", { type: "radio", value: "2", name: "YearlyRadio" }).appendTo(yearlyOption2);
            $(yearlyOption2).append('<select id="MonthsOfYear2" class="months" style="width: 110px"></select>');
            $(yearlyOption2).append("&nbsp;的&nbsp;");
            $(yearlyOption2).append('<select id="DayOrderInYear" class="day-order-in-month" style="width: 80px"></select>&nbsp;');
            $(yearlyOption2).append('<select id="DayWeekForYear" class="week-days" style="width: 100px"></select>');
            $(yearlyOption2).appendTo(yearlyTab);

            $(yearlyTab).append("开始时间：&nbsp;");
            $(yearlyTab).append('<select id="YearlyHours" class="hours" style="width: 60px"></select>');
            $(yearlyTab).append('<select id="YearlyMinutes" class="minutes" style="width: 60px"></select>');

            $(yearlyTab).appendTo(tabContent);
            $(tabContent).appendTo(span12);

            //creating the button and results input
            resultsName = $(this).prop("id");
            $(this).prop("name", resultsName);

            $(span12).appendTo(row);
            $(cronOpera).appendTo(row);
            $(cronDescArea).appendTo(row);
            $(row).appendTo(container);
            $(container).appendTo(mainDiv);
            $(cronContainer).append(mainDiv);

            var that = $(this);

            // Hide the original input
            that.hide();

            // Replace the input with an input group
            var $g = $("<div>").addClass("input-group");
            // Add an input
            var $i = $("<input readonly='readonly' name='jq-validation-jobcron'  placeholder='点击右侧按钮选择'>", { type: 'text', placeholder: 'Cron trigger'}).addClass("form-control").val($(that).val());//, readonly: 'readonly'
            $i.appendTo($g);
            // Add the button
            var $b = $("<button class=\"btn btn-default\"><span class=\"fa fa-edit\"></span></button>");
            // Put button inside span
            var $s = $("<span>").addClass("input-group-btn");
            $b.appendTo($s);
            $s.appendTo($g);

            $(this).before($g);

            inputElement = that;
            displayElement = $i;

            $b.popover({
                html: true,
                content: function () {
                    return $(cronContainer).html();
                },
                template: '<div class="popover" style="max-width:500px !important; width:500px"><div class="arrow"></div><div class="popover-inner"><button type="button" class="close" data-dismiss="modal" aria-hidden="true" style="margin:3px 3px 0px 0px;opacity: 3;">×</button><h3 class="popover-title"></h3><div class="popover-content"><p></p></div></div></div>',
                placement: 'bottom'

            }).on('click', function (e) {
                e.preventDefault();
                fillDataOfMinutesAndHoursSelectOptions();
                fillDayWeekInMonth();
                fillInWeekDays();
                fillInMonths();
                $('#CronGenTabs a').click(function (e) {
                    e.preventDefault();
                    $(this).tab('show');
                    //generate();
                });
                $("#CronGenMainDiv select,input").change(function (e) {
                    //generate();
                });
                $("#CronGenMainDiv input").focus(function (e) {
                    //generate();
                });
                $('button.close').click(function (e) {
                    e.preventDefault();
                    $b.click();
                    return false;
                    //generate();
                });
                $("#cron-gen-btn").click(function(e){
                    e.preventDefault();
                    generate();
                    $("#cron-explain-tb thead span").html("（"+inputElement.val()+"）");
                });
                $("#cron-clear-btn").click(function(e){
                    e.preventDefault();
                    // 更新input的值
                    inputElement.val("");
                    displayElement.val("");
                    $("#cron-explain-tb tbody").html("<tr><td style='font-style: italic;'>no  results</td></tr>");
                    $("#cron-explain-tb thead span").empty();
                });
                $("#cron-explain-btn").click(function(e){
                    e.preventDefault();
                    explainCronExpress(inputElement.val());
                    /*$.ajax({
                        "url":"/app/scheduler/job/cron/explain",
                        "data":{"_method_":"GET","cronExpress":inputElement.val()},
                        "type":"POST",
                        "async":false,
                        "error":function(){
                        },
                        "success":function(response){
                            if(response == null || '0000' != response.resultCode){
                            }else{
                                if(response.result != null){
                                    //$("#cron-explain-tb tbody").html('<tr><td>Cron表达式是否有效？ '+response.result.valid+'</td><tr>');
                                    var nextFireList = response.result.nextFireTimeList;
                                    var tbContent = "";
                                    if(nextFireList != null && nextFireList.length >0){
                                        for(var index in nextFireList){
                                            tbContent = tbContent + "<tr><td>"+nextFireList[index]+"</td></tr>";
                                        }
                                        $("#cron-explain-tb thead span").html("（"+inputElement.val()+"）");
                                        $("#cron-explain-tb tbody").html(tbContent);
                                    }

                                }

                            }
                        }
                    });
                    */
                });
            });
            return true;
        }
    });


    var fillInMonths = function () {
        var days = [
            { text: "一月", val: "1" },
            { text: "二月", val: "2" },
            { text: "三月", val: "3" },
            { text: "四月", val: "4" },
            { text: "五月", val: "5" },
            { text: "六月", val: "6" },
            { text: "七月", val: "7" },
            { text: "八月", val: "8" },
            { text: "九月", val: "9" },
            { text: "十月", val: "10" },
            { text: "十一月", val: "11" },
            { text: "十二月", val: "12" }
        ];
        $(".months").each(function () {
            fillOptions(this, days);
        });
    };

    var fillOptions = function (elements, options) {
        for (var i = 0; i < options.length; i++)
            $(elements).append("<option value='" + options[i].val + "'>" + options[i].text + "</option>");
    };
    var fillDataOfMinutesAndHoursSelectOptions = function () {
        for (var i = 0; i < 60; i++) {
            if (i < 24) {
                $(".hours").each(function () { $(this).append(timeSelectOption(i)); });
            }
            $(".minutes").each(function () { $(this).append(timeSelectOption(i)); });
        }
    };
    var fillInWeekDays = function () {
        var days = [
            { text: "星期一", val: "MON" },
            { text: "星期二", val: "TUE" },
            { text: "星期三", val: "WED" },
            { text: "星期四", val: "THU" },
            { text: "星期五", val: "FRI" },
            { text: "星期六", val: "SAT" },
            { text: "星期天", val: "SUN" }
        ];
        $(".week-days").each(function () {
            fillOptions(this, days);
        });

    };
    var fillDayWeekInMonth = function () {
        var days = [
            { text: "第一个", val: "1" },
            { text: "第二个", val: "2" },
            { text: "第三个", val: "3" },
            { text: "第四个", val: "4" }
        ];
        $(".day-order-in-month").each(function () {
            fillOptions(this, days);
        });
    };
    var displayTimeUnit = function (unit) {
        if (unit.toString().length == 1)
            return "0" + unit;
        return unit;
    };
    var timeSelectOption = function (i) {
        return "<option id='" + i + "'>" + displayTimeUnit(i) + "</option>";
    };

    var generate = function () {

        var activeTab = $("ul#CronGenTabs li.active a").prop("id");
        var results = "";
        switch (activeTab) {
            case "MinutesTab":
                results = "0 0/" + $("#MinutesInput").val() + " * 1/1 * ? *";
                break;
            case "HourlyTab":
                switch ($("input:radio[name=HourlyRadio]:checked").val()) {
                    case "1":
                        results = "0 0 0/" + $("#HoursInput").val() + " 1/1 * ? *";
                        break;
                    case "2":
                        results = "0 " + Number($("#AtMinutes").val()) + " " + Number($("#AtHours").val()) + " 1/1 * ? *";
                        break;
                }
                break;
            case "DailyTab":
                switch ($("input:radio[name=DailyRadio]:checked").val()) {
                    case "1":
                        results = "0 " + Number($("#DailyMinutes").val()) + " " + Number($("#DailyHours").val()) + " 1/" + $("#DaysInput").val() + " * ? *";
                        break;
                    case "2":
                        results = "0 " + Number($("#DailyMinutes").val()) + " " + Number($("#DailyHours").val()) + " ? * MON-FRI *";
                        break;
                }
                break;
            case "WeeklyTab":
                var selectedDays = "";
                $("#Weekly input:checkbox:checked").each(function () { selectedDays += $(this).val() + ","; });
                if (selectedDays.length > 0)
                    selectedDays = selectedDays.substr(0, selectedDays.length - 1);
                results = "0 " + Number($("#WeeklyMinutes").val()) + " " + Number($("#WeeklyHours").val()) + " ? * " + selectedDays + " *";
                break;
            case "MonthlyTab":
                switch ($("input:radio[name=MonthlyRadio]:checked").val()) {
                    case "1":
                        results = "0 " + Number($("#MonthlyMinutes").val()) + " " + Number($("#MonthlyHours").val()) + " " + $("#DayOfMOnthInput").val() + " 1/" + $("#MonthInput").val() + " ? *";
                        break;
                    case "2":
                        results = "0 " + Number($("#MonthlyMinutes").val()) + " " + Number($("#MonthlyHours").val()) + " ? 1/" + Number($("#EveryMonthInput").val()) + " " + $("#DayInWeekOrder").val() + "#" + $("#WeekDay").val() + " *";
                        break;
                }
                break;
            case "YearlyTab":
                switch ($("input:radio[name=YearlyRadio]:checked").val()) {
                    case "1":
                        results = "0 " + Number($("#YearlyMinutes").val()) + " " + Number($("#YearlyHours").val()) + " " + $("#YearInput").val() + " " + $("#MonthsOfYear").val() + " ? *";
                        break;
                    case "2":
                        results = "0 " + Number($("#YearlyMinutes").val()) + " " + Number($("#YearlyHours").val()) + " ? " + $("#MonthsOfYear2").val() + " " + $("#DayWeekForYear").val() + "#" + $("#DayOrderInYear").val() + " *";
                        break;
                }
                break;
        }

        // Update original control
        inputElement.val(results);
        // Update display
        displayElement.val(results);
    };

})(jQuery);
