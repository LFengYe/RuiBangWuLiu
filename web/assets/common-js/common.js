
//将localStorage的menuDatas数据提取出来   进行菜单渲染   并未菜单绑定回调事件  
//点击菜单的某一项    则跳转到相应的页面  并将localstorage的module url值设置为点击的菜单项的相关值

var t, s, sp;
var ajaxPage1, ajaxPage2, ajaxPage3;    //ajaxPage*[1|2|3]根据module url向后台发送ajax请求  进行对应页面的渲染
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
                $("#" + type).fadeIn(500).siblings(".page").hide();         //页面切换
                localStorage.setItem("module", module);             //重置module  URL的值
                localStorage.setItem("url", url);
                // console.log(module+"XXX"+url);
                switch (type) {                                      //渲染对应的页面
                    case "page1":
                        ajaxPage1(module);
                        break;
                    case "page2":
                        ajaxPage2(module);
                        break;
                    case "page3":
//                        console.log("page3:" + module);
                        ajaxPage3(module);
                        break;
                }
            }
        });

        if (window.TableSelectPanel) {
            s = new stringSelectPanel([]);
            t = new TableSelectPanel({}, null);
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
    $.ajax({
        type: "post",
        url: url,
        data: JSON.stringify(obj),
        dataType: "json",
        success: function (data) {
            if (data) {
                if (data.status === 0) {
                    if (data.message) {
                        alert(data.message);
                    }
                    if (data.data) {
                        successCallBack(JSON.parse(data.data));
                    } else {
                        successCallBack();
                    }
                } else if (data.status === -99) {
                    alert('由于您长时间没有操作,登陆已失效!请你重新登录!');
                    window.parent.location.href = "login.html";
                } else {
                    alert(data.message);

                    if (data.data) {
                        console.log(data);
                        failCallBack(JSON.parse(data.data));
                    } else {
                        failCallBack();
                    }
                }
            } else {
                alert("服务器出错!");
                failCallBack();
            }
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
        var controlItem = control[i].split(',');
        txt = control[i].split(',')[0];
        len = control[i].split(',')[controlItem.length - 1];
        titles[i] = txt + ',' + len;
    }
    return titles;
}

function disableKeys(event) {
    var e = (document.all) ? window.event : event;
    var evCode = (document.all) ? e.keyCode : e.which;
    var srcElement = (document.all) ? e.srcElement : e.target;
    if (evCode == 13) {
        evCode = 9;
    }
    if (srcElement.type != "textarea" && srcElement.type != "text") {
        if (evCode == 8) {
            return false;
        }
    } else {
        if (srcElement.readOnly) {
            return false;
        }
    }
}