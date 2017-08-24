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
    var $finishItem = $(".page2-container .page2-finishItem");
    var $printItem = $(".page2-container .page2-printItem");
    var $auditItem = $("#page2-audit");

    var $inspection = $("#page2-inspection");
    var $search = $(".page2-container .page2-query");
    var $add = $("#page2-add");
    var $modify = $("#page2-modify");
    var $cancel = $("#page2-cancel");
    var $print = $("#page2-print");
    var $printPatch = $("#page2-printPatch");
    var $import = $("#page2-import");
    var $confirm = $("#page2-confirm");
    var $audit = $("#page2-auditItem");
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
    var checkSelected = [];
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
        checkSelected = [];
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
                $mainTableBox.clearSelected();
            }, function () {
                mainCancelRows = [];
                $mainTableBox.clearSelected();
            });
        });

        $finishItem.off("click");
        $finishItem.click(function () {
            if (mainCancelRows.length === 0) {
                alert("未选中行, 不能确认");
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
            ajaxData("finish", {del: submitDel}, function () {
                mainCancelRows = [];
                $mainTableBox.clearSelected();
            }, function () {
                mainCancelRows = [];
                $mainTableBox.clearSelected();
            });
        });

        $printItem.off("click");
        $printItem.click(function () {
            if (mainCancelRows.length === 0) {
                alert("未选中行");
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
            ajaxData("print", {del: submitDel, type: "all"}, function (data) {
                //data.datas.splice(2, data.datas.length - 1);//只取前两条测试用
                $printArea.render(data.datas);

                var strBodyStyle = "<style>" + document.getElementById("print_code_style").innerHTML + "</style>";
                var htmlStr = strBodyStyle + "<body>" + $("#print_area").html() + "</body>";

                var LODOP = getLodop();
                LODOP.PRINT_INIT("条码打印");
                LODOP.SET_PRINT_STYLE("FontSize", 14);
                LODOP.ADD_PRINT_HTML(8, 10, 300, 400, htmlStr);
                LODOP.PREVIEW();
                /*
                 $("#print_area").css({
                 "height": "auto"
                 , "overflow": "visible"
                 }).printArea();
                 */
                mainCancelRows = [];
                $mainTableBox.clearSelected();
            }, function () {
                mainCancelRows = [];
                $mainTableBox.clearSelected();
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
                    //console.log(data);
                    if (data) {
                        if (moudle === "计划出库" || moudle === "总成计划") {
                            if (data.datas) {
                                $chidTableBox.render(data.datas);
                            }
                            if (data.fileUrl) {
                                location.href = data.fileUrl;
                            }
                        } else {
                            $chidTableBox.render(data);
                        }
                    } else {
                        initDOM(moudle);
                        $("#page2-return").trigger("click");
                    }
                }, function (data) {
                    importSuccess = false;
                    if (data) {
                        $chidTableBox.render(data);
                    } else {
                        initDOM(moudle);
                        $("#page2-return").trigger("click");
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
                    $("#page2-return").trigger("click");
                });
            }
        });

        $auditItem.off("click");
        $auditItem.on("click", function () {
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

        $audit.off("click");
        $audit.on("click", function (e) {
            var arr = $chidTableBox.getSelectedItem();
            if (arr && arr.length > 0) {
                ajaxData("auditItem", {datas: arr}, function (data) {
                    if (data) {
                        $chidTableBox.setData(data);
                    }
                    $chidTableBox.clearSelected();
                    $("#page2-return").trigger("click");
                }, function () {
                    $("#page2-return").trigger("click");
                });
            } else {
                alert("未选中数据!");
            }
        });

        $print.off("click");
        $print.on("click", function () {
            if (moudle === "条码打印") {
                var arr = $chidTableBox.getSelectedItem();
                if (arr && arr.length > 0) {
                    ajaxData("printItem", {del: arr, type: "selected"}, function (data) {
                        $printArea.render(data.datas);
                        var strBodyStyle = "<style>" + document.getElementById("print_code_style").innerHTML + "</style>";
                        var htmlStr = strBodyStyle + "<body>" + $("#print_area").html() + "</body>";

                        var LODOP = getLodop();
                        LODOP.PRINT_INIT("条码打印");
                        LODOP.SET_PRINT_STYLE("FontSize", 14);
                        LODOP.ADD_PRINT_HTM(8, 10, 300, 400, htmlStr);
                        LODOP.PREVIEW();

                        $chidTableBox.clearSelected();
                    }, function () {
                        $chidTableBox.clearSelected();
                    });
                } else {
                    alert("未选中数据!");
                }
            } else {
                $("#print_area").css({
                    "height": "auto"
                    , "overflow": "visible"
                }).printArea();
            }
        });

        $printPatch.off("click");
        $printPatch.on("click", function (e) {
            var arr = $chidTableBox.getSelectedItem();
            if (arr && arr.length > 0) {
                if (arr.length > 1) {
                    alert("只能选择一条数据!");
                } else {
                    var packNumber = prompt("请输入箱签范围", "1-1");
                    if (packNumber) {
                        console.log(packNumber);
                        var str = packNumber.split("-");
                        console.log(str);
                        if (str.length === 2) {
                            ajaxData("printPatch", {del: arr, patch: packNumber}, function (data) {
                                $printArea.render(data.datas);
                                var strBodyStyle = "<style>" + document.getElementById("print_code_style").innerHTML + "</style>";
                                var htmlStr = strBodyStyle + "<body>" + $("#print_area").html() + "</body>";

                                var LODOP = getLodop();
                                LODOP.PRINT_INIT("条码打印");
                                LODOP.SET_PRINT_STYLE("FontSize", 14);
                                LODOP.ADD_PRINT_HTM(10, 10, 300, 400, htmlStr);
                                LODOP.PREVIEW();

                                $chidTableBox.clearSelected();
                            }, function () {
                                $chidTableBox.clearSelected();
                            });
                        } else {
                            alert("输入格式不正确!");
                        }
                    }
                }
            } else {
                alert("未选中数据!");
            }
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
                        var data = JSON.parse($("#import_return_data").val());
                        //console.log(data);
                        if (data.datas) {
                            $chidTableBox.render(data.datas);
                        }
                        if (data.fileUrl) {
                            location.href = data.fileUrl;
                        }
                        var importRes = $("#import_result").val();
                        if (importRes === 0) {
                            importSuccess = true;
                        } else {
                            importSuccess = false;
                        }
                        $("#import_return_data").val("");
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
            if (data.primary)
                primary = data.primary.split(",");
            else
                primary = [];
            if (data.detailPrimary)
                detailPrimary = data.detailPrimary.split(",");
            else
                detailPrimary = [];

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
                    ajaxData(OPERATION.REQUEST_DETAIL, {rely: whereObj, pageSize: detailPageSize, pageIndex: minPageIndex, isHistory: isHistory}, function (data) {
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
                    var obj = {"pageIndex": minPageIndex, "pageSize": detailPageSize, "datas": tmp.keywords, "rely": whereObj, isHistory: isHistory};
                    ajaxData(OPERATION.REQUEST_DETAIL, obj, function (data) {
                        $chidTableBox.render(data.datas);
                    }, function () {
                    });
                },
                checkBoxCallBack: function (index, selected, obj) {
                    if (selected) {
                        whereObj = {};
                        for (var proIndex in detailPrimary) {
                            var proName = detailPrimary[proIndex];
                            whereObj[proName] = obj[proName];
                        }
                        checkSelected.push(whereObj);
                    } else {
                        checkSelected.splice(index, 1);
                    }

                    if (moudle === "总成计划" || moudle === "计划出库") {
                    } else {
                    }
                }
            });

            //console.log("url:" + localStorage.getItem("url"));
            if (moudle === "条码打印") {
                $printArea.createPrintCode({
                    printArea: data.printArea
                });
            } else {
                $printArea.createPrintArea({
                    printArea: data.printArea,
                    type: 2
                });
            }
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
        } else if (moudle === "待检审核") {
            $chidTableBox.show();
            $childINputBox.hide();
            $childINputBox.next().hide();
            $mainInputBox.formDisable();
            $detailList.show();
            $mainTable.hide();
        } else if (moudle === "备货确认" || moudle === "领货确认"
                || moudle === "配送确认") {
            $chidTableBox.show();
            $childINputBox.hide();
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
        switch (moudle) {
            case "报检信息":
            {
                $addItem.css("display", "none");
                $("#page2-submit").css("display", "none");
                $auditItem.css("display", "none");
                $inspection.css("display", "inline-block");
                $confirm.css("display", "none");
                //$import.css("display", "none");
                $deleteItem.css("display", "none");
                $finishItem.css("display", "none");
                $history.css("display", "inline-block");
                $printItem.css("display", "none");

                $add.attr("disabled", "disabled");
                $modify.attr("disabled", "disabled");
                $cancel.attr("disabled", "disabled");
                $import.attr("disabled", "disabled");
                $print.attr("disabled", false);
                $printPatch.css("display", "none");
                break;
            }
            case "待检审核":
            {
                $addItem.css("display", "none");
                $("#page2-submit").css("display", "none");
                $auditItem.css("display", "inline-block");
                $inspection.css("display", "none");
                $confirm.css("display", "none");
                //$import.css("display", "none");
                $deleteItem.css("display", "none");
                $finishItem.css("display", "none");
                $history.css("display", "inline-block");
                $printItem.css("display", "none");

                $add.attr("disabled", "disabled");
                $modify.attr("disabled", "disabled");
                $cancel.attr("disabled", "disabled");
                $import.attr("disabled", "disabled");
                $print.attr("disabled", false);
                $audit.attr("disabled", false);
                $printPatch.css("display", "none");
                break;
            }
            case "备货确认":
            case "领货确认":
            case "配送确认":
            {
                $addItem.css("display", "none");
                $("#page2-submit").css("display", "none");
                $auditItem.css("display", "none");
                $inspection.css("display", "none");
                $confirm.css("display", "none");
                //$import.css("display", "none");
                $deleteItem.css("display", "none");
                $history.css("display", "inline-block");
                $finishItem.css("display", "none");
                $printItem.css("display", "none");

                $add.attr("disabled", "disabled");
                $modify.attr("disabled", "disabled");
                $cancel.attr("disabled", "disabled");
                $import.attr("disabled", "disabled");
                $print.attr("disabled", "disabled");
                $audit.attr("disabled", false);
                $printPatch.css("display", "none");
                break;
            }
            case "分装入库":
            case "分装出库":
            {
                $addItem.css("display", "inline-block");
                $("#page2-submit").css("display", "inline-block");
                $auditItem.css("display", "none");
                $inspection.css("display", "none");
                $confirm.css("display", "none");
                //$import.css("display", "none");
                $deleteItem.css("display", "none");
                $finishItem.css("display", "none");
                $history.css("display", "inline-block");
                $printItem.css("display", "none");

                $add.attr("disabled", "disabled");
                $modify.attr("disabled", false);
                $cancel.attr("disabled", "disabled");
                $import.attr("disabled", "disabled");
                $print.attr("disabled", "disabled");
                $audit.attr("disabled", "disabled");
                $printPatch.css("display", "none");
                break;
            }
            case "计划出库":
            case "总成计划":
            {
                $addItem.css("display", "inline-block");
                $("#page2-submit").css("display", "inline-block");
                $auditItem.css("display", "none");
                $inspection.css("display", "none");
                $confirm.css("display", "inline-block");
                //$import.css("display", "inline-block");
                $deleteItem.css("display", "inline-block");
                $finishItem.css("display", "inline-block");
                $printItem.css("display", "none");
                $history.css("display", "none");

                !type ? $add.attr("disabled", false) : $add.attr("disabled", "disabled");
                !type ? $modify.attr("disabled", false) : $modify.attr("disabled", "disabled");
                !type ? $cancel.attr("disabled", false) : $cancel.attr("disabled", "disabled");
                $import.attr("disabled", false);
                $print.css("diaplay", "none");
                $printPatch.css("display", "none");
                $audit.attr("disabled", "disabled");
                break;
            }
            case "条码打印":
            {
                $addItem.css("display", "none");
                $("#page2-submit").css("display", "none");
                $auditItem.css("display", "none");
                $inspection.css("display", "none");
                $confirm.css("display", "none");
                $deleteItem.css("display", "none");
                $finishItem.css("display", "none");
                $printItem.css("display", "inline-block");
                $history.css("display", "none");

                !type ? $add.attr("disabled", false) : $add.attr("disabled", "disabled");
                !type ? $modify.attr("disabled", false) : $modify.attr("disabled", "disabled");
                !type ? $cancel.attr("disabled", false) : $cancel.attr("disabled", "disabled");
                $import.attr("disabled", "disabled");
                $print.attr("disabled", false);
                $audit.attr("disabled", "disabled");
                $printPatch.css("display", "inline-block");
                break;
            }
            /*case "临时调货":
             case "非生成领料":
             {
             $addItem.css("display", "inline-block");
             $("#page2-submit").css("display", "inline-block");
             $audit.css("display", "none");
             $inspection.css("display", "none");
             $confirm.css("display", "inline-block");
             //$import.css("display", "inline-block");
             $deleteItem.css("display", "inline-block");
             $finishItem.css("display", "none");
             $history.css("display", "none");
             
             !type ? $add.attr("disabled", false) : $add.attr("disabled", "disabled");
             !type ? $modify.attr("disabled", false) : $modify.attr("disabled", "disabled");
             !type ? $cancel.attr("disabled", false) : $cancel.attr("disabled", "disabled");
             $import.attr("disabled", false);
             $print.attr("disabled", false);
             $auditItem.attr("disabled", "disabled");
             break;
             }*/
            case "送检出库":
            case "送检返回":
            case "终端退库":
            case "返修出库":
            case "返修入库":
            {
                $addItem.css("display", "inline-block");
                $("#page2-submit").css("display", "inline-block");
                !type ? $auditItem.css("display", "none") : $auditItem.css("display", "inline-block");
                $inspection.css("display", "none");
                $confirm.css("display", "none");
                //$import.css("display", "none");
                $deleteItem.css("display", "inline-block");
                $finishItem.css("display", "none");
                $history.css("display", "none");
                $printItem.css("display", "none");

                $add.attr("disabled", false);
                $modify.attr("disabled", false);
                $cancel.attr("disabled", false);
                $import.attr("disabled", "disabled");
                $print.css("diaplay", "none");
                !type ? $audit.attr("disabled", "disabled") : $audit.attr("disabled", false);
                $printPatch.css("display", "none");
                break;
            }
            default:
            {
                $addItem.css("display", "inline-block");
                $("#page2-submit").css("display", "inline-block");
                !type ? $auditItem.css("display", "none") : $auditItem.css("display", "inline-block");
                $inspection.css("display", "none");
                $confirm.css("display", "none");
                //$import.css("display", "none");
                $deleteItem.css("display", "inline-block");
                $finishItem.css("display", "none");
                $history.css("display", "none");
                $printItem.css("display", "none");

                $add.attr("disabled", false);
                $modify.attr("disabled", false);
                $cancel.attr("disabled", false);
                $import.attr("disabled", false);
                $print.css("diaplay", "none");
                $audit.attr("disabled", "disabled");
                $printPatch.css("display", "none");
                break;
            }
        }

        if (moudle === "待检入库")
            $auditItem.css("display", "none");
    }

    function dataIsReadOnly(isReadOnly) {
        if (isReadOnly) {
            $mainInputBox.formDisable();
            $("#page2-submit").css("display", "none");
            $auditItem.css("display", "none");
            $inspection.css("display", "none");
            //$childINputBox.hide();
            //$childINputBox.next().hide();
            $add.attr("disabled", "disabled");
            $modify.attr("disabled", "disabled");
            $cancel.attr("disabled", "disabled");
            $import.attr("disabled", "disabled");
            $print.attr("disabled", false);
            $audit.attr("disabled", false);
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
                var obj = {"pageIndex": minPageIndex, "pageSize": detailPageSize, "datas": tmp.keywords, "rely": whereObj, isHistory: isHistory};
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