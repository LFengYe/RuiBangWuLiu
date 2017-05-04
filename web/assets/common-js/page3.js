(function () {
    var $mainTableBox = $(".page3-container .page3-main-table .page3-show-table");
    var $mainInputBox = $(".page3-container .page3-detail-list .page3-main-input-box");
    var $chidTableBox = $(".page3-container .page3-detail-list .page3-show-table");
    var $detailList = $(".page3-container .page3-detail-list");
    var $mainTable = $(".page3-container .page3-main-table");
    var $pageTurn = $(".page3-container .page3-pagination li a");
    var $search = $(".page3-container .page3-query");


    var OPERATION = {
        CREATE: "create",
        REQUEST_DETAIL: "request_detail",
        REQUEST_PAGE: "request_page"
    };

    $detailList.hide();

    var maxInpage = 20;

    var dataType = "";

    var pageIndex = 1;

    var request = {
        startIndex: 1,
        endIndex: 30,
        dataType: "2013-3-7&2013-3-18" | "关键字"
    };

    function initDOM() {
        ajaxData(OPERATION.CREATE, {}, function (arr) {
            console.log(arr);
            $mainTableBox.html("");
            $("<div></div>").appendTo($mainTableBox).insertTwoHeaderTable({
                title0: arr.titles,
                title1: null,
                datas: arr.datas,
                dbclickRowCallBack: function (index, maps) {
                    //$mainInputBox.objInInputs(maps);
                    ajaxData(OPERATION.REQUEST_DETAIL, {datas: maps}, function (data) {
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
            /*
             console.log(arr);
             var data;
             $mainTableBox.html("");
             for (var i = 0; i < arr.length; i++) {
             data = arr[i];
             
             }
             */
        }, function () {

        });

    }

    function bindEvt() {
        $("#page3-return").click(function () {
            $detailList.hide();
            $mainTable.show();
        });
        $search.click(function (e) {
            var $dateInputs = $(".page3-container .wc-page3-form input");
            var key = $dateInputs.eq(0).val();
            var start = $dateInputs.eq(1).val();
            var end = $dateInputs.eq(2).val();
            if (start != "" && end != "") {
                request.dataType = start + "&" + end;
            } else {
                request.dataType = '';
            }
            request.dataType += "|" + key;
            request.startIndex = 1;
            request.endIndex = maxInpage;

            ajaxData(OPERATION.REQUEST_PAGE, request, function (data) {
                $mainTableBox.render(data);
            }, function () {
            });

        });
        $pageTurn.click(function (e) {
            if ($(this).hasClass("page3-left")) {
                if (pageIndex >= 2) {
                    pageIndex--;
                }
            } else if ($(this).hasClass("page3-right")) {
                pageIndex++;
            }
            request.startIndex = (pageIndex - 1) * maxInpage + 1;
            request.endIndex = request.startIndex + maxInpage - 1;
            ajaxData(OPERATION.REQUEST_PAGE, request, function (data) {
                $mainTableBox.render(data);
            }, function () {
                pageIndex--;                  //请求不到数据时,将页数恢复原值
            });
        });
    }

    ajaxPage3 = function (moudle) {
        initDOM();
        bindEvt();
    };
})();


