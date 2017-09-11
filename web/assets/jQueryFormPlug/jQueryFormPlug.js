//该表单插件负责如下
//根据数据动态生成DOM
//将obj对象填入表单 
//将表单的值转为为obj对象
//对非事件输入框进行合法性的验证
//对事件输入框  调用回调函数   并处于等待填值状态
//整个表单提交前要对必填选项进行验证  
//enter键自动切换
//将表单置空
//将表单变成不可编辑状态
$.fn.insertInputForm = function (options) {
    var _default_ = {
        controls: {}, //输入框描述
        mustWrite: [], //必填项目
        lastInputCallBack: function () {
            console.log("我是一个回调函数！");
        }, //最后一个输入框  对enter键的回调函数响应
        tableInputCallBack: null,
        width: 1000,
        requesFun: function () {
        },
        selectpanel: null
    };
    options = $.extend(_default_, options);
    //console.log(options.tableInputCallBack);
    var _pro_ = {
        getDOM: function () {
            var inputResult = "";
            var controls = options.controls;
            for (var i in controls) {
                var txt = controls[i].split(',')[0];
                if (controls[i].indexOf('@') >= 0) {
                    //如果只有一个@符, 并且不是是@table(供选择值只有一个)
                    if ((controls[i].indexOf('@') === controls[i].lastIndexOf("@")) && controls[i].indexOf("@table") < 0) {
                        var value = controls[i].split("@")[1];
                        if (controls[i].split(',')[1] === 'hidden') {
                            inputResult += "<input class='input' type='hidden' name='" + i + "' value='" + value + "'/>";
                        } else if (controls[i].split(',')[1] === 'parent') {
                            inputResult += "<input class='input parent' type='hidden' name='" + i + "' value='" + value + "'/>";
                        } else {
                            inputResult += "<div><span>" + txt + "</span>:<input class='event special input' name='" + i + "' value='" + value + "'></div>";
                        }
                    } else {
                        inputResult += "<div><span>" + txt + "</span>:<input class='event input' name='" + i + "'/></div>";
                    }
                } else if (controls[i].split(',')[1] === 'date') {
                    inputResult += "<div><span>" + txt + "</span>:<input class='input' type='text' onfocus='WdatePicker({dateFmt: \"yyyy-MM-dd HH:mm:ss\"})' name='" + i + "' value='" + getMaxDate() + "'/></div>";
                } else if (controls[i].split(',')[1] === 'bool') {
                    inputResult += "<div><span>" + txt + "</span>:<input class='radio' type='radio' name='" + i + "' value='1' />是<input  class='radio' type='radio' name='" + i + "' value='2'/>否</div>";
                } else if (controls[i].split(',')[1] === 'img') {
                    inputResult += "<div><span>" + txt + "</span>:<input  class='input' type='file' name='" + i + "'/></div>";
                } else if (controls[i].split(',')[1] === 'password') {
                    inputResult += "<div><span>" + txt + "</span>:<input  class='input' type='password' name='" + i + "'/></div>";
                } else if (controls[i].split(',')[1] === 'hidden') {
                    inputResult += "<input class='input' type='hidden' name='" + i + "'/>";
                } else if (controls[i].split(',')[1] === 'select') {
                    var selects = controls[i].split(",").slice(2);

                    inputResult += "<div><span>" + txt + "</span>:<input class='input' type='hidden' name='" + i + "' value='" + selects[0] + "'/>";
                    inputResult += "<select class='select'>";

                    for (var str in selects) {
                        inputResult += "<option value='" + selects[str] + "'>" + selects[str] + "</option>";
                    }
                    inputResult += "</select>";
                    inputResult += "</div>";
                } else if (controls[i].split(',')[1] === 'calculate') {
                    inputResult += "<input class='input calculate' type='hidden' name='" + i + "'/>";
                } else if (controls[i].split(',')[1] === 'parent') {
                    inputResult += "<input class='input parent' type='hidden' name='" + i + "'/>";
                } else if (controls[i].split(',')[1] === 'check') {
                    inputResult += "<div><span>" + txt + "</span>:<input class='input check' name='" + i + "'/></div>";
                } else {
                    inputResult += "<div><span>" + txt + "</span>:<input class='input' name='" + i + "'/></div>";
                }
            }

            this.html("<div class='input-area'>" + inputResult + "</div>").addClass("jQueryForm");

            this.$inputs = this.find(".input-area input.input");
            this.$radios = this.find(".input-area input.radio");
            this.$select = this.find(".input-area select");
            this.$calculate = this.find(".input-area input.calculate");
            this.$check = this.find(".input-area .check");
            this.parent = this.find(".input-area input.parent");
            //将只有一个选项的输入框默认输入这个选项
        },
        checkValue: function () {
            for (var i = 0; i < this.$check.length; i++) {
                var item = this.$check[i];
                var name = item.name;
                var value = item.value;

                var obj = options.controls[name];
                var check = obj.split(",").slice(0).map(function (i) {
                    return i.slice(0);
                });
                var referenceObj = this.$inputs.filter("[name=" + check[3] + "]");
                if (check[2] === "小于") {
                    if (eval(referenceObj.val()) < eval(value)) {
                        item.focus();
                        item.value = referenceObj.val();
                        alert(check[0] + "值不能大于" + referenceObj.val());
                        return false;
                    }
                }
                if (check[2] === "大于") {
                    if (eval(referenceObj.val()) > eval(value)) {
                        item.focus();
                        item.value = referenceObj.val();
                        alert(check[0] + "值不能小于" + referenceObj.val());
                        return false;
                    }
                }
            }
            return true;
        },
        calculateValue: function () {
            for (var i = 0; i < this.$calculate.length; i++) {
                var item = this.$calculate[i];
                var calFields = options.controls[item.name].split(',');
                var firstFieldVal = this.$inputs.filter("[name=" + calFields[2] + "]").val();
                var secondFieldVal = this.$inputs.filter("[name=" + calFields[3] + "]").val();
                if (firstFieldVal && secondFieldVal) {
                    if (calFields[4] === "/")
                        item.value = Math.ceil(firstFieldVal / secondFieldVal);
                    if (calFields[4] === "*")
                        item.value = Math.ceil(firstFieldVal * secondFieldVal);
                    if (calFields[4] === "+")
                        item.value = Math.ceil(firstFieldVal + secondFieldVal);
                    if (calFields[4] === "-")
                        item.value = Math.ceil(firstFieldVal - secondFieldVal);
                } else {
                    alert("计算[" + calFields[0] + "]值出现错误!");
                    return false;
                }
            }
            return true;
        },
        parentFieldValue: function (parent) {
            var obj = parent.getInputValObj(true);
            for (var i = 0; i < this.parent.length; i++) {
                var item = this.parent[i];
                item.value = obj[item.name];
                var specialItem = options.controls[item.name].split(',');
                if (specialItem[2] && obj[specialItem[2]]) {
                    item.value = obj[specialItem[2]];
                }
            }
        },
        lastInputEnter: function () {
            this.$inputs.last().bind("keypress", function (e) {
                if (e.keyCode == "13") {
                    $(this).blur();
                    options.lastInputCallBack();
                }
            });
        },
        getFirstInput: function () {
            for (var i = 0; i < $(this.$inputs).length; i++) {
                if (!($(this.$inputs[i]).hasClass("special")) && !($(this.$inputs[i]).attr("type") === "hidden")) {
                    return $(this.$inputs[i]);
                }
            }
        },
        getInputValObj: function (containHidden) {
            var obj = {};
            var name, value, type;

            for (var i = 0; i < this.$inputs.length; i++) {
                if (containHidden) {
                    name = this.$inputs.eq(i).attr("name");
                    value = this.$inputs.eq(i).val();
                    obj[name] = value;
                } else {
                    type = this.$inputs.eq(i).attr("type");
                    if (type === "hidden")
                        continue;
                    name = this.$inputs.eq(i).attr("name");
                    value = this.$inputs.eq(i).val();
                    obj[name] = value;
                }
            }
            for (var i = 0; i < this.$radios.length; i++) {
                name = this.$radios.eq(i).attr("name");
                if (this.$radios.eq(i).is(":checked")) {
                    value = this.$radios.eq(i).attr("value");
                    if (value == "1") {
                        obj[name] = true;
                    } else if (value == "2") {
                        obj[name] = false;
                    }
                } else {
                    obj[name] = "";
                }
            }
            //console.log(this.extraDatas);
            $.extend(obj, this.extraDatas);
            return obj;
        },
        objInInputs: function (obj) {
            for (var i in obj) {
                if (obj[i]) {
                    this.$inputs.filter("[name='" + i + "']").val(obj[i]);
                }
                var selectInput = this.$inputs.filter("[name='" + i + "']").next("select");
                if (selectInput && obj[i]) {
                    selectInput.val(obj[i]);
                }
            }
            return this;
        },
        isEmpty: function () {
            for (var i = 0; i < this.$inputs.length; i++) {
                if (this.$inputs.eq(i).val() != "") {
                    return false;
                }
            }
            return true;
        },
        isFinishForm: function () {
            var flag = true;
            for (var j = 0; j < options.mustWrite.length; j++) {
                var $input = this.$inputs.filter("[name=" + options.mustWrite[j] + "]");
                if ($input.val() == '') {
                    $input.focus();
                    var name = options.mustWrite[j];
                    var mm = options.controls[name].split(',')[0];
                    alert(mm + "必填！");
                    flag = false;
                }
            }
            return flag;
        },
        clearInputsArea: function () {
            for (var i = 0; i < $(this.$inputs).length; i++) {
                if (!($(this.$inputs[i]).hasClass("special")) && !($(this.$inputs[i]).attr("type") === "hidden")) {
                    $(this.$inputs[i]).val("");
                }
            }
            return this;
        },
        NormalInutBlur: function () {
            var that = this;
            this.$inputs.not(".event").on('blur', function () {
                //获得该非事件输入框的属性名
                var name = $(this).attr("name");
                //获得合法的标准
                var arr = options.controls[name].split(',');
                var txt = arr[0];
                var type = arr[1];
                var maxLength = arr[2];
                //获得该输入框的value值并判断是否符合标准
                var val = $(this).val();
                that.testNormalInputslegal(txt, type, maxLength, val, $(this));
            });
            return this;
        },
        testNormalInputslegal: function (txt, type, maxLength, val, $input) {
            switch (type) {
                case 'string':
                    if (val.length > maxLength) {
                        alert(txt + "不能超过" + maxLength + "个字节");
                        $input.val('').trigger('focus');
                    }
                    break;
                case 'number':
                    if (isNaN(val) || val.length > maxLength) {
                        alert('请输入长度不超过' + maxLength + '位数字');
                        $input.val('').trigger('focus');
                    }
                    break;
                case 'bool':
                    if (val != "是" && val != "否" && val != '') {
                        alert('请输入是或否');
                        $input.val('').trigger('focus');

                    }
                    break;
                default:
                    break;
            }
        },
        inputAtuoToggle: function () {
            //绑定input对象enter键自动切换的事件~~~
            this.$inputs.keypress(function (e) {
                if (e.keyCode === 13) {
                    $(this).parent().next().children("input").trigger("focus");
                }
            });
        },
        formDisable: function (arr) {
            if (!arr) {
                this.$inputs.attr("disabled", "disabled");
                for (var i = 0; i < this.$inputs.length; i++) {
                    var selectInput = $(this.$inputs[i]).next("select");
                    //console.log(selectInput);
                    if (selectInput) {
                        selectInput.attr("disabled", "disabled");
                    }
                }
            } else {
                for (var i = 0; i < arr.length; i++) {
                    this.$inputs.filter("[name=" + arr[i] + "]").attr("disabled", "disabled");
                }
            }

        },
        RemoveformDisable: function () {
            this.$inputs.removeAttr("disabled");
            for (var i = 0; i < this.$inputs.length; i++) {
                var selectInput = $(this.$inputs[i]).next("select");
                //console.log(selectInput);
                if (selectInput) {
                    selectInput.attr("disabled", false);
                }
            }
        },
        EvtInputFocus: function () {
            this.extraDatas = {};
            var that = this;
            this.$inputs.filter(".event").on('click', function () {
                var name = $(this).attr("name");
                var $input = $(this);
                var obj = options.controls;
                var arr, txt, relys;
                //进行ajax请求     返回的是table
                if (obj[name].indexOf("@table") >= 0) {
                    arr = obj[name].split(':')[0].split(',');
                    relys = arr.slice(2).map(function (i) {
                        return i.slice(1);
                    });
                    var $rely, rely_obj = {};
                    for (var i = 0; i < relys.length; i++) { //判断所依赖的字段是否填写完整
                        $rely = that.$inputs.filter("[name=" + relys[i] + "]");
                        if ($rely.val() == "") {
                            $rely.trigger("focus");
                            alert("【" + obj[relys[i]].split(",")[0] + "】不能为空");
                            return;
                        }
                        rely_obj[relys[i]] = $rely.val();
                    }
                    //ajax请求数据
                    var data = {
                        rely: rely_obj,
                        target: name,
                        datas: "",
                        pageSize: 15,
                        pageIndex: 1
                    };
                    //判断该字段是否携带隐含的不可见字段
//                    console.log(obj[name]);
                    if (obj[name].indexOf(":") >= 0) {
                        arr = obj[name].split(':')[1].split(',').map(function (i) {
                            return i.slice(1);
                        });
                    }
                    //进行ajax请求
                    options.requesFun(data, function (data) {
                        options.selectpanel.reset(2, data, function (obj) {
                            $input.val(obj[name]);
                            $input.focus();
                            if ($(".parent").filter("[name='" + name + "']")) {
                                $(".parent").filter("[name='" + name + "']").val(obj[name]);
                            }
                            for (var i = 0; i < arr.length; i++) {
                                that.extraDatas[arr[i]] = obj[arr[i]];
                                if ($(".parent").filter("[name='" + arr[i] + "']")) {
                                    $(".parent").filter("[name='" + arr[i] + "']").val(obj[arr[i]]);
                                }
                                //设置携带隐含字段的值
                                if (that.$inputs.filter("[name='" + arr[i] + "']")) {
                                    that.$inputs.filter("[name='" + arr[i] + "']").val(obj[arr[i]]);
                                }
                            }
                        });
                    });
                } else if (obj[name].indexOf("@mulOptionTable") >= 0) {
                    arr = obj[name].split(':')[0].split(',');
                    relys = arr.slice(2).map(function (i) {
                        return i.slice(1);
                    });
                    var $rely, rely_obj = {};
                    for (var i = 0; i < relys.length; i++) { //判断所依赖的字段是否填写完整
                        $rely = that.$inputs.filter("[name=" + relys[i] + "]");
                        if ($rely.val() == "") {
                            $rely.trigger("focus");
                            alert("【" + obj[relys[i]].split(",")[0] + "】不能为空");
                            return;
                        }
                        rely_obj[relys[i]] = $rely.val();
                    }
                    //ajax请求数据
                    var data = {
                        rely: rely_obj,
                        target: name,
                        datas: "",
                        pageSize: 15,
                        pageIndex: 1
                    };
                    //判断该字段是否携带隐含的不可见字段
//                    console.log(obj[name]);
                    if (obj[name].indexOf(":") >= 0) {
                        arr = obj[name].split(':')[1].split(',').map(function (i) {
                            return i.slice(1);
                        });
                    }
                    //进行ajax请求
                    options.requesFun(data, function (data) {
                        data.titles.checkbox = "选择,5%";
                        options.selectpanel.reset(3, data, function (resarr) {
                            for (var i = 0; i < resarr.length; i++) {
                                var obj = resarr[i];
                                $input.val(obj[name]);
                                $input.focus();
                                if ($(".parent").filter("[name='" + name + "']")) {
                                    $(".parent").filter("[name='" + name + "']").val(obj[name]);
                                }
                                for (var j = 0; j < arr.length; j++) {
                                    that.extraDatas[arr[j]] = obj[arr[j]];
                                    if ($(".parent").filter("[name='" + arr[j] + "']")) {
                                        $(".parent").filter("[name='" + arr[j] + "']").val(obj[arr[j]]);
                                    }
                                    //设置携带隐含字段的值
                                    if (that.$inputs.filter("[name='" + arr[j] + "']")) {
                                        that.$inputs.filter("[name='" + arr[j] + "']").val(obj[arr[j]]);
                                    }
                                }
                                options.tableInputCallBack();
                            }
                        });
                    });
                } else {
                    arr = obj[name].split(',').slice(1, -1).map(function (it) {
                        return it.slice(1);
                    });
                    if (arr.length == 1) {
                        that.formDisable([name]);
                    }
                    options.selectpanel.reset(1, arr, function (str) {
                        $input.val(str);
                    });
                }
            });
            this.$inputs.filter(".event").bind('keypress', function (e) {
                if (e.keyCode === 13 && !$(this).val()) {
                    $(this).trigger('click');
                }
            });
            /*
             this.$inputs.filter(".check").on('blur', function() {
             var name = $(this).attr("name");
             var obj = options.controls[name];
             var value = $(this).val();
             var check = obj.split(",").slice(2).map(function(i){
             return i.slice(0);
             });
             var referenceObj = that.$inputs.filter("[name=" + check[1] + "]")
             if (check[0] == "小于") {
             if (eval(referenceObj.val()) < eval(value)) {
             alert("值不能大于" + referenceObj.val());
             //$(this).focus();
             return ;
             }
             }
             if (check[0] == "大于") {
             if (eval(referenceObj.val()) > eval(value)) {
             alert("值不能小于" + referenceObj.val());
             return ;
             }
             }  
             });
             */
        },
        specialInput: function () {
            this.$select.on("change", function () {
                $(this).prev().val($(this).val());
                //console.log($(this).prev().attr("name"));
                if ($(".parent").filter("[name='" + $(this).prev().attr("name") + "']")) {
                    $(".parent").filter("[name='" + $(this).prev().attr("name") + "']").val($(this).val());
                }
            });
            this.$inputs.filter(".special").attr("disabled", "disabled");
        }
    };
    $.extend(this, _pro_);
    this.getDOM();
    this.NormalInutBlur();
    this.EvtInputFocus();
    this.inputAtuoToggle();
    this.lastInputEnter();
    this.specialInput();
    return this;
};
