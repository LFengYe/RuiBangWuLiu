
(function () {
    var $mainTable = $(".page2-container .page2-main-table");
    var $mainTableBox = $(".page2-container .page2-main-table .page2-show-table");

    var $detailList = $(".page2-container .page2-detail-list");
    var $mainInputBox = $(".page2-container .page2-detail-list .page2-main-input-box");
    var $childINputBox = $(".page2-container .page2-detail-list .page2-child-input-box");
    var $chidTableBox = $(".page2-container .page2-detail-list .page2-show-table");
    
    var $printArea = $("#print_area");

    var $addItem = $(".page2-container .page2-addItem");
//    var $pageTurn = $(".page2-container .page2-pagination li a");
    var $search = $(".page2-container .page2-query");
    var $add = $("#page2-add");
    var $modify = $("#page2-modify");
    var $cancel = $("#page2-cancel");
    var $print = $("#page2-print");
    //var $search_on_keyword = $(".page2-container .page2-keyword-query button");

    var OPERATION = {
        CREATE: "create",
        ADD: "add",
        REQUEST_TABLE: "request_table",
        REQUEST_DETAIL: "request_detail",
        REQUEST_PAGE: "request_page",
        REQUEST_ON_DATE: "request_on_date",
        SUBMIT: "submit"
    };

    $detailList.hide();

    var detailPageSize = 999;
    var minPageIndex = 1;
    var pageSize = 20;
    var primary = [];

    var modifyRow = null;
    var oriObj = null;

    var cancelRows = {};
    var detailWhereObj = {};

    function bindEvt() {
        $search.off("click");
        $search.click(function (e) {
            var $dateInputs = $(".page2-container .wc-page2-form input");
            var key = $dateInputs.eq(0).val();
            var start = $dateInputs.eq(1).val();
            var end = $dateInputs.eq(2).val();
            var timeInterval;
            if (start != "" && end != "") {
                timeInterval = {start: start, end: end};
            } else {
                timeInterval = {};
            }
            
            var request = {datas: key, rely: timeInterval, pageSize: pageSize,pageIndex: minPageIndex};
            ajaxData(OPERATION.REQUEST_ON_DATE, request, function (data) {
                console.log(data);
                $mainTableBox.render(data.datas);
                $mainTableBox.page(data.counts, minPageIndex, pageSize);
            }, function () {
            });

        });
        
        $addItem.off("click");
        $addItem.click(function () {
            ajaxData(OPERATION.ADD, {}, function (data) {
                updateInputBox(data);
                $detailList.fadeIn(500);
                $mainTable.fadeOut(200);
            }, function(){});
        });
        
        $("#page2-return").off("click");
        $("#page2-return").click(function () {
            $childINputBox.clearInputsArea();
            $mainInputBox.clearInputsArea();
            $chidTableBox.emptyTable();
            $mainInputBox.RemoveformDisable();
            $childINputBox.RemoveformDisable();
            $detailList.hide();
            $mainTable.show();
            $("#page2-submit").removeAttr("disabled");
            $add.removeAttr("disabled");
            $modify.removeAttr("disabled");
            $cancel.removeAttr("disabled");

        });
        
        $("#page2-submit").off("click");
        $("#page2-submit").click(function () {
            var arr = $chidTableBox.getAllDatas();
            for (var index in arr) {
                arr[index].listNumber = parseInt(index) + 1;
            }
            if (arr.length == 0) {
                console.log("请将表格添加数据后再提交");
                alert("请将表格添加数据后再提交！");
                return;
            }
            if (!$mainInputBox.isFinishForm()) {
                return;
            }
            var item = $mainInputBox.getInputValObj(true);
            var obj = {
                item: item,
                details: arr
            };
            ajaxData(OPERATION.SUBMIT, obj, function (data) {
                alert("提交成功!");
                $("#page2-return").trigger("click");
                $mainTableBox.add(0, item);
            }, function () {
            });

        });

        $add.off("click");
        $add.on("click", function (e) {
            $print.attr("disabled", "disabled");
            $("#page1-add").attr("disabled", true);
            $childINputBox.parentFieldValue($mainInputBox);
            if ($childINputBox.isFinishForm() && (!modifyRow)) {
                $childINputBox.calculateValue();
                var obj = $childINputBox.getInputValObj(true);
                if (!$chidTableBox.isUnique(obj)) {
                    $("#page1-add").attr("disabled", false);
                    return false;
                }
                $chidTableBox.add(0, obj);
                $childINputBox.clearInputsArea();
            }
            $("#page1-add").attr("disabled", false);
        });

        $modify.off("click");
        $modify.on("click", function (e) {
            if (modifyRow >= 0 && $childINputBox.isFinishForm()) {
                var obj = $childINputBox.getInputValObj();
                $.extend(oriObj, obj);
                $chidTableBox.update(modifyRow, oriObj);

                modifyRow = null;
                $childINputBox.clearInputsArea();
                $childINputBox.RemoveformDisable();
            }
        });

        $cancel.off("click");
        $cancel.on("click", function (e) {
            console.log(cancelRows);
            var arr = [];
            for (var index in cancelRows) {
                if (cancelRows[index] != null) {
                    arr.push(cancelRows[index]);
                }
            }
            console.log(arr);
            $chidTableBox.del2(arr);
        });
        
        $print.off("click");
        $print.on("click", function () {
            $("#print_area").css({
                "height" : "auto"
                ,"overflow" : "visible"
            }).printArea();
        });
    }

    function initDOM() {
        ajaxData(OPERATION.CREATE, {}, function (data) {
            primary = data.primary.split(",");
            $mainTableBox.insertTable({
                titles: data.titles,
                datas: data.datas,
                unique: data.unique,
                dataCount: data.counts,
                pageSize: 20,
                isLocalSearch: false,
                dbclickRowCallBack: function (index, maps) {
                    //var whereObj = new Object();
                    detailWhereObj = {};
                    for (var proIndex in primary) {
                        var proName = primary[proIndex];
                        detailWhereObj[proName] = maps[proName];
                    }
                    ajaxData(OPERATION.REQUEST_DETAIL, {rely: detailWhereObj, pageSize: detailPageSize, pageIndex: minPageIndex}, function (data) {
                        if (data.datas) {
                            $mainInputBox.objInInputs(maps);
                            $("#page2-submit").attr("disabled", "disabled");
                            $add.attr("disabled", "disabled");
                            $modify.attr("disabled", "disabled");
                            $cancel.attr("disabled", "disabled");
                            $print.removeAttr("disabled");
                            $chidTableBox.render(data.datas);
                            $mainInputBox.formDisable();
                            $childINputBox.formDisable();
                            $detailList.show();
                            $mainTable.hide();
                            
                            $printArea.controlData(maps);
                            $printArea.render(data.datas);
                        } else {
                            alert("没有明细!");
                        }
                    }, function () {
                    });
                },
                pageCallBack: function (pageIndex, keyword) {
                    var obj = {"pageIndex": pageIndex, "pageSize": pageSize, "datas": keyword, "rely": "{}"};
                    ajaxData(OPERATION.REQUEST_PAGE, obj, function (data) {
                        $mainTableBox.render(data.datas);
                        $mainTableBox.page(data.counts, pageIndex, pageSize);
                    }, function () {
                    });
                }/*,
                searchCallBack: function (keyword) {
                    var obj = {"pageIndex": minPageIndex, "pageSize": pageSize, "datas": keyword, "rely": "{}"};
                    ajaxData(OPERATION.REQUEST_PAGE, obj, function (data) {
                        $mainTableBox.render(data.datas);
                        $mainTableBox.page(data.counts, 1);
                    }, function () {
                    });
                }*/
            });
            $mainInputBox.insertInputForm({
                controls: data.control,
                mustWrite: data.mustwrite,
                requesFun: function (data, callback) {  //事件选择框  数据加载 的 接口
                    ajaxData(OPERATION.REQUEST_TABLE, data, function (data) {
                        callback(data);
                    }, function () {
                    });
                },
                selectpanel: sp //选择框对象
            });
            $childINputBox.insertInputForm({
                controls: data.detailControl,
                mustWrite: data.detailMustwrite,
                requesFun: function (data, callback) {  //事件选择框  数据加载 的 接口
                    ajaxData(OPERATION.REQUEST_TABLE, data, function (data) {
                        callback(data);
                    }, function () {
                    });
                },
                selectpanel: sp, //选择框对象
                lastInputCallBack: function () {
                }
            });
            $chidTableBox.insertTable({
                titles: data.detailTitles,
                unique: data.detailUnique,
                dbclickRowCallBack: function (index, obj) {
                    $childINputBox.objInInputs(obj);
                    $childINputBox.formDisable(data.unique2);
                    modifyRow = index;
                    oriObj = obj;
                },
                clickRowCallBack: function (index, obj) {
                    if (cancelRows[index]) {
                        cancelRows[index] = null;
                    } else {
                        cancelRows[index] = index;
                    }
                },
                /*pageCallBack: function (pageIndex, keyword) {
                    var obj = {"pageIndex": pageIndex, "pageSize": 15, "datas": keyword, "rely": "{}"};
                    ajaxData(OPERATION.REQUEST_PAGE, obj, function (data) {
                        $chidTableBox.render(data.datas);
                        $chidTableBox.page(data.counts, pageIndex);
                    }, function () {
                    });
                },*/
                searchCallBack: function (keyword) {
                    var obj = {"pageIndex": minPageIndex, "pageSize": detailPageSize, "datas": keyword, "rely": detailWhereObj};
                    ajaxData(OPERATION.REQUEST_DETAIL, obj, function (data) {
                        $chidTableBox.render(data.datas);
                    }, function () {
                    });
                }
            });
            $printArea.createPrintArea({
                printArea: data.printArea
            });
            $("#page2-return").trigger("click");
        }, function () {
        });

    }

    function updateInputBox(data) {
        $mainInputBox.insertInputForm({
            controls: data.control,
            mustWrite: data.mustwrite,
            requesFun: function (data, callback) {  //事件选择框  数据加载 的 接口
                ajaxData(OPERATION.REQUEST_TABLE, data, function (data) {
                    callback(data);
                }, function () {
                });
            },
            selectpanel: sp //选择框对象
        });
        $childINputBox.insertInputForm({
            controls: data.detailControl,
            mustWrite: data.detailMustwrite,
            requesFun: function (data, callback) {  //事件选择框  数据加载 的 接口
                ajaxData(OPERATION.REQUEST_TABLE, data, function (data) {
                    callback(data);
                }, function () {
                });
            },
            selectpanel: sp, //选择框对象
            lastInputCallBack: function () {
            }
        });
    }

    ajaxPage2 = function () {
        initDOM();
        bindEvt();
    };
})();