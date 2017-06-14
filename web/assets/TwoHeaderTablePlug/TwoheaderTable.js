(function ($) {
    
    var _default = {
        title0: {},
        title1: {},
        datas: [],
        clickRowCallBack: function (index, obj) {
            console.log(index);
        },
        dbclickRowCallBack: function (module, obj) {
        }
    };
    
    var _style = {
        lineHeight: 33
    };

    var _funs_ = {
        getDOM: function () {
            this.html('<div class="wrapper"><div class="tht-header"></div><div class="tht-container"></div></div>');

            //加载表头
            var title0 = this.title0;
            var title0_str = '';
            var colspans, rowspans, txt, width, detail;
            this.arr = [];
            this.public_obj = {};
            for (var i in title0) {
                txt = title0[i].split(',')[0];
                colspans = parseInt(title0[i].split(',')[1]);
                rowspans = parseInt(title0[i].split(',')[2]);
                width = title0[i].split(',')[3];
                detail = title0[i].split(',')[5];
                //console.log(title0[i].split(',')[4]);
                if (title0[i].split(',')[4] === "false") {
                    this.public_obj[i] = true;
                }
                if (colspans > 1) {
                    for (var k = 0; k < colspans; k++) {
                        this.arr.push(i);
                    }
                    title0_str += "<td name='" + i + "' style='width:" + width + "'  colspan='" + colspans + "' detail='" + detail + "'>" + txt + "</td>";
                } else {
                    this.arr.push(i);
                    if (rowspans > 1)
                        title0_str += "<td name='" + i + "' style='width:" + width + "' class='cols'  rowspan='" + rowspans + "' detail='" + detail + "'>" + txt + "</td>";
                    else
                        title0_str += "<td name='" + i + "' style='width:" + width + "' detail='" + detail + "'>" + txt + "</td>";
                }
            }
            title0_str = "<tr>" + title0_str + "</tr>";
            
            var title1_str = '', title1 = this.title1;
            for (var i in title1) {
                title1_str += "<td>" + title1[i] + "</td>";
            }
            title1_str = "<tr>" + title1_str + "</tr>";
            
            this.$container = this.find(".tht-container").append(title0_str + title1_str);

            this.addClass("tht");

            return this;
        },
        getTableDataDOM: function (datas) {

            var data_str = '', result = '';
            for (var i = 0; i < datas.length; i++) {
               for (var k = 0; k < this.arr.length; k++) {
                   data_str += "<td index = " + i + " name=" + this.arr[k] + ">" + datas[i][this.arr[k]] + "</td>";
               }
                result += "<tr>" + data_str + "</tr>";
                data_str = '';
            }
            
            result += "";
            this.$container.find("tr:gt(1)").remove();
            this.$container.append(result);
            _funs_.bindEvt.call(this);
        },
        bindEvt: function () {
            var that = this;
            this.$container.find("tr:gt(1)").children("td").dblclick(function (e) {
                var name = $(this).attr("name");
                var titleTd = that.$container.find("tr:eq(0)").children("td[name='" + name + "']");
                var module = titleTd.attr("detail");
                
                if (that.public_obj[name]) {
                    return;
                }
                var index = $(this).attr("index");

                var obj = that.datas[index];

                var obj2 = {};
                for (var i in that.public_obj) {
                    obj2[i] = obj[i];
                }
                //obj2[name] = $(this).text();
                //console.log(obj2);
                that.dbclickRowCallBack(module, obj2);

            });
            this.$container.find("td").click(function() {
            });
        },
        getRowData: function (index) {
        },
        dataFilter: function (data) {
            this.afterFilter = [];
            var str = '';                  //保证元组不重复
            var that = this;
            this.$container.find('li:contains("' + data + '")').each(function () {
                var eq = $(this).index();
                if (str.indexOf(eq) < 0) {
                    str += eq;
                    that.afterFilter.push([eq, that.datas[eq]]);
                }

            });
            this.$container.find("ul").html('');
            var arr = this.afterFilter.map(function (it) {
                return it[1];
            });
            _funs_.getTableDataDOM.call(this, arr);
            this.filterState = true;        //表示数据筛选状态
            return this;
        }
    };

    var _interface = {
        render: function (data) {
            this.datas = data;
            _funs_.getTableDataDOM.call(this, this.datas);
        }
    };
    
    $.fn.insertTwoHeaderTable = function (options) {
        console.log(options);
        $.extend(this, _default, options);
        _funs_.getDOM.call(this);
        _funs_.getTableDataDOM.call(this, this.datas);
        $.extend(this, _interface);
        return this;
    };
})(window.jQuery);
