/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.test;

import com.alibaba.fastjson.JSONObject;
import com.cn.util.Units;

/**
 *
 * @author LFeng
 */
public class Test {
    public static void main(String[] args) {
        String loginUrl = "http://localhost:8880/RuiBangWuLiu/action.do";
        JSONObject loginParams = new JSONObject();
        loginParams.put("module", "userLogin");
        loginParams.put("operation", "employeeLogin");
        loginParams.put("username", "底盘库");
        loginParams.put("password", "1");
        loginParams.put("type", "app");
        Units.requestWithPost(loginUrl, loginParams.toJSONString());
        
        String httpUrl = "http://localhost:8880/RuiBangWuLiu/app.do";
        String sendBody = "";
        JSONObject params = new JSONObject();
        params.put("module", "备货管理");
        params.put("operation", "create");
        params.put("startTime", "2014-01-01");
        params.put("endTime", "2017-10-10");
        params.put("ZDCustomerID", "9998");
        params.put("isFinished", "1");
        Units.requestWithPost(httpUrl, sendBody);
    }
}
