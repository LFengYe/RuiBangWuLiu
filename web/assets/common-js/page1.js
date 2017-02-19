(function () {
    var $inputBox = $(".page1-container .page1-input-box");
    var $buttonArea = $(".page1-container .page1-button-area");
    var $tableBox = $(".page1-container .page1-show-table");
    var $dateQueryBtn = $(".wc-page1-form .page1-query");
    var $dateInputs = $(".wc-page1-form input.wc-control");
    var primary = [];

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
    var modifyRow = null;
    var selectedSet = [];

    function initDOM() {
        ajaxData(OPERATION.CREATE, {}, function (data) {
            primary = data.primary.split(",");
            $inputBox.insertInputForm({
                controls: data.control,
                mustWrite: data.mustwrite,
                requesFun: function (data, callback) {  //事件选择框  数据加载 的 接口
                    ajaxData(OPERATION.REQUEST_TABLE, data, function (data) {
                        callback(data);
                    }, function () {
                    });
                },
                selectpanel: sp  //选择框对象
            });
            $tableBox.insertTable({
                titles: data.titles,
                datas: data.datas,
                unique: data.unique,
                dbclickRowCallBack: function (index, obj) {
                    $inputBox.objInInputs(obj);
                    modifyRow = index;
                },
                clickRowCallBack: function (index, obj) {
                    if (selectedSet[index]) {
                        selectedSet[index] = null;
                    } else {
                        selectedSet[index] = obj;
                    }
                }
            });
        }, function () {
            $inputBox.html("");
            $tableBox.html("");
        });
    }

    function bindEvt() {
        $("#page1-add").on("click", function (e) {
            if ($inputBox.isFinishForm() && (!modifyRow)) {

                var obj = $inputBox.getInputValObj(false);
                if (!$tableBox.isUnique(obj)) {
                    return false;
                }
                $tableBox.add(0, obj);
                submitDatas.add.push(obj);
                console.log(submitDatas.add);
                selectedSet = [];
                $inputBox.clearInputsArea();
            }
        });
        $("#page1-modify").on("click", function (e) {
            if (modifyRow >= 0 && $inputBox.isFinishForm()) {
                var obj = $inputBox.getInputValObj(true);
                $tableBox.update(modifyRow, obj);

                var whereObj = new Object();
                for (var proIndex in primary) {
                    var proName = primary[proIndex];
                    whereObj[proName] = obj[proName];
                    delete obj[proName];
                }

                submitDatas.update.push(obj);
                submitDatas.update.push(whereObj);
                
                console.log(submitDatas.update);
                modifyRow = null;
                $inputBox.clearInputsArea();
            }
        });
        $("#page1-delete").on("click", function (e) {
            var arr = [];
            for (var index in selectedSet) {
                console.log(index);
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
        });
        $("#page1-query").on("click", function (e) {
            var data = $inputBox.getInputValObj();
            ajaxData(OPERATION.QUERY_ITEM, {datas: data}, function (data) {
                $tableBox.render(data);
            }, function () {
                alert("未查询到任何结果");
            });
        });
        $("#page1-submit").on("click", function (e) {
            //submitDatas.update = $tableBox.getAllDatas();
            if (submitDatas.add.length == 0 && submitDatas.del.length == 0 && submitDatas.update.length == 0) {
                alert("您当前没有新增任何信息");
                return;
            }
            ajaxData(OPERATION.SUBMIT, submitDatas, function (data) {
                alert("提交成功");
                submitDatas.add = [];
                submitDatas.del = [];
                submitDatas.update = [];
                selectedSet = [];
            }, function () {
            });
        });
    }

    ajaxPage1 = function () {
        initDOM();
        bindEvt();
    };
})();

//点一个菜单选项   显示对应的page  
//          根据对应的URL   module请求数据     初始化页面
//          绑定事件