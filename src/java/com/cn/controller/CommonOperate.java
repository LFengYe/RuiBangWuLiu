/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.controller;

import com.alibaba.fastjson.JSONArray;
import com.cn.util.DatabaseOpt;
import com.cn.util.Units;
import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author LFeng
 */
public class CommonOperate {
    
    private static final Logger logger = Logger.getLogger(CommonOperate.class);

    /**
     * 批量删除操作
     * @param delete
     * @param packageName
     * @param className
     * @param orderField
     * @param findField
     * @return
     * @throws Exception 
     */
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
    
    /**
     * 数据结转
     * @param user
     * @return 
     */
    public String dataMoveToHistory(String user) {
        DatabaseOpt opt;
        Connection conn = null;
        CallableStatement statement = null;
        String json = null;
        try {
            opt = new DatabaseOpt();
            conn = opt.getConnect();
            statement = conn.prepareCall("{? = call spMoveRBDataToHistoryWare(?, ?)}");
            statement.registerOutParameter(1, Types.INTEGER);
            statement.setString(2, user);
            statement.registerOutParameter(3, Types.NVARCHAR);
            
            statement.execute();
            int result = statement.getInt(1);
            String message = statement.getString("RESULTMessage");
            if (result == 100) {
                json = Units.objectToJson(0, "结转成功", null);
            } else {
                json = Units.objectToJson(result, message, null);
            }
        } catch (SQLException ex) {
            logger.error("数据库执行出错", ex);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                logger.error("数据库关闭连接错误", ex);
            }
        }
        return json;
    }
}
