(function () {
    var $mainTableBox = $(".page3-container .page3-main-table .page3-show-table");
    var $mainInputBox = $(".page3-container .page3-detail-list .page3-main-input-box");
    var $chidTableBox = $(".page3-container .page3-detail-list .page3-show-table");
    var $thirdTableBox = $(".page3-container .page3-third-list .page3-show-table");
    var $thirdList = $(".page3-container .page3-third-list");
    var $detailList = $(".page3-container .page3-detail-list");
    var $mainTable = $(".page3-container .page3-main-table");
    var $pageTurn = $(".page3-container .page3-pagination li a");
    var $search = $(".page3-container .page3-query");
    var $export = $(".page3-container .page3-export");
    var $keysword = $(".page3-container").find("input[name='keywords']");
    var $pageLeft = $(".page3-container .page3-left");
    var $pageRight = $(".page3-container .page3-right");

    var OPERATION = {
        CREATE: "create",
        REQUEST_DETAIL: "request_detail",
        REQUEST_PAGE: "request_page"
    };

    var pageSize = 15;
    var dataType = "";
    var pageIndex = 1;

    function initData(moudle) {
        $mainTable.show();
        $detailList.hide();
        $thirdList.hide();
        pageSize = 15;
        pageIndex = 1;
        if (moudle === "退货出库报表") {
            $("#partStatusSelect").parent(".wc-group").css("display", "inline-block");
        } else {
            $("#partStatusSelect").parent(".wc-group").css("display", "none");
        }
        $(".keywords").val("");
        $(".start-time").val(getPreCarryOverDate() + ' 00:00:00');
        $(".end-time").val(getNowDateShort() + ' 23:59:59');
    }

    function initDOM(moudle) {
        initData(moudle);
        getReportData();
    }

    function getReportData() {
        var tmp = JSON.parse(serializeJqueryElement($(".page3-container .wc-page3-form")));
        var request = {};
        request.type = "create";
        request.pageIndex = pageIndex;
        request.pageSize = pageSize;
        if (tmp.startTime && tmp.endTime) {
            request.start = tmp.startTime;
            request.end = tmp.endTime;
        }
        if (tmp.keywords) {
            request.datas = tmp.keywords;
        }
        if (tmp.partStatus) {
            request.partStatus = tmp.partStatus;
        }
        ajaxData(OPERATION.CREATE, request, function (arr) {
            $mainTableBox.html("");
            if (!arr || !arr.datas)
                pageIndex = pageIndex - 1;
            //$("<div></div>").appendTo($mainTableBox).insertTwoHeaderTable({
            $mainTableBox.insertTwoHeaderTable({
                title0: arr.titles,
                title1: null,
                datas: arr.datas,
                pageSize: pageSize,
                dbclickRowCallBack: function (module, maps) {
                    request.pageIndex = 1;
                    request.pageSize = pageSize;
                    request.start = null;
                    request.datas = maps;
                    ajax(module, "report.do", OPERATION.REQUEST_DETAIL, request, function (data) {
                        $detailList.show();
                        $mainTable.hide();
                        $thirdList.hide();
                        $chidTableBox.insertTwoHeaderTable({
                            title0: data.titles,
                            title1: null,
                            datas: data.datas,
                            pageSize: pageSize,
                            dbclickRowCallBack: function (module, maps) {
                                request.pageIndex = 1;
                                request.pageSize = pageSize;
                                request.start = null;
                                request.datas = maps;
                                ajax(module, "report.do", OPERATION.REQUEST_DETAIL, request, function (data) {
                                    $thirdList.show();
                                    $detailList.hide();
                                    $mainTable.hide();
                                    $thirdTableBox.insertTwoHeaderTable({
                                        title0: data.titles,
                                        title1: null,
                                        datas: data.datas
                                    });
                                }, function () {});
                            }
                        });
                    }, function () {});
                }
            });
        }, function () {
            pageIndex = pageIndex - 1;
        });
    }

    function bindEvt(moudle) {
        $("#partStatusSelect").off("change");
        $("#partStatusSelect").on("change", function () {
            $(this).prev().val($(this).val());
        });

        $(".page3-detail-list .page3-return").off("click");
        $(".page3-detail-list .page3-return").click(function () {
            $detailList.hide();
            $thirdList.hide();
            $mainTable.show();
        });

        $(".page3-third-list .page3-return").off("click");
        $(".page3-third-list .page3-return").click(function () {
            $thirdList.hide();
            $mainTable.hide();
            $detailList.show();
        });

        $export.off("click");
        $export.click(function (e) {
            var tmp = JSON.parse(serializeJqueryElement($(".page3-container .wc-page3-form")));
            var request = {};
            request.type = "export";
            request.pageIndex = 1;
            request.pageSize = 99999999;
            if (tmp.startTime && tmp.endTime) {
                request.start = tmp.startTime;
                request.end = tmp.endTime;
            }
            if (tmp.keywords) {
                request.datas = tmp.keywords;
            }
            if (tmp.partStatus) {
                request.partStatus = tmp.partStatus;
            }
            ajaxData("create", request, function (data) {
                location.href = data.fileUrl;
            }, function () {
            });
        });

        $search.off("click");
        $search.click(function (e) {
            pageIndex = 1;
            getReportData();
            /*
             var tmp = JSON.parse(serializeJqueryElement($(".page3-container .wc-page3-form")));
             var request = {};
             request.type = "search";
             if (tmp.startTime && tmp.endTime) {
             request.start = tmp.startTime;
             request.end = tmp.endTime;
             //request = {start: tmp.startTime, end: tmp.endTime};
             }
             if (tmp.partStatus) {
             request.partStatus = tmp.partStatus;
             }
             ajaxData("create", request, function (data) {
             //console.log(data);
             $mainTableBox.render(data.datas);
             }, function () {});
             */
        });
        /*
         $keysword.change(function () {
         });
         $keysword.keypress(function (event) {
         switch (event.keyCode) {
         case 13:
         if (moudle === "良品库存") {
         var keyword = $(this).val();
         $mainTableBox.filter(keyword);
         }
         break;
         }
         });
         */

        $pageLeft.off("click");
        $pageLeft.click(function (e) {
            pageIndex = pageIndex - 1;
            if (pageIndex > 0) {
                getReportData();
            } else {
                pageIndex = 1;
                alert("当前页为第一页");
            }
        });

        $pageRight.off("click");
        $pageRight.click(function (e) {
            pageIndex = pageIndex + 1;
            getReportData();
        });
    }

    ajaxPage3 = function (moudle) {
        initDOM(moudle);
        bindEvt(moudle);
    };
})();


