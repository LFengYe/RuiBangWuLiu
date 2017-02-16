
$.fn.insertMenuPlug = function (options) {
    var _default_ = {
        datas: {},
        //href为跳转到三种模块格式哪一种            module为模块名称                url为该模块进行ajax请求的后台地址
        callback: function (href, module, url, route) {
        }

    };
    var iconArr = ["glyphicon glyphicon-plus-sign",
        "glyphicon glyphicon-header",
        "glyphicon glyphicon-paperclip",
        "glyphicon glyphicon-new-window",
        "glyphicon glyphicon-fire",
        "glyphicon glyphicon-calendar",
        "glyphicon glyphicon-eye-open",
        "glyphicon glyphicon-screenshot",
        "glyphicon glyphicon-info-sign",
        "glyphicon glyphicon-align-left",
        "glyphicon glyphicon-info-sign",
        "glyphicon glyphicon-cog"];
    var index = 0;

    getIcon = function () {
//        console.log(index);
        index++;
        return iconArr[index];
    };
    
    options = $.extend(_default_, options);
    var getDOM = function (obj, $ul) {
        for (var i in obj) {
            if (typeof obj[i] == 'object') {
                var $li = $("<li class='list-group-item jmp-list'><span class='" + getIcon() + "'></span><span class='jmp-title'>" + i + "</span><ul class='list-group'></ul></li>").appendTo($ul);
                getDOM(obj[i], $li.children('ul'));
            } else {
                $ul.append("<li class='list-group-item jmp-a'><span><a href='" + obj[i] + "'>" + i + "</a></span></li>");
            }
        }
    };
    this.bindEvt = function () {

        this.find('ul').hide().click(function () {
            $(this).children('ul li').slideDown(1000);
        });
        this.find("li.jmp-list").click(function (e) {

            $(this).children("ul").slideToggle('1000').parent().siblings().children('ul').slideUp(500);
            e.stopPropagation();
        });
        this.find('li.jmp-a').click(function (e) {
            $(".jmp .jmp-a").removeClass("selected");
            $(this).addClass("selected");
            var $a = $(this).find('a');
            
            var id = $a.attr("href").split(',')[0];
            var url = $a.attr("href").split(',')[1];
            
            //路径  及当前所在位置
            var $current = $a;
            var arr = [$a.html()];
            
            while ($current.parents("ul").length > 0) {
                console.log($current.parent("ul"));
                $current = $current.parents("ul").prev();
                console.log($current.text());
                arr.unshift($current.text());
            }
            options.callback(id, $a.text(), url, arr);
            return false;                    //阻止a的默认事件
        });
    };
    getDOM(options.datas, this);
    this.addClass("jmp");
    this.bindEvt();
};


