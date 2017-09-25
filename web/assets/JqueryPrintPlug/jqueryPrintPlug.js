(function ($) {
    var _default = {
        printArea:{},
        datasTitle:{},
        datas:[],
        type: 2
    };
    
    var _funs_ = {
        getDOM: function() {
            this.html("");
            var inputResult = "";
            var printArea = this.printArea;
            for (var i in printArea) {
                inputResult += "<div class='div_row' name='" + i + "'>";
                var row = printArea[i];
                if (i === "datasTitle") {
                    this.datasTitle = row;
                }
                for (var j in row) {
                    var rowItems = row[j].split(",");
                    inputResult += "<div class='div_cell col' style='width: " + rowItems[0] + ";' name='" + j + "' flag='" + rowItems[2] + "'><div class='cell_start'></div>" + (rowItems[1] ? rowItems[1] : "") + "<div><ul></ul></div></div>";
                }
                inputResult += "</div>";
            }
            
            if (this.type == 1)
                this.html("<div class='div_table_2'>" + inputResult + "</div>");
            if (this.type == 2)
                this.html("<div class='div_table_2'>" + inputResult + "</div>");
            
            this.$detasTitle = this.find("div[name='datasTitle']");
            return this;
        },
        insertData: function(datas) {
            //console.log(datas);
            this.$detasTitle.find("ul").html("");
            var obj = null;
            for (var i = 0; i < datas.length; i++) {
                obj = datas[i];
                if (!obj)
                    continue;
                for (var j in this.datasTitle) {
                    if (obj[j]) {
                        this.$detasTitle.children("div[name='" + j + "']").find("ul").append("<li><div class='cell_start'></div>" + obj[j] + "</li>");
                    } else {
                        this.$detasTitle.children("div[name='" + j + "']").find("ul").append("<li><div class='cell_start'></div>" + " " + "</li>");
                    }
                }
            }
            return this;
        },
        addData: function(datas) {
            this.$detasTitle.find("ul").html("");
            var obj = null;
            for (var i = 0; i < datas.length; i++) {
                obj = datas[i];
                if (!obj)
                    continue;
                for (var j in this.datasTitle) {
                    if (obj[j]) {
                        this.$detasTitle.children("div[name='" + j + "']").find("ul").append("<li><div class='cell_start'></div>" + obj[j] + "</li>");
                    } else {
                        this.$detasTitle.children("div[name='" + j + "']").find("ul").append("<li><div class='cell_start'></div>" + " " + "</li>");
                    }
                }
            }
            return this;
        },
        inputControlData: function(data) {
            for (var i in data) {
                this.find("div[name='" + i + "']").html("<div class='cell_start'></div>" + data[i]);
            }
            return this;
        }
    };
    
    var _interface = {
        controlData: function(data) {
            _funs_.inputControlData.call(this, data);
        },
        render: function(datas) {
            this.datas = datas;
            _funs_.insertData.call(this, this.datas);
        }
    };
    $.fn.createPrintArea = function (options) {
        $.extend(this, _default, options);
        _funs_.getDOM.call(this);
        _funs_.insertData.call(this, this.datas);
        $.extend(this, _interface);
        return this;
    };
})(window.jQuery);

