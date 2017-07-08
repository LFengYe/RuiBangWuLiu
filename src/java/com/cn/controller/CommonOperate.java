/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.controller;

import com.alibaba.fastjson.JSONArray;
import com.cn.bean.out.FJHOutWareHouse;
import com.cn.util.DatabaseOpt;
import com.cn.util.Units;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author LFeng
 */
public class CommonOperate {

    public String batchDeleteOperate(String delete, String packageName, String className, String orderField, String findField) throws Exception {
        String json;
        
        CommonController commonController = new CommonController();
        DatabaseOpt opt = new DatabaseOpt();
        Class classObj = Class.forName(packageName + className);
        Field field = classObj.getDeclaredField(findField);
        field.setAccessible(true);
        
        String whereCase = commonController.getWhereSQLStrWithArray(JSONArray.parseArray(delete));
        List<Object> list = commonController.dataBaseQuery("table", packageName, className, "*", whereCase, Integer.MAX_VALUE, 1, orderField, 1, opt.getConnect());
        if (list != null && list.size() > 0) {
            int count = 0;
            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                Object obj = classObj.cast(iterator.next());
                if (!Units.strIsEmpty(String.valueOf(field.get(obj)))) {
                    count++;
                }
            }
            if (count == 0) {
                ArrayList<Integer> delResult = commonController.dataBaseOperate(delete, packageName, className, "delete", opt.getConnect());
                if (delResult.get(0) == 0) {
                    json = Units.objectToJson(0, "删除操作成功!", null);
                } else if (delResult.get(0) == 547) {
                    count = 0;
                    for (int i = 1; i < delResult.size(); i++) {
                        if (delResult.get(i) != 1) {
                            count++;
                        }
                    }
                    json = Units.objectToJson(-1, "有" + count + "条数据不能删除!", null);
                } else {
                    json = Units.objectToJson(-1, "删除操作失败!", null);
                }
            } else {
                json = Units.objectToJson(-1, "有" + count + "条数据不能删除!", null);
            }
        } else {
            json = Units.objectToJson(-1, "输入参数错误!", null);
        }
        return json;
    }
    
    
}
