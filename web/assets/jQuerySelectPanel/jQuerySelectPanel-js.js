
//abstract SelectPanel   
function SelectPanel(data, callbackFun) {
    this.data = data;                     //private
    this.callback = callbackFun;         //private
    this.$container = null;               //private
    this.$elementDOM = null;               //private
    this.initDOM();
    this.render();
    this.bindClickEvt();
}

SelectPanel.prototype.reset = function (data, fun) {
    //public
    this.data = data;
    this.callback = fun;
    this.render();
    this.bindClickEvt();
};
SelectPanel.prototype.show = function () {
    //public
    this.$container.show(500);
};
SelectPanel.prototype.initDOM = function () {
    //private
    this.$container = $('<div class="SelectPanel"></div>');
    this.$container.appendTo("body");
};
SelectPanel.prototype.render = function () {
};
SelectPanel.prototype.bindClickEvt = function () {   //private

};
//interface  SelectPanel2[]

//extends SelectPanel
function stringSelectPanel(data, callBackfun) {
    SelectPanel.call(this, data, callBackfun);
}
extend(stringSelectPanel, SelectPanel);
stringSelectPanel.prototype.render = function () {
    var result = '';
    for (var i = 0; i < this.data.length; i++) {
        result += '<span class="SSP-element">' + this.data[i] + '</span>';
    }
    this.$container.html(result);
    this.$elementDOM = this.$container.children();
};
stringSelectPanel.prototype.bindClickEvt = function () {
    var that = this;
    this.$elementDOM.click(function (e) {
        var index = $(this).index();
        that.callback(that.data[index]);
        that.$container.hide(500);
    });
};



//extends SelectPanel
function TableSelectPanel(data, callBackfun) {
    var _default_ = {
        titles: {},
        datas: []
    };
    $.extend(data, _default_);
    SelectPanel.call(this, data, callBackfun);

}
extend(TableSelectPanel, SelectPanel);
TableSelectPanel.prototype.render = function () {
    var that = this;
    this.$container.insertTable({
        titles: this.data.titles, //表头  
        datas: this.data.datas, //数据源
        clickRowCallBack: function (index, obj) {
            that.callback(obj);
            that.$container.hide(500);
            //console.log(index)
        },
        dbclickRowCallBack: function (index, obj) {
            console.log(obj);
        },
        isLocalSearch: true
    });
};
TableSelectPanel.prototype.bindClickEvt = function () {

};

//extends TableSelectPanel
function TableMultiSelectPanel(data, callBackfun) {
    this.resSet = {};
    var _default_ = {
        titles: {},
        datas: []
    };
    $.extend(data, _default_);
    SelectPanel.call(this, data, callBackfun);
}
extend(TableMultiSelectPanel, TableSelectPanel);
TableMultiSelectPanel.prototype.render = function () {
    this.resSet = {};
    var that = this;
    this.$container.insertTable({
        titles: this.data.titles, //表头  
        datas: this.data.datas, //数据源
        clickRowCallBack: function (index, obj) {
            if (!that.resSet[index]) {
                that.resSet[index] = obj;
            } else {
                that.resSet[index] = null;
            }
        },
        dbclickRowCallBack: function (index, obj) {
            console.log(obj);
        },
        isLocalSearch: true
    });
    this.$container.append("<button class='btn btn-info my-sure'>确定</button>");
};
TableMultiSelectPanel.prototype.bindClickEvt = function () {
    var that = this;

    this.$container.find(".my-sure").on("click", function (e) {
        var resArr = [];
        console.log(that.resSet);
        for (var ele in that.resSet) {
            if (that.resSet[ele]) {
                resArr.push(that.resSet[ele]);
            }
        }
        that.callback(resArr);
        that.$container.hide(500);
    });
};


function extend(childClass, superClass) {                  //封装继承函数
    var f = function () {
    };
    f.prototype = superClass.prototype;
    childClass.prototype = new f();
    childClass.prototype.constructor = childClass;
}

//该代理类和TableSelectPanel stringSelectPanel类一样实现了同样的接口    即都拥有reset被外界使用的函数
//但是该代理类的作用是根据不同的参数决定使用哪种选择框
function SelectPanelProxy(tableselet, stringselet, multiselet) {
    this.tableselet = tableselet;
    this.stringselet = stringselet;
    this.multiselet = multiselet;
}
SelectPanelProxy.prototype.reset = function (type, data, callback) {

    switch (type) {
        case 1:
            console.log(1);
            this.stringselet.reset(data, callback);
            this.stringselet.show();
            break;
        case 2:
            console.log(2);
            this.tableselet.reset(data, callback);
            this.tableselet.show();
            break;
        case 3:
            this.multiselet.reset(data, callback);
            this.multiselet.show();
            break;
    }
};
            