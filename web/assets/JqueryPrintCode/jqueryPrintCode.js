(function ($) {
    var _default = {
        printArea: {},
        datasTitle: {},
        printFiled: [],
        datas: [],
        itemHtml: ""
    };

    var _funs_ = {
        getDOM: function () {
            this.html("");
            var inputResult = "";
            var printArea = this.printArea;
            for (var i in printArea) {
                inputResult += "<div class='div_code_row' name='" + i + "'>";
                var row = printArea[i];
                for (var j in row) {
                    var rowItems = row[j].split(",");
                    inputResult += "<div class='div_code_cell' style='width: " + rowItems[0] + ";' name='" + j + "'>" + (rowItems[1] ? rowItems[1] : "") + "</div>";
                    this.printFiled.push(j);
                }
                inputResult += "</div>";
            }
            this.itemHtml = "<div class='div_code_table'>" + inputResult + "</div>";

            //this.html(this.itemHtml);
            return this;
        },
        insertData: function (datas) {
            this.html("");
            for (var i = 0; i < datas.length; i++) {
                var item = datas[i];
                var htmlStr = _funs_.createHtml.call(this, i);
                this.append(htmlStr);
                _funs_.insertValue.call(this, i, item);
            }
            return this;
        },
        inputControlData: function (data) {
            for (var i in data) {
                this.find("div[name='" + i + "']").html("<div class='cell_start'></div>" + data[i]);
            }
            return this;
        },
        createHtml: function (itemId) {
            var inputResult = "";
            var printArea = this.printArea;
            for (var i in printArea) {
                inputResult += "<div class='div_code_row' name='" + i + "'>";
                var row = printArea[i];
                for (var j in row) {
                    var rowItems = row[j].split(",");
                    inputResult += "<div class='div_code_cell' style='width: " + rowItems[0] + ";' name='" + j + "'>" + (rowItems[1] ? rowItems[1] : "") + "</div>";
                }
                inputResult += "</div>";
            }
            return "<div class='div_code_table' id='" + itemId + "'><img alt='条码' id='img" + itemId + "' class='div_code_cell_img'/>" + inputResult + "</div>";
        },
        insertValue: function (itemId, itemData) {
            var str = "SY2014744208";
            var options = {
                format: "CODE128",
                displayValue: false,
                height: 60
            };
            $("#img" + itemId).JsBarcode(str, options);

            var printFiled = this.printFiled;
            for (var i in printFiled) {
                this.find("#" + itemId).find("div[name='" + printFiled[i] + "']").text(itemData[printFiled[i]]);
            }
        }
    };

    var _interface = {
        controlData: function (data) {
            _funs_.inputControlData.call(this, data);
        },
        render: function (datas) {
            this.datas = datas;
            _funs_.insertData.call(this, this.datas);
        },
        getHtmlWithData: function (itemData) {
            var inputResult = "";
            var printArea = this.printArea;
            for (var i in printArea) {
                inputResult += "<div class='div_code_row' name='" + i + "'>";
                var row = printArea[i];
                for (var j in row) {
                    var rowItems = row[j].split(",");
                    inputResult += "<div class='div_code_cell' style='width: " + rowItems[0] + ";' name='" + j + "'>" + (rowItems[1] ? rowItems[1] : itemData[j]) + "</div>";
                }
                inputResult += "</div>";
            }

            var str = "SY2014744208";
            var options = {
                format: "CODE128",
                displayValue: false,
                height: 50
            };
            $("#code_img_div img").JsBarcode(str, options);

            return "<div class='div_code_table'>" + $("#code_img_div").html() + inputResult + "</div>";
        },
        printCode: function (datas) {
            var LODOP = getLodop();
            LODOP.PRINT_INIT("条码打印");
            LODOP.SET_PRINT_PAGESIZE(1, "70mm", "50mm", "");

            for (var i = 0; i < datas.length; i++) {
                var itemData = datas[i];
                console.log(itemData);
                var htmlStr = this.getHtmlWithData(itemData);
                var strBodyStyle = "<style>" + document.getElementById("print_code_style").innerHTML + "</style>";
                htmlStr = strBodyStyle + "<body>" + htmlStr + "</body>";
                //LODOP.ADD_PRINT_BARCODE(0, 0, "70mm", "18mm", "128A", "SY2014744208");
                LODOP.ADD_PRINT_HTM(0, 0, "70mm", "50mm", htmlStr);
                LODOP.NEWPAGE();
            }
            //LODOP.PRINT();
            LODOP.PREVIEW();
        }
    };
    $.fn.createPrintCode = function (options) {
        $.extend(this, _default, options);
        _funs_.getDOM.call(this);
        _funs_.insertData.call(this, this.datas);
        $.extend(this, _interface);
        return this;
    };
})(window.jQuery);

