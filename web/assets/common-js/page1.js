(function () {
    var $inputBox = $(".page1-container .page1-input-box");
    var $buttonArea = $(".page1-container .page1-button-area");
    var $tableBox = $(".page1-container .page1-show-table");
    var $dateQueryBtn = $(".wc-page1-form .page1-query");
    var $dateInputs = $(".wc-page1-form input.wc-control");

    var $printArea = $("#print_area");

    var OPERATION = {
        CREATE: "create",
        REQUEST_TABLE: "request_table",
        QUERY_ITEM: "query_item",
        SUBMIT: "submit",
        QUERY_ON_DATE: "query_on_date",
        IMPORT: "import",
        EXPORT: "export"
    };
    var submitDatas = {
        add: [],
        update: [],
        del: []
    };
    var primary = [];
    var inputNames = {};
    var modifyRow = null;
    var selectedSet = [];
    var pageSize = 15;

    function initData() {
        submitDatas = {
            add: [],
            update: [],
            del: []
        };
        primary = [];
        inputNames = {};
        modifyRow = null;
        selectedSet = [];
        pageSize = 15;
    }

    function initDOM(module) {
        initData();
        ajaxData(OPERATION.CREATE, {}, function (data) {
            primary = data.primary.split(",");
            for (var i in data.control) {
                var txt = data.control[i].split(',')[0];
                inputNames[i] = txt;
            }

            $inputBox.insertInputForm({
                controls: data.control,
                mustWrite: data.mustwrite,
                requesFun: function (data, callback) {  //事件选择框  数据加载 的 接口
                    ajaxData(OPERATION.REQUEST_TABLE, data, function (data) {
                        callback(data);
                    }, function () {
                    });
                },
                selectpanel: sp, //选择框对象
                lastInputCallBack: function () {
                    $("#page1-add").trigger("click");
                },
                tableInputCallBack: function () {
                    if ($inputBox.isFinishForm()) {
                        var obj = $inputBox.getInputValObj(true);
                        if (!$tableBox.isUnique(obj)) {
                            return false;
                        }
                        if ($inputBox.checkValue() && $inputBox.calculateValue()) {
                            $tableBox.add(0, obj);
                            submitDatas.add.push(obj);
                            selectedSet = [];
                        }
                    }
                }
            });

            $tableBox.insertTable({
                titles: data.titles,
                datas: data.datas,
                unique: data.unique,
                primary: primary,
                dataCount: data.counts,
                pageSize: pageSize,
                dbclickRowCallBack: function (index, obj) {
                    $inputBox.objInInputs(obj);
                    $inputBox.formDisable(primary);
                    modifyRow = index;
                },
                clickRowCallBack: function (index, obj) {
                    if (selectedSet[index]) {
                        //selectedSet.splice(index, 1);
                        selectedSet[index] = null;
                    } else {
                        selectedSet[index] = obj;
                    }
                },
                pageCallBack: function (pageIndex, keyword) {
                    var tmp = JSON.parse(keyword);
                    var obj = {"pageIndex": pageIndex, "pageSize": pageSize, "datas": tmp.keywords, "rely": "{}"};
                    ajaxData("request_page", obj, function (data) {
                        $tableBox.render(data.datas);
                        $tableBox.page(data.counts, pageIndex, pageSize);
                    }, function () {
                    });
                },
                searchCallBack: function (keyword) {
                    //console.log("search key:" + keyword);
                    var tmp = JSON.parse(keyword);
                    var obj = {"pageIndex": 1, "pageSize": pageSize, "datas": tmp.keywords, "rely": "{}"};
                    ajaxData("request_page", obj, function (data) {
                        $tableBox.render(data.datas);
                        $tableBox.page(data.counts, 1, pageSize);
                    }, function () {
                    });
                }
            });

            moduleOperate(module);

            $printArea.createPrintArea({
                printArea: data.printArea
                , type: 1
            });
        }, function () {
            $inputBox.html("");
            $tableBox.html("");
        });
    }

    function bindEvt(module) {
        $("#page1-add").off("click");
        $("#page1-add").on("click", function (e) {
            $("#page1-add").attr("disabled", true);
            if ($inputBox.isFinishForm() && (!modifyRow)) {
                var obj = $inputBox.getInputValObj(true);
                //console.log(obj);
                if (!$tableBox.isUnique(obj)) {
                    $("#page1-add").attr("disabled", false);
                    return false;
                }
                if ($inputBox.checkValue() && $inputBox.calculateValue()) {
                    $tableBox.add(0, obj);
                    submitDatas.add.push(obj);
                    selectedSet = [];
                    $inputBox.clearInputsArea();
                    if (module == "分装入库" || module == "分装出库") {
                        $("#page1-submit").trigger("click");
                    }
                }
            } else {
                if (modifyRow) {
                    alert("当前修改状态, 请点击【修改】按钮");
                }
            }
            $("#page1-add").attr("disabled", false);
        });
        $("#page1-modify").off("click");
        $("#page1-modify").on("click", function (e) {
            $("#page1-modify").attr("disabled", true);
            if (modifyRow >= 0 && $inputBox.isFinishForm()) {
                var obj = $inputBox.getInputValObj(true);
                $tableBox.update(modifyRow, obj);

                var updateObj = deepCopy(obj);
                var whereObj = new Object();
                for (var proIndex in primary) {
                    var proName = primary[proIndex];
                    whereObj[proName] = updateObj[proName];
                    delete updateObj[proName];
                }

                submitDatas.update.push(updateObj);
                submitDatas.update.push(whereObj);

                modifyRow = null;
                $inputBox.clearInputsArea();
            } else {
                alert("请先双击要修改的数据行");
            }
            $("#page1-modify").attr("disabled", false);
        });
        $("#page1-delete").off("click");
        $("#page1-delete").on("click", function (e) {
            $("#page1-delete").attr("disabled", true);
            var arr = [];
            for (var index in selectedSet) {
                if (selectedSet[index]) {
                    arr.push(index);

                    var whereObj = new Object();
                    for (var proIndex in primary) {
                        var proName = primary[proIndex];
                        whereObj[proName] = selectedSet[index][proName];
                    }
                    submitDatas.del.push(whereObj);
                }
            }
            $tableBox.del2(arr);
            selectedSet = [];
            $("#page1-delete").attr("disabled", false);
        });
        $("#page1-query").off("click");
        $("#page1-query").on("click", function (e) {
            var data = $inputBox.getInputValObj();
            ajaxData(OPERATION.QUERY_ITEM, {datas: data}, function (data) {
                $tableBox.render(data);
            }, function () {
                alert("未查询到任何结果");
            });
        });
        $("#page1-submit").off("click");
        $("#page1-submit").on("click", function (e) {
            if (submitDatas.add.length == 0 && submitDatas.del.length == 0 && submitDatas.update.length == 0) {
                alert("您当前没有新增任何信息");
                return;
            }
            ajaxData(OPERATION.SUBMIT, submitDatas, function (data) {
                submitDatas.add = [];
                submitDatas.del = [];
                submitDatas.update = [];
                selectedSet = [];
                initDOM(module);
            }, function () {
                submitDatas.add = [];
                submitDatas.del = [];
                submitDatas.update = [];
                selectedSet = [];
            });
        });
        $("#page1-import").off("click");
        $("#page1-import").on("click", function (e) {
            displayLayer(2, "import_page.html?method=import", "数据导入", function () {
                initDOM();
            });
        });
        $("#page1-export").off("click");
        $("#page1-export").on("click", function (e) {
            var obj = {"datas": $(".jtb-header input").val()};
            ajaxData("export", obj, function (data) {
                location.href = data.fileUrl;
            }, function () {
            });
        });
        $("#page1-print").off("click");
        $("#page1-print").on("click", function (e) {
            var inboundCount = 0;
            for (var i = 0; i < selectedSet.length; i++) {
                var obj = selectedSet[i];
                if (obj) {
                    inboundCount += obj.operateAmount;
                }
            }
            var obj = {};
            obj.inboundCount = inboundCount;
            $printArea.controlData(obj);
            $printArea.render(selectedSet);
            $printArea.printHtml();
            /*
             $("#print_area").css({
             "height": "auto"
             , "overflow": "visible"
             }).printArea();
             */
        });
    }

    function moduleOperate(module) {
        //console.log(module);
        switch (module) {
            case "分装入库":
            case "分装出库":
                $("#page1-add").css("display", "inline-block");
                $("#page1-modify").css("display", "none");
                $("#page1-delete").css("display", "none");
                $("#page1-query").css("display", "none");
                $("#page1-submit").css("display", "none");
                $("#page1-import").css("display", "none");
                $("#page1-export").css("display", "none");
                break;
            default:
                $("#page1-add").css("display", "inline-block");
                $("#page1-modify").css("display", "inline-block");
                $("#page1-delete").css("display", "inline-block");
                $("#page1-query").css("display", "inline-block");
                $("#page1-submit").css("display", "inline-block");
                $("#page1-import").css("display", "inline-block");
                $("#page1-export").css("display", "inline-block");
        }
    }

    ajaxPage1 = function (module) {
        initDOM(module);
        bindEvt(module);
    };
})();

//点一个菜单选项   显示对应的page  
//          根据对应的URL   module请求数据     初始化页面
//          绑定事件