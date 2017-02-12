
//将localStorage的menuDatas数据提取出来   进行菜单渲染   并未菜单绑定回调事件  
//点击菜单的某一项    则跳转到相应的页面  并将localstorage的module url值设置为点击的菜单项的相关值

var t, s, sp;
var ajaxPage1, ajaxPage2, ajaxPage3;             //ajaxPage*[1|2|3]根据module url向后台发送ajax请求  进行对应页面的渲染
$(function () {
    if (localStorage.getItem("isLogin") == "true") {
        var menuDatas = localStorage.getItem('menuDatas');
        var $route = $(".wc-route .breadcrumb");

        $.fn.insertMenuPlug && $(".wc-menu").insertMenuPlug({
            datas: JSON.parse(menuDatas),
            callback: function (type, module, url, route_arr) {
                $route.find("li").remove();
                for (var i = 0; i < route_arr.length; i++) {
                    $route.append("<li>" + route_arr[i] + "</li>");
                }
//						 self.location = type+".html";
                $("#" + type).fadeIn(500).siblings(".page").hide();         //页面切换
                localStorage.setItem("module", module);             //重置module  URL的值
                localStorage.setItem("url", url);
                // console.log(module+"XXX"+url);
                switch (type) {                                      //渲染对应的页面
                    case "page1":
                        ajaxPage1();
                        break;
                    case "page2":

                        ajaxPage2();
                        break;
                    case "page3":
                        ajaxPage3();
                        break;
                }
            }
        });
        if (window.TableSelectPanel) {
            t = new TableSelectPanel({}, null);
            s = new stringSelectPanel([]);

            m = new TableMultiSelectPanel({}, null);
            sp = new SelectPanelProxy(t, s, m);
        }

    }
});

//工具函数
//ajax函数将该项目所有ajax请求进行了封装   对ajax请求的格式和返回数据格式进行了处理   并执行回调函数  
function ajax(module, url, operation, data, successCallBack, failCallBack) {
//    console.log("ajax request");
    var obj = {
        module: module,
        operation: operation
    };
    $.extend(obj, data);
//    console.log("提交给服务器的数据：");
    /*
     $.each(obj, function (key, value) {
     if (typeof value == "object") {
     obj[key] = JSON.stringify(obj[key]);
     }
     });
     */
    //obj = JSON.stringfy(obj);
//    console.log(obj);
    $.ajax({
        type: "post",
        url: url,
        data: JSON.stringify(obj),
        dataType: "json",
        success: function (data) {
            if (data.status !== 0) {
                alert(data.msg);
                failCallBack();
            } else {
                successCallBack(JSON.parse(data.data));
            }
            /*
             if (data.indexOf("error") == 0) {
             failCallBack();
             } else if (data.indexOf("success") == 0) {
             successCallBack(data);
             } else {
             data = (new Function("", "return" + data))();
             successCallBack(data);
             }
             */
        }
    });
}

function ajaxData(operation, data, successCallBack, failCallBack) {
    ajax(localStorage.getItem("module"), localStorage.getItem("url"), operation, data, successCallBack, failCallBack);
}

function getLength(control) {
    var titles = {};
    var txt, len;
    for (var i in control) {
        txt = control[i].split(',')[0];
        len = control[i].split(',')[2];
        if (len > 200) {
            len = '140px';
        } else if (len > 100) {
            len = '120px';
        } else if (len > 80) {
            len = '100px';
        } else {
            len = '80px';
        }
        titles[i] = txt + ',' + len;
    }
    return titles;
}