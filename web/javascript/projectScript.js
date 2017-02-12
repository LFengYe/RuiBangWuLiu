/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Ajax获取数据
 * @param {String} url 服务器接口URL
 * @param {String} sendBody - 请求参数列表，格式json字符串
 * @param {function} callback - 回调函数，请求成功执行的函数
 * @returns {undefined}
 */
function getDataInterface(url, sendBody, callback) {
    var request = getXMLHttpRequest();
//    var url = getRootPath() + "/DataInterfaceServlet";
    request.open("POST", url, true);
    request.onreadystatechange = function () {
        if (request.readyState === 4 && request.status === 200) {
            var result = request.responseText;
//            alert(result);
            var status = eval('(' + result + ')')['status'];
            if (status == -99) {
                alert('由于你长时间没有操作,导致Session失效!请你重新登录!');
                window.parent.location.href = "../login.html";
            } else {
                callback(result);
            }
        }
    };
    request.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    request.send(sendBody);
}