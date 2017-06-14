/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.controller;

import com.alibaba.fastjson.JSONObject;
import com.cn.bean.Customer;
import com.cn.bean.PartBaseInfo;
import com.cn.bean.report.KFQCFenLuData;
import com.cn.util.DatabaseOpt;
import com.cn.util.RedisAPI;
import java.util.List;

/**
 *
 * @author LFeng
 */
public class ReportDetailController {

    public String sfcTotalDataRequestDetail(String name, String start, String end, String datas) throws Exception {
        String json = "";
        JSONObject dataJson = JSONObject.parseObject(datas);
        CommonController commonController = new CommonController();
        DatabaseOpt opt = new DatabaseOpt();
        switch (name) {
            case "kfQC": {
                
                JSONObject proParams = new JSONObject();
                proParams.put("PartCode", "string," + dataJson.getString("partCode"));
                proParams.put("SupplierID", "string," + dataJson.getString("supplierID"));
                List<Object> list = commonController.proceduceQuery("spGetKFQCFenLuData", proParams, "com.cn.bean.report.KFQCFenLuData", opt.getConnect());
                for (Object obj : list) {
                    KFQCFenLuData data = (KFQCFenLuData) obj;
                    PartBaseInfo baseInfo = JSONObject.parseObject(RedisAPI.get("partBaseInfo_" + data.getPartCode()), PartBaseInfo.class);
                    data.setPartName(baseInfo.getPartName());
                    data.setPartID(baseInfo.getPartID());

                    Customer customer = JSONObject.parseObject(RedisAPI.get("customer_" + data.getSupplierID()), Customer.class);
                    data.setSupplierName(customer.getCustomerAbbName());
                }
                break;
            }
            case "xcQC": {
                break;
            }
        }
        return json;
    }
    
    
}
