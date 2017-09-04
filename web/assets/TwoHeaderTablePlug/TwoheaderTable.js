(function ($) {

    var _default = {
        title0: {},
        title1: {},
        datas: [],
        pageIndex: 1,
        pageSize: 15,
        dataCount: 0,
        clickRowCallBack: function (index, obj) {
            console.log(index);
        },
        dbclickRowCallBack: function (module, obj) {
        },
        sortMethod: 0
    };

    var _style = {
        lineHeight: 33,
        widths: {},
        colGroup: ""
    };

    var _funs_ = {
        getDOM: function () {
            var htmlStr = '<div class="wrapper"><div class="tht-header"></div><table class="chromaTable"></table></div>';
            htmlStr += '<div class="jtb-page"><div class="page"></div><div class="page-info"></div></div>';
            this.html(htmlStr);

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
                    title0_str += "<th name='" + i + "' colspan='" + colspans + "' detail='" + detail + "'>" + txt + "</th>";
//                    title0_str += "<td name='" + i + "' style='width:" + width + "'  colspan='" + colspans + "' detail='" + detail + "'>" + txt + "</td>";
                    _style.colGroup += "<col width='" + width + "'></col>";
                } else {
                    this.arr.push(i);
                    if (rowspans > 1)
                        title0_str += "<th name='" + i + "' class='cols'  rowspan='" + rowspans + "' detail='" + detail + "'>" + txt + "</th>";
//                    title0_str += "<td name='" + i + "' style='width:" + width + "' class='cols'  rowspan='" + rowspans + "' detail='" + detail + "'>" + txt + "</td>";
                    else
                        title0_str += "<th name='" + i + "' detail='" + detail + "'>" + txt + "</th>";
//                    title0_str += "<td name='" + i + "' style='width:" + width + "' detail='" + detail + "'>" + txt + "</td>";
                    _style.colGroup += "<col width='" + width + "'></col>";
                }
                _style.widths[i] = width;
            }
            title0_str = "<thead><tr>" + title0_str + "</tr></thead>";

            var title1_str = '', title1 = this.title1;
            for (var i in title1) {
                title1_str += "<td>" + title1[i] + "</td>";
            }
            title1_str = "<tr>" + title1_str + "</tr>";

            this.$container = this.find(".chromaTable").append(title0_str);
            this.addClass("tht");
            return this;
        },
        getTableDataDOM: function (datas) {
            var that = this;
            var data_str = '', result = '';
            for (var i = 0; i < datas.length; i++) {
                for (var k = 0; k < this.arr.length; k++) {
                    data_str += "<td index ='" + i + "' name='" + this.arr[k] + "'>" + datas[i][this.arr[k]] + "</td>";
                }
                result += "<tr>" + data_str + "</tr>";
                data_str = '';
            }
            result = "<tbody>" + result + "</tbody>";
            this.$container.find("tr:gt(0)").remove();
            this.$container.append(result);
            this.$container.chromatable({
                width: this.$container.width() + "px",
                height: "540px",
                scrolling: "yes",
                headerClickCallback: function (name) {
                    if (that.sortMethod) {
                        that.sortMethod = 0;
                    } else {
                        that.sortMethod = 1;
                    }

                    datas.sort(function (a, b) {
                        if (that.sortMethod) {
                            //return a[name] - b[name];
                            return compareFunc(a[name], b[name]);
                        } else {
                            //return b[name] - a[name];
                            return compareFunc(b[name], a[name]);
                        }
                    });
                    _funs_.getTableDataDOM.call(that, datas);
                }
            });
            _funs_.bindEvt.call(this);
        },
        getPageInfo: function () {
            var that = this;
            this.pageCount = parseInt((this.dataCount % this.pageSize === 0) ? (this.dataCount / this.pageSize) : (this.dataCount / this.pageSize + 1));
            laypage({
                cont: this.find(".jtb-page .page"),
                pages: this.pageCount,
                curr: this.pageIndex,
                skip: true,
                jump: function (obj, first) {
                    if (!first) {
                        that.pageIndex = obj.curr;
                        var keyWord;
                        if (that.isLocalSearch)
                            keyWord = serializeJqueryElement(that.find(".LocalFilter").parent());
                        else
                            keyWord = serializeJqueryElement($(".page2-container .wc-page2-form"));
                        //console.log(keyWord);
                        if (that.pageCallBack) {
                            that.pageCallBack(obj.curr, keyWord);
                        }
                    }
                }
            });
            this.find(".jtb-page .page-info").html("当前第" + this.pageIndex + "页,当前页" + this.datas.length + "条数据,   共" + this.pageCount + "页");
            if (this.pageCount > 1) {
                this.find(".jtb-page").css("display", "block");
            } else {
                this.find(".jtb-page").css("display", "none");
            }
            return this;
        },
        bindEvt: function () {
            var that = this;
            this.$container.find("tbody").find("tr").children("td").click(function (e) {
                var index = $(this).attr("index");
                that.$container.find("tbody tr:nth-child(" + (parseInt(index) + 1) + ")").toggleClass("clicked");
            });

            this.$container.find("tbody").find("tr").children("td").dblclick(function (e) {
                var name = $(this).attr("name");
                var titleTd = that.$container.find("thead").find("tr:eq(0)").children("th[name='" + name + "']");
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

            this.$container.find("thead").find("tr").children("th").click(function (e) {
                var name = $(this).attr("name");
                that.datas.sort(function (a, b) {
                    console.log(a);
                    console.log(b);
                    return a[name] - b[name];
                });
                _funs_.getTableDataDOM.call(that, that.datas);
            });
        },
        getRowData: function (index) {
        },
        dataFilter: function (data) {
            this.afterFilter = [];
            var str = '';                  //保证元组不重复
            var that = this;
            for (var i = 0; i < this.datas.length; i++) {
                var item = JSON.stringify(this.datas[i]);
                if (item.toLowerCase().indexOf(data.toLowerCase()) >= 0) {
                    this.afterFilter.push(this.datas[i]);
                }
            }
            /*this.$container.find("tbody").find('tr:contains("' + data + '")').each(function () {
             var eq = $(this).index();
             if (str.indexOf(eq) < 0) {
             str += eq;
             that.afterFilter.push([eq, that.datas[eq]]);
             }
             });
             return;
             this.$container.find("ul").html('');
             var arr = this.afterFilter.map(function (it) {
             return it[1];
             });*/
            console.log(this.afterFilter);
            _funs_.getTableDataDOM.call(this, this.afterFilter);
            this.filterState = true;        //表示数据筛选状态
            return this;
        }
    };

    var _interface = {
        render: function (data) {
            this.datas = data;
            _funs_.getTableDataDOM.call(this, this.datas);
        },
        page: function (dataCount, pageIndex, pageSize) {
            this.dataCount = dataCount;
            this.pageIndex = pageIndex;
            this.pageSize = pageSize;
            _funs_.getPageInfo.call(this);
        },
        filter: function (keyword) {
            console.log(keyword);
            if (keyword)
                _funs_.dataFilter.call(this, keyword);
            else
                _funs_.getTableDataDOM.call(this, this.datas);
        }
    };

    $.fn.insertTwoHeaderTable = function (options) {
        $.extend(this, _default, options);
        _funs_.getDOM.call(this);
        _funs_.getTableDataDOM.call(this, this.datas);
        $.extend(this, _interface);
        return this;
    };

})(window.jQuery);
