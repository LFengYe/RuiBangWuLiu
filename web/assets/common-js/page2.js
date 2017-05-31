
(function () {
    var $mainTable = $(".page2-container .page2-main-table");
    var $mainTableBox = $(".page2-container .page2-main-table .page2-show-table");

    var $detailList = $(".page2-container .page2-detail-list");
    var $mainInputBox = $(".page2-container .page2-detail-list .page2-main-input-box");
    var $childINputBox = $(".page2-container .page2-detail-list .page2-child-input-box");
    var $chidTableBox = $(".page2-container .page2-detail-list .page2-show-table");

    var $printArea = $("#print_area");

    var $history = $(".page2-container .page2-history");
    var $addItem = $(".page2-container .page2-addItem");
    var $deleteItem = $(".page2-container .page2-deleteItem");
    var $audit = $("#page2-audit");
    var $inspection = $("#page2-inspection");
    var $search = $(".page2-container .page2-query");
    var $add = $("#page2-add");
    var $modify = $("#page2-modify");
    var $cancel = $("#page2-cancel");
    var $print = $("#page2-print");
    var $import = $("#page2-import");
    var $confirm = $("#page2-confirm");
    //var $search_on_keyword = $(".page2-container .page2-keyword-query button");

    var OPERATION = {
        CREATE: "create",
        ADD: "add",
        REQUEST_TABLE: "request_table",
        REQUEST_DETAIL: "request_detail",
        REQUEST_PAGE: "request_page",
        REQUEST_ON_DATE: "request_on_date",
        SUBMIT: "submit",
        AUDIT: "audit"
    };

    $detailList.hide();

    var detailPageSize = 999;
    var minPageIndex = 1;
    var pageSize = 20;
    var primary = [];
    var detailPrimary = [];

    var mainClickRow = null;
    var modifyRow = null;
    var oriObj = null;

    var mainCancelRows = [];
    var cancelRows = {};
    var whereObj = {};
    var submitDatas = {
        add: [],
        update: [],
        del: []
    };
    var operate = "add";
    var importSuccess = false;
    var isHistory = 0;

    function initData() {
        $(".start-time").val(getNowDateShort() + ' 00:00:00');
        $(".end-time").val(getNowDateShort() + ' 23:59:59');
        primary = [];
        detailPrimary = [];

        mainClickRow = null;
        modifyRow = null;
        oriObj = null;

        mainCancelRows = [];
        cancelRows = {};
        whereObj = {};
        submitDatas = {
            add: [],
            update: [],
            del: []
        };
        operate = "add";
        importSuccess = false;
        if ($history.find(":checkbox").is(":checked")) {
            isHistory = 1;
        } else {
            isHistory = 0;
        }
    }

    function bindEvt(moudle) {
        $history.find(":checkbox").off("click");
        $history.find(":checkbox").click(function (e) {
            //console.log($(this).is(':checked'));
            if ($(this).is(':checked')) {
                isHistory = 1;
            } else {
                isHistory = 0;
            }
        });

        //$(".page2-container .wc-page2-form input").val("");
        $search.off("click");
        $search.click(function (e) {
            /*
             var $dateInputs = $(".page2-container .wc-page2-form input");
             var key = $dateInputs.eq(0).val();
             var start = $dateInputs.eq(1).val();
             var end = $dateInputs.eq(2).val();
             */
            var tmp = JSON.parse(serializeJqueryElement($(".page2-container .wc-page2-form")));
            var timeInterval;
            if (tmp.startTime && tmp.endTime) {
                timeInterval = {start: tmp.startTime, end: tmp.endTime};
            } else {
                timeInterval = {};
            }

            var request = {datas: tmp.keywords, rely: timeInterval, pageSize: pageSize, pageIndex: minPageIndex, isHistory: isHistory};
            ajaxData(OPERATION.REQUEST_ON_DATE, request, function (data) {
                //console.log(data);
                $mainTableBox.render(data.datas);
                $mainTableBox.page(data.counts, minPageIndex, pageSize);
            }, function () {
            });

        });

        $addItem.off("click");
        $addItem.click(function () {
            importSuccess = false;
            ajaxData(OPERATION.ADD, {}, function (data) {
                operate = "add";
                updateInputBox(data);
                moudleDiaplay(moudle, 0);
                moudleOperate(moudle, 0);
                /*
                $modify.removeAttr("disabled");
                $cancel.removeAttr("disabled");
                $add.removeAttr("disabled");
                $import.removeAttr("disabled");
                $print.attr("disabled", "disabled");

                $chidTableBox.show();
                $childINputBox.show();
                $childINputBox.next().show();
                $detailList.show();
                $mainTable.hide();
                */
            }, function () {
            });
        });

        $deleteItem.off("click");
        $deleteItem.click(function () {
            if (mainCancelRows.length === 0) {
                alert("未选中行, 不能删除");
                return;
            }
            var arr = [];
            var submitDel = [];
            for (var index in mainCancelRows) {
                if (mainCancelRows[index]) {
                    arr.push(index);
                    var whereObj = new Object();
                    for (var proIndex in primary) {
                        var proName = primary[proIndex];
                        whereObj[proName] = mainCancelRows[index][proName];
                    }
                    submitDel.push(whereObj);
                }
            }
            ajaxData("delete", {del: submitDel}, function (data) {
                $mainTableBox.del2(arr);
                mainCancelRows = [];
            }, function (data) {
            });
        });

        $("#page2-return").off("click");
        $("#page2-return").click(function () {
            importSuccess = false;
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
            $import.removeAttr("disabled");
        });

        $confirm.off("click");
        $confirm.on("click", function () {
            if (!importSuccess) {
                alert("计划添加未成功!不能确认");
                return;
            }
            var item = $mainInputBox.getInputValObj(true);
            whereObj = {};
            for (var proIndex in primary) {
                var proName = primary[proIndex];
                whereObj[proName] = item[proName];
            }
//            var selectData = $mainTableBox.getAllDatas()[mainClickRow];
            ajaxData("confirm", {datas: whereObj}, function (data) {
                $("#page2-return").trigger("click");
            }, function () {
                $("#page2-return").trigger("click");
            });
        });

        $inspection.off("click");
        $inspection.click(function () {
            if (!$mainInputBox.isFinishForm()) {
                return;
            }
            var item = $mainInputBox.getInputValObj(true);
            var obj = {
                item: item,
                details: null
            };
            var selectData = $mainTableBox.getAllDatas()[mainClickRow];
            ajaxData("inspection", obj, function (data) {
                $("#page2-return").trigger("click");
                for (var index in data) {
                    selectData[index] = data[index];
                }
                $mainTableBox.update(mainClickRow, selectData);
            }, function () {
                //$("#page2-return").trigger("click");
            });
        });

        $("#page2-submit").off("click");
        $("#page2-submit").click(function () {
            if (operate === "add") {
                var arr = $chidTableBox.getAllDatas();
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
                    details: arr,
                    operate: operate
                };
                ajaxData(OPERATION.SUBMIT, obj, function (data) {
                    importSuccess = true;
                    if (data) {
                        $chidTableBox.render(data);
                    } else {
                        $("#page2-return").trigger("click");
                        initDOM(moudle);
                    }
                }, function (data) {
                    importSuccess = false;
                    if (data) {
                        $chidTableBox.render(data);
                    } else {
                        $("#page2-return").trigger("click");
                        initDOM(moudle);
                    }
                });
            }
            if (operate === "modify") {
                submitDatas.operate = operate;
                if (submitDatas.del.length == 0 && submitDatas.update.length == 0 && submitDatas.add.length == 0) {
                    alert("您当前没有新增任何信息");
                    return;
                }
                ajaxData(OPERATION.SUBMIT, submitDatas, function (data) {
                    submitDatas.add = [];
                    submitDatas.del = [];
                    submitDatas.update = [];
                    $("#page2-return").trigger("click");
                }, function () {
                });
            }
        });

        $audit.off("click");
        $audit.on("click", function () {
            var item = $mainInputBox.getInputValObj(true);
            whereObj = {};
            for (var proIndex in primary) {
                var proName = primary[proIndex];
                whereObj[proName] = item[proName];
            }
            var selectData = $mainTableBox.getAllDatas()[mainClickRow];
            ajaxData(OPERATION.AUDIT, {datas: whereObj}, function (data) {
                $("#page2-return").trigger("click");
                for (var index in data) {
                    selectData[index] = data[index];
                }
                $mainTableBox.update(mainClickRow, selectData);
            }, function () {
            });
        });

        $add.off("click");
        $add.on("click", function (e) {
            $add.attr("disabled", "disabled");
            $childINputBox.parentFieldValue($mainInputBox);
            if (!modifyRow) {
                if ($childINputBox.isFinishForm()) {
                    if ($childINputBox.checkValue() && $childINputBox.calculateValue()) {
                        var obj = $childINputBox.getInputValObj(true);
                        if (!$chidTableBox.isUnique(obj)) {
                            $add.attr("disabled", false);
                            return false;
                        }
                        $chidTableBox.add(0, obj);
                        submitDatas.add.push(obj);
                        $childINputBox.clearInputsArea();
                    }
                }
            } else {
                alert("当前修改状态, 请点击【修改】按钮");
            }
            $add.attr("disabled", false);
        });

        $modify.off("click");
        $modify.on("click", function (e) {
            console.log(modifyRow);
            if (modifyRow >= 0 && $childINputBox.isFinishForm()) {
                if ($childINputBox.checkValue() && $childINputBox.calculateValue()) {
                    var obj = $childINputBox.getInputValObj(true);
                    $.extend(oriObj, obj);
                    $chidTableBox.update(modifyRow, oriObj);

                    var updateObj = deepCopy(obj);
                    var whereObj = new Object();
                    for (var proIndex in detailPrimary) {
                        var proName = detailPrimary[proIndex];
                        whereObj[proName] = updateObj[proName];
                        delete updateObj[proName];
                    }

                    submitDatas.update.push(updateObj);
                    submitDatas.update.push(whereObj);

                    modifyRow = null;
                    $childINputBox.clearInputsArea();
                    $childINputBox.RemoveformDisable();
                }
            }
        });

        $cancel.off("click");
        $cancel.on("click", function (e) {
            var arr = [];
            //var submitDel = [];
            for (var index in cancelRows) {
                if (cancelRows[index]) {
                    arr.push(index);
                    var whereObj = new Object();
                    for (var proIndex in detailPrimary) {
                        var proName = detailPrimary[proIndex];
                        whereObj[proName] = cancelRows[index][proName];
                    }
                    //submitDel.push(whereObj);
                    submitDatas.del.push(whereObj);
                }
            }
            //submitDatas.del.push(submitDel);
            $chidTableBox.del2(arr);
            cancelRows = [];
        });

        $print.off("click");
        $print.on("click", function () {
            $("#print_area").css({
                "height": "auto"
                , "overflow": "visible"
            }).printArea();
        });

        $import.off("click");
        $import.on("click", function (e) {
            if (!$mainInputBox.isFinishForm()) {
                return;
            }
            var item = $mainInputBox.getInputValObj(true);
            $childINputBox.parentFieldValue($mainInputBox);
            var child = $childINputBox.getInputValObj(true);
            //console.log("import_page.html?method=importDetail&item=" + JSON.stringify(item) + "&detail=" + JSON.stringify(child));
            displayLayer(2, "import_page.html?method=importDetail&item=" + escape(JSON.stringify(item)) + "&detail=" + escape(JSON.stringify(child)),
                    "数据导入", function () {
                        $chidTableBox.render(JSON.parse($("#import_return_data").val()));
                        var importRes = $("#import_result").val(0);
                        if (importRes === 0) {
                            importSuccess = true;
                        } else {
                            importSuccess = false;
                        }
                    });
        });
    }

    function initDOM(moudle) {
        initData();
        $("#page2-return").trigger("click");
        var tmp = JSON.parse(serializeJqueryElement($(".page2-container .wc-page2-form")));
        var timeInterval;
        if (tmp.startTime && tmp.endTime) {
            timeInterval = {start: tmp.startTime, end: tmp.endTime};
        } else {
            timeInterval = {};
        }
        ajaxData(OPERATION.CREATE, {isHistory: isHistory, datas: tmp.keywords, rely: timeInterval}, function (data) {
            primary = data.primary.split(",");
            detailPrimary = data.detailPrimary.split(",");
            $mainTableBox.insertTable({
                titles: data.titles,
                datas: data.datas,
                unique: data.unique,
                primary: primary,
                dataCount: data.counts,
                pageSize: 20,
                isLocalSearch: false,
                clickRowCallBack: function (index, obj) {
                    if (mainCancelRows[index]) {
                        mainCancelRows[index] = null;
                    } else {
                        mainCancelRows[index] = obj;
                    }
                },
                dbclickRowCallBack: function (index, maps) {
                    //var whereObj = new Object();
                    importSuccess = true;
                    mainClickRow = index;

                    whereObj = {};
                    for (var proIndex in primary) {
                        var proName = primary[proIndex];
                        whereObj[proName] = maps[proName];
                    }
                    ajaxData(OPERATION.REQUEST_DETAIL, {rely: whereObj, pageSize: detailPageSize, pageIndex: minPageIndex}, function (data) {
                        operate = "modify";
                        $mainInputBox.objInInputs(maps);
                        if (moudle === "报检信息") {
                        } else {
                            if (data.datas) {
                                $chidTableBox.render(data.datas);
                                $childINputBox.parentFieldValue($mainInputBox);
                                $printArea.controlData(maps);
                                $printArea.render(data.datas);
                            } else {
                                alert("没有明细!");
                            }
                        }
                        moudleDiaplay(moudle, 1);
                        moudleOperate(moudle, 1);
                        dataIsReadOnly(data.readOnly);
                    }, function () {
                    });
                },
                pageCallBack: function (pageIndex, keyword) {
                    //console.log(keyword);
                    var tmp = JSON.parse(keyword);
                    var timeInterval;
                    if (tmp.startTime && tmp.endTime) {
                        timeInterval = {start: tmp.startTime, end: tmp.endTime};
                    } else {
                        timeInterval = {};
                    }
                    var obj = {"pageIndex": pageIndex, "pageSize": pageSize, "datas": tmp.keywords, "rely": timeInterval, isHistory: isHistory};
                    ajaxData(OPERATION.REQUEST_PAGE, obj, function (data) {
                        $mainTableBox.render(data.datas);
                        $mainTableBox.page(data.counts, pageIndex, pageSize);
                    }, function () {
                    });
                }
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
                selectpanel: sp,
                lastInputCallBack: function () {
                    $childINputBox.getFirstInput().trigger("focus");
                }
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
                selectpanel: sp,
                lastInputCallBack: function () {
                    $add.trigger("click");
                }/*,
                 tableInputCallBack: function (resarr) {
                 console.log(resarr);
                 }*/
            });
            $chidTableBox.insertTable({
                titles: data.detailTitles,
                unique: data.detailUnique,
                primary: data.detailPrimary.split(","),
                isLocalSearch: true,
                dbclickRowCallBack: function (index, obj) {
                    $childINputBox.objInInputs(obj);
                    $childINputBox.formDisable(data.detailPrimary.split(","));
                    modifyRow = index;
                    oriObj = obj;
                },
                clickRowCallBack: function (index, obj) {
                    if (cancelRows[index]) {
                        cancelRows[index] = null;
                    } else {
                        cancelRows[index] = obj;
                    }
                },
                searchCallBack: function (keyword) {
                    var tmp = JSON.parse(keyword);
                    var obj = {"pageIndex": minPageIndex, "pageSize": detailPageSize, "datas": tmp.keywords, "rely": whereObj};
                    ajaxData(OPERATION.REQUEST_DETAIL, obj, function (data) {
                        $chidTableBox.render(data.datas);
                    }, function () {
                    });
                }
            });
            $printArea.createPrintArea({
                printArea: data.printArea
            });
            moudleOperate(moudle, 1);
        }, function () {
        });
    }
    
    // type = 0 --- Add操作
    // type = 1 --- modify操作
    function moudleDiaplay(moudle, type) {
        if (moudle === "报检信息") {
            $mainInputBox.formDisable(primary);
            $childINputBox.hide();
            $childINputBox.next().hide();
            $mainTable.hide();
            $chidTableBox.hide();
            $detailList.show();
            $childINputBox.parentFieldValue($mainInputBox);
        } else if (moudle === "待检审核" || moudle === "备货确认" || moudle === "领货确认"
                || moudle === "配送确认" || moudle === "良品库存") {
            $chidTableBox.show();
            $childINputBox.hide();
            $childINputBox.next().hide();
            $mainInputBox.formDisable();
            $detailList.show();
            $mainTable.hide();
        } else {
            $chidTableBox.show();
            $childINputBox.show();
            $childINputBox.next().show();
            type && $mainInputBox.formDisable();
            $detailList.show();
            $mainTable.hide();
        }
    }

    function moudleOperate(moudle, type) {
        if (moudle === "报检信息") {
            $addItem.css("display", "none");
            $("#page2-submit").css("display", "none");
            $audit.css("display", "none");
            $inspection.css("display", "inline-block");
            $confirm.css("display", "none");
            //$import.css("display", "none");
            $deleteItem.css("display", "none");
            $history.css("display", "inline-block");

            $add.attr("disabled", "disabled");
            $modify.attr("disabled", "disabled");
            $cancel.attr("disabled", "disabled");
            $import.attr("disabled", "disabled");
        } else if (moudle === "待检审核" || moudle === "备货确认" ||
                moudle === "领货确认" || moudle === "配送确认") {
            $addItem.css("display", "none");
            $("#page2-submit").css("display", "none");
            $audit.css("display", "inline-block");
            $inspection.css("display", "none");
            $confirm.css("display", "none");
            //$import.css("display", "none");
            $deleteItem.css("display", "none");
            $history.css("display", "inline-block");

            $add.attr("disabled", "disabled");
            $modify.attr("disabled", "disabled");
            $cancel.attr("disabled", "disabled");
            $import.attr("disabled", "disabled");
        } else if (moudle === "计划出库" || moudle === "临时调货" ||
                moudle === "非生产领料") {
            $addItem.css("display", "inline-block");
            $("#page2-submit").css("display", "inline-block");
            $audit.css("display", "none");
            $inspection.css("display", "none");
            $confirm.css("display", "inline-block");
            //$import.css("display", "inline-block");
            $deleteItem.css("display", "inline-block");
            $history.css("display", "none");

            !type ? $add.attr("disabled", false) : $add.attr("disabled", "disabled");
            !type ? $modify.attr("disabled", false) : $modify.attr("disabled", "disabled");
            !type ? $cancel.attr("disabled", false) : $cancel.attr("disabled", "disabled");
            $import.attr("disabled", false);
        } else {
            $addItem.css("display", "inline-block");
            $("#page2-submit").css("display", "inline-block");
            !type ? $audit.css("display", "none") : $audit.css("display", "inline-block");
            $inspection.css("display", "none");
            $confirm.css("display", "none");
            //$import.css("display", "none");
            $deleteItem.css("display", "inline-block");
            $history.css("display", "none");

            $add.attr("disabled", false);
            $modify.attr("disabled", false);
            $cancel.attr("disabled", false);
            $import.attr("disabled", "disabled");
        }
        if (moudle === "待检入库")
            $audit.css("display", "none");
    }

    function dataIsReadOnly(isReadOnly) {
        if (isReadOnly) {
            $mainInputBox.formDisable();
            $("#page2-submit").css("display", "none");
            $audit.css("display", "none");
            $inspection.css("display", "none");
            $childINputBox.hide();
            $childINputBox.next().hide();
        }
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
            selectpanel: sp,
            lastInputCallBack: function () {
                $childINputBox.getFirstInput().trigger("focus");
            }
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
            selectpanel: sp,
            lastInputCallBack: function () {
                $add.trigger("click");
            }/*,
             tableInputCallBack: function (resarr) {
             console.log(resarr);
             }*/
        });
        $chidTableBox.insertTable({
            titles: data.detailTitles,
            unique: data.detailUnique,
            primary: data.detailPrimary.split(","),
            dbclickRowCallBack: function (index, obj) {
                $childINputBox.objInInputs(obj);
                $childINputBox.formDisable(data.detailPrimary.split(","));
                modifyRow = index;
                oriObj = obj;
            },
            clickRowCallBack: function (index, obj) {
                if (cancelRows[index]) {
                    cancelRows[index] = null;
                } else {
                    cancelRows[index] = obj;
                }
            },
            searchCallBack: function (keyword) {
                var tmp = JSON.parse(keyword);
                var obj = {"pageIndex": minPageIndex, "pageSize": detailPageSize, "datas": tmp.keywords, "rely": whereObj};
                ajaxData(OPERATION.REQUEST_DETAIL, obj, function (data) {
                    $chidTableBox.render(data.datas);
                }, function () {
                });
            }
        });
    }

    ajaxPage2 = function (moudle) {
        initDOM(moudle);
        bindEvt(moudle);
    };
})();