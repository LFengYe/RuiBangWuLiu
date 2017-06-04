(function () {
    var $mainTableBox = $(".page3-container .page3-main-table .page3-show-table");
    var $mainInputBox = $(".page3-container .page3-detail-list .page3-main-input-box");
    var $chidTableBox = $(".page3-container .page3-detail-list .page3-show-table");
    var $detailList = $(".page3-container .page3-detail-list");
    var $mainTable = $(".page3-container .page3-main-table");
    var $pageTurn = $(".page3-container .page3-pagination li a");
    var $search = $(".page3-container .page3-query");
    var $export = $(".page3-container .page3-export");


    var OPERATION = {
        CREATE: "create",
        REQUEST_DETAIL: "request_detail",
        REQUEST_PAGE: "request_page"
    };

    $detailList.hide();

    var maxInpage = 20;

    var dataType = "";

    var pageIndex = 1;

    function initDOM(moudle) {
        if (moudle === "退货出库报表") {
            $("#partStatusSelect").parent(".wc-group").css("display", "inline-block");
        } else {
            $("#partStatusSelect").parent(".wc-group").css("display", "none");
        }
        $(".start-time").val("");
        $(".end-time").val("");
        var tmp = JSON.parse(serializeJqueryElement($(".page3-container .wc-page3-form")));
        var request = {};
        request.type = "create";
        if (tmp.startTime && tmp.endTime) {
            request.start = tmp.startTime;
            request.end = tmp.endTime;
        }
        if (tmp.partStatus) {
            request.partStatus = tmp.partStatus;
        }
        ajaxData(OPERATION.CREATE, request, function (arr) {
            $mainTableBox.html("");
            //$("<div></div>").appendTo($mainTableBox).insertTwoHeaderTable({
            $mainTableBox.insertTwoHeaderTable({
                title0: arr.titles,
                title1: null,
                datas: arr.datas,
                dbclickRowCallBack: function (name, maps) {
                    //$mainInputBox.objInInputs(maps);
                    request.name = name;
                    request.datas = maps;
                    ajaxData(OPERATION.REQUEST_DETAIL, request, function (data) {
                        $chidTableBox.insertTable({
                            titles: data.titles,
                            datas: data.datas
                        });
                        $detailList.show();
                        $mainTable.hide();
                    }, function () {
                    });
                }
            });
        }, function () {

        });

    }

    function bindEvt() {
        $("#partStatusSelect").off("change");
        $("#partStatusSelect").on("change", function () {
            $(this).prev().val($(this).val());
        });
        
        $("#page3-return").off("click");
        $("#page3-return").click(function () {
            $detailList.hide();
            $mainTable.show();
        });
        
        $export.off("click");
        $export.click(function (e) {
            var tmp = JSON.parse(serializeJqueryElement($(".page3-container .wc-page3-form")));
            var request = {};
            request.type = "export";
            if (tmp.startTime && tmp.endTime) {
                request.start = tmp.startTime;
                request.end = tmp.endTime;
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
                console.log(data);
                $mainTableBox.render(data.datas);
            }, function () {});
        });
    }

    ajaxPage3 = function (moudle) {
        initDOM(moudle);
        bindEvt();
    };
})();


