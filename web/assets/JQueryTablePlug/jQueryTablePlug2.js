(function ($) {
    var _funs_ = {
        getDOM: function () {
            var htmlStr = '<div class="wrapper"><div class="jtb-header">';
            htmlStr += '<span>查询条件:</span><input name="keywords" placeholder="查询条件" class="wc-control" /><button class="LocalFilter">查询</button>';
            //htmlStr += '<button class="select-all">全选</button>';
            htmlStr += '</div>';
            htmlStr += '<div class="jtb-container"><div class="jtb-scroll"></div></div>';
            htmlStr += '<div class="select-all"><button class="">全选</button></div>';
            htmlStr += '<div class="jtb-page"><div class="page"></div><div class="page-info"></div></div>';
            htmlStr += '</div>';
            this.html(htmlStr);

            //加载表头
            var result = '', txt = '', width = '', totalWidth = 0;
            //result += '<div class="col" name="checkbox"><h5>选择</h5><div class="inner"><ul></ul></div></div>';
            for (var i in this.titles) {
                txt = this.titles[i].split(',')[0];
                width = this.titles[i].split(',')[this.titles[i].split(',').length - 1];
                //totalWidth += parseInt(width);
                if (width === "0")
                    result += '<div class="col" name="' + i + '" style="display:none;"><h5>' + txt + '</h5><div class="inner"><ul></ul></div></div>';
                else
                    result += '<div class="col" name="' + i + '" style="width:' + width + ';"><h5>' + txt + '</h5><div class="inner"><ul></ul></div></div>';
                if (i === "checkbox") {
                    this.isAllSelect = true;
                }
            }
            this.$container = this.find(".jtb-scroll").append(result);
            this.$header = this.find(".jtb-header");
            this.$selectAll = this.find(".select-all");
            
            this.addClass("jtb");
            !this.isAllSelect && this.$selectAll.hide();
            !this.isLocalSearch && this.$header.hide();
            return this;
        },
        getTableDataDOM: function (datas) {
            //this.datas = datas;
            this.$container.find("ul").html("");
            var obj = null;
            for (var i = 0; i < datas.length; i++) {
                obj = datas[i];
                for (var k in this.titles) {
                    if (k === "checkbox") {
                        this.$container.children("div[name='" + k + "']").find("ul").append("<li><input type='checkbox' index='" + i + "'/></li>");
                        continue;
                    }
                    if (obj[k])
                        this.$container.children("div[name='" + k + "']").find("ul").append("<li>" + obj[k] + "</li>");
                    else
                        this.$container.children("div[name='" + k + "']").find("ul").append("<li>" + "  " + "</li>");
                }
            }
            return this;
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
            this.$container.find("ul").on("click", "li", function (e) {
                var index = $(this).index();
                //console.log(that.filterState);
                that.filterState && that.clickRowCallBack(index, that.afterFilter[index]);
                !that.filterState && that.clickRowCallBack(index, that.datas[index]);
                that.$container.find("ul li:nth-child(" + (index + 1) + ")").toggleClass("clicked");
            }).on("dblclick", "li", function (e) {
                var index = $(this).index();
                that.filterState && that.dbclickRowCallBack(index, that.afterFilter[index]);
                !that.filterState && that.dbclickRowCallBack(index, that.datas[index]);
                that.$container.find("ul li:nth-child(" + (index + 1) + ")").toggleClass("dbclicked");
            }).on("click", "input[type='checkbox']", function() {
                var index = $(this).attr("index");
                var selected = $(this).is(':checked');
                //console.log("index:" + index);
                that.checkBoxCallBack(index, selected, that.datas[index]);
                
                //that.filterState && that.checkBoxCallBack(index, selected, that.afterFilter[index]);
                //!that.filterState && that.checkBoxCallBack(index, selected, that.datas[index]);
            });
            
            /* 本地数据筛选
             this.find(".jtb-header button").click(function (e) {
             var keyWord = $(this).prev().val();
             _funs_.dataFilter.call(that, keyWord);
             }).prev().focus(function (e) {
             _funs_.getTableDataDOM.call(that, that.datas);
             that.filterState = false;
             });
             */
            this.find(".select-all button").click(function() {
                var checkBoxs = that.$container.find("input[type='checkbox']");
                var text = $(this).html();
                if (text === "全选") {
                    for (var i = 0; i < checkBoxs.length; i++) {
                        if (!$(checkBoxs[i]).is(':checked')) {
                            $(checkBoxs[i]).trigger('click');
                        }
                    }
                    $(this).html("取消全选");
                } else {
                    for (var i = 0; i < checkBoxs.length; i++) {
                        if ($(checkBoxs[i]).is(':checked')) {
                            $(checkBoxs[i]).trigger('click');
                        }
                    }
                    $(this).html("全选");
                }
            });
            this.find(".jtb-header button").click(function (e) {
                //var keyWord = $(this).prev().val();
                var keyWord = serializeJqueryElement(that.find(".LocalFilter").parent());
                if (that.searchCallBack) {
                    that.searchCallBack(keyWord);
                    return;
                }
            }).prev().focus(function (e) {
                $(this).val("");
                _funs_.getTableDataDOM.call(that, that.datas);
                that.filterState = false;
            });
            this.find(".jtb-header button").prev().keypress(function(event) {
                switch(event.keyCode) {
                    case 13:
                    {
                        $(this).next().trigger("click");
                        break;
                    }
                }
            });
        }
    };

    var _interface = {
        add: function (index, obj) {
            if (this.filterState) {
                var eq = this.afterFilter[index][0];
                this.datas.splice(0, 0, obj);
                this.filterState = false;
            } else {
                this.datas.splice(index, 0, obj);
            }
            _funs_.getTableDataDOM.call(this, this.datas);
        },
        del: function (index) {
            if (this.filterState) {
                var eq = this.afterFilter[index][0];
                this.datas.splice(eq, 1);
                this.filterState = false;
            } else {
                this.datas.splice(index, 1);
            }
            _funs_.getTableDataDOM.call(this, this.datas);
        },
        del2: function (arr) {   		//删除多个
            var index, set = this.datas;
            this.datas = [];
            for (var i = 0; i < arr.length; i++) {
                index = arr[i];
                set[index] = null;
            }
            for (var j = 0; j < set.length; j++) {
                set[j] && this.datas.push(set[j]);
            }
            this.render(this.datas);
        },
        update: function (index, obj) {

            if (this.filterState) {
                var eq = this.afterFilter[index][0];
                this.datas.splice(eq, 1, obj);
                this.filterState = false;
            } else {
                this.datas.splice(index, 1, obj);
            }
            _funs_.getTableDataDOM.call(this, this.datas);
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
        },
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
        emptyTable: function () {
            this.datas = [];
            _funs_.getTableDataDOM.call(this, this.datas);
        },
        getAllDatas: function () {
            return this.datas;
        },
        isUnique: function (obj) {
            for (var i = 0; i < this.datas.length; i++) {
                //判断表的unique字段是否存在重复
                for (var e = 0; e < this.unique.length; e++) {
                    var key = this.unique[e];
                    if (this.datas[i][key] == obj[key]) {
                        alert("【" + this.titles[key].split(",")[0] + "】不能重复!");
                        return false;
                    }
                }
                //判断表的主码是否存在重复记录
                var primaryIsRepeat = true;
                for (var index = 0; index < this.primary.length; index++) {
                    var pri = this.primary[index];
                    //console.log("data:" + this.datas[i][pri] + ",obj:" + obj[pri]);
                    if (this.datas[i][pri] != obj[pri]) {
                        primaryIsRepeat = false;
                        break;
                    }
                }
                if (primaryIsRepeat) {
                    alert("该记录已存在!");
                    return false;
                }
            }
            return true;
        },
        headerFocus: function() {
            this.$header.find("input").trigger("focus");
        },
        getSelectedItem: function() {
            var arr = [];
            var checkBoxs = this.$container.find("input[type='checkbox']");
            for (var i = 0; i < checkBoxs.length; i++) {
                if ($(checkBoxs[i]).is(':checked')) {
                    var obj = this.datas[$(checkBoxs[i]).attr("index")];
                    var whereObj = {};
                    for (var proIndex in this.primary) {
                        var proName = this.primary[proIndex];
                        whereObj[proName] = obj[proName];
                    }
                    arr.push(whereObj);
                }
            }
            //console.log(arr);
            return arr;
        },
        setData: function(data) {
            for (var index in data) {
                for (var i = 0; i < this.datas.length; i++) {
                    this.datas[i][index] = data[index];
                }
            }
            this.render(this.datas);
        },
        clearSelected: function() {
            this.$container.find("ul li").removeClass("clicked");
        },
        clearDoubleClick: function() {
            this.$container.find("ul li .dbclicked").removeClass("dbclicked");
        },
        selectAllClear: function() {
            this.find(".select-all button").trigger("click");
        }
    };

    $.fn.insertTable = function (options) {
        var _default = {
            titles: [], //表头  
            datas: [], //数据源
            unique: [], //表不重复字段
            primary: [], //表主码
            clickRowCallBack: function (index, obj) {
                //console.log(index);
            },
            dbclickRowCallBack: function (index, obj) {
                //console.log(obj);
            },
            pageCallBack: function (pageIndex, keyword) {
            },
            searchCallBack: function (keyword) {
            },
            checkBoxCallBack: function(index, selected, obj) {
                
            },
            isLocalSearch: true,
            isAllSelect: false,
            pageIndex: 1,
            pageSize: 15,
            isDialog: false,
            dataCount: 0
        };
        $.extend(this, _default, options);
        _funs_.getDOM.call(this);
        _funs_.getTableDataDOM.call(this, this.datas);
        _funs_.getPageInfo.call(this);
        _funs_.bindEvt.call(this);
        $.extend(this, _interface);
        
        return this;
    };
})(window.jQuery);
