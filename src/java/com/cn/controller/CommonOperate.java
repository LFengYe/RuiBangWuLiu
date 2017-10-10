/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cn.bean.in.SJBackWareHouse;
import com.cn.util.DatabaseOpt;
import com.cn.util.Units;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
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
     *
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
     *
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

    public String getDetailOperate(String packageName, String className, String datas, String rely,
            String primaryField, String auditField, String detailAuditField, boolean isAllDetail,
            String dataType, int pageSize, int pageIndex, DetailOperateInterface callback) throws Exception {

        String json;
        CommonController commonController = new CommonController();
        DatabaseOpt opt = new DatabaseOpt();
        //主表类
        Class mainClass = Class.forName(packageName + className);
        // 将属性的首字符大写，方便构造get，set方法  
        auditField = auditField.substring(0, 1).toUpperCase() + auditField.substring(1);
        Method auditMethod = mainClass.getMethod("get" + auditField);
        //明细表类
        Class detailClass = Class.forName(packageName + className + "List");
        Method method = detailClass.getMethod("getRecordCount", new Class[0]);
        //构建主表where条件
        String mainTabWhereSql = primaryField + " = '" + JSONObject.parseObject(rely).getString(primaryField) + "'";
        List<Object> list = commonController.dataBaseQuery("table", packageName, className, "*", mainTabWhereSql, pageSize, pageIndex, primaryField, 0, (dataType.compareToIgnoreCase("isHis") == 0) ? (opt.getConnectHis()) : (opt.getConnect()));
        if (list != null && list.size() > 0) {
            Object obj = list.get(0);

            HashMap<String, String> limitMap = callback.getLimitMap();
            //构建明细表where条件
            String whereSql = getDetailWhereCase(detailClass, datas, rely, detailAuditField, isAllDetail);
            //明细表查询
            List<Object> detailList = commonController.dataBaseQuery("view", packageName, className + "List", "*", whereSql, pageSize, pageIndex, primaryField, 0, (dataType.compareToIgnoreCase("isHis") == 0) ? (opt.getConnectHis()) : (opt.getConnect()));
            if (detailList != null && detailList.size() > 0) {
                for (Object detailObj : detailList) {
                    callback.setLimit(limitMap, detailObj);
                }
                json = getDetailData(detailList, obj, auditMethod, method);
            } else {
                json = Units.objectToJson(-1, "数据为空!", null);
            }
        } else {
            json = Units.objectToJson(-1, "输入参数错误!", null);
        }
        return json;
    }

    public String getDetailData(List<Object> detailList, Object mainObj, Method mainAuditMethod, Method recordCountMethod) throws Exception {
        String result = "{}";
        StringBuffer buffer = new StringBuffer(result);
        buffer.insert(buffer.lastIndexOf("}"), "\"datas\":" + JSONObject.toJSONString(detailList, Units.features));
        //判断主表审核字段是否为空, 设置明细只读属性
        if (Units.strIsEmpty((String) mainAuditMethod.invoke(mainObj))) {
            buffer.insert(buffer.lastIndexOf("}"), ",\"readOnly\":false");
        } else {
            buffer.insert(buffer.lastIndexOf("}"), ",\"readOnly\":true");
        }
        //明细表数据条数
        buffer.insert(buffer.lastIndexOf("}"), ",\"counts\":" + recordCountMethod.invoke(null, new Object[]{}));
        //buffer.insert(buffer.lastIndexOf("}"), ",\"rely\":" + rely);
        result = buffer.toString();
        return Units.objectToJson(0, "", result);

    }

    public String getDetailWhereCase(Class detailClass, String datas, String rely, String detailAuditField, boolean isAllDetail) {
        CommonController commonController = new CommonController();
        String whereSql = commonController.getWhereSQLStr(detailClass, datas, rely, true);
        String detailWhereCase = "";
        if (!Units.strIsEmpty(detailAuditField)) {
            detailWhereCase = detailAuditField + " is null";
        }
        if (!Units.strIsEmpty(whereSql)) {
            detailWhereCase = whereSql + " and " + detailWhereCase;
        }
        whereSql = isAllDetail ? detailWhereCase : whereSql;
        return whereSql;
    }

    public String auditItemOperate(String packageName, String className, String datas,
            String primaryField, String auditField, String detailAuditField, String detailAuditValue) throws Exception {
        String json;
        CommonController commonController = new CommonController();
        DatabaseOpt opt = new DatabaseOpt();
        //主表类
        Class mainClass = Class.forName(packageName + className);
        // 将属性的首字符大写，方便构造get，set方法  
        auditField = auditField.substring(0, 1).toUpperCase() + auditField.substring(1);
        Method auditMethod = mainClass.getMethod("get" + auditField);
        
        JSONArray arrayParam = JSONArray.parseArray(datas);
        String primaryValue = arrayParam.getJSONObject(0).getString(primaryField);
        String mainTabWhereSql = primaryField + " = '" + primaryValue + "'";
        List<Object> list = commonController.dataBaseQuery("table", packageName, className, "*", mainTabWhereSql, 11, 1, primaryField, 0, opt.getConnect());
        if (list != null && list.size() > 0) {
            Object obj = list.get(0);
            
            JSONArray updateArray = new JSONArray();
            if (!Units.strIsEmpty((String) auditMethod.invoke(obj))) {
                for (int i = 0; i < arrayParam.size(); i++) {
                    JSONObject params = new JSONObject();
                    params.put(detailAuditField, detailAuditValue);
                    updateArray.add(params);
                    updateArray.add(arrayParam.getJSONObject(i));
                }
                int result = commonController.dataBaseOperate(updateArray.toJSONString(), packageName, className + "List", "update", opt.getConnect()).get(0);
                if (result == 0) {
                    JSONObject params = new JSONObject();
                    params.put(detailAuditField, detailAuditValue);
                    json = Units.objectToJson(0, "审核成功!", params.toJSONString());
                } else {
                    json = Units.objectToJson(-1, "审核失败!", null);
                }
            } else {
                json = Units.objectToJson(-1, "单据未审核, 先审核单据才能审核明细!", null);
            }
        } else {
            json = Units.objectToJson(-1, "输入参数错误!", null);
        }
        return json;
    }

    public String importOperate(String packageName, String className, String item, String primaryField,
            ArrayList<Object> importData, ImportOperateInterface callback) throws Exception {
        String json;
        CommonController commonController = new CommonController();
        DatabaseOpt opt = new DatabaseOpt();

        if (importData != null && importData.size() > 0) {
            boolean importSuccess = true;
            Iterator iterator = importData.iterator();
            while (iterator.hasNext()) {
                if (!callback.checkData(iterator.next())) {
                    importSuccess = false;
                }
            }

            if (importSuccess) {
                int result = commonController.dataBaseOperate("[" + item + "]", packageName, className, "add", opt.getConnect()).get(0);
                if (result == 0) {
                    //System.out.println("import:" + JSONObject.toJSONString(importData));
                    result = commonController.dataBaseOperate(JSONObject.toJSONString(importData, Units.features), packageName, className + "List", "add", opt.getConnect()).get(0);
                    if (result == 0) {
                        JSONObject object = new JSONObject();
                        object.put("datas", importData);
                        json = Units.objectToJson(0, "数据添加成功!", JSONObject.toJSONString(object, Units.features));
                    } else if (result == 2627) {
                        json = Units.objectToJson(-1, "明细中存在重复数据!", null);
                    } else {
                        if (!Units.strIsEmpty(Units.getSubJsonStr(item, primaryField))) {
                            commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, primaryField) + "]", packageName, className, "delete", opt.getConnect());
                        }
                        json = Units.objectToJson(-1, "明细添加失败!", null);
                    }
                } else if (result == 2627) {
                    json = Units.objectToJson(-1, "数据以保存, 请勿重复提交!", null);
                } else {
                    json = Units.objectToJson(-1, "数据添加失败!", null);
                }
            } else {
                JSONObject object = new JSONObject();
                object.put("datas", importData);
                json = Units.objectToJson(-1, "数据添加失败!", object.toJSONString());
            }
        } else {
            json = Units.objectToJson(-1, "上传数据为空或格式不正确!", null);
        }
        return json;
    }

    public String submitOperate(String packageName, String className, String item, String primaryField, String details) throws Exception {
        String json;
        CommonController commonController = new CommonController();
        DatabaseOpt opt = new DatabaseOpt();

        int result = commonController.dataBaseOperate("[" + item + "]", packageName, className, "add", opt.getConnect()).get(0);
        if (result == 0) {
            result = commonController.dataBaseOperate(details, packageName, className + "List", "add", opt.getConnect()).get(0);
            if (result == 0) {
                json = Units.objectToJson(0, "数据添加成功!", null);
            } else if (result == 2627) {
                if (!Units.strIsEmpty(Units.getSubJsonStr(item, primaryField))) {
                    commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, primaryField) + "]", packageName, className, "delete", opt.getConnect()).get(0);
                }
                json = Units.objectToJson(-1, "明细中存在重复数据!", null);
            } else {
                if (!Units.strIsEmpty(Units.getSubJsonStr(item, primaryField))) {
                    commonController.dataBaseOperate("[" + Units.getSubJsonStr(item, primaryField) + "]", packageName, className, "delete", opt.getConnect()).get(0);
                }
                json = Units.objectToJson(-1, "明细添加失败!", null);
            }
        } else if (result == 2627) {
            json = Units.objectToJson(-1, "数据以保存, 请勿重复提交!", null);
        } else {
            json = Units.objectToJson(-1, "数据添加失败!", null);
        }
        return json;
    }

    public String submitOperate(String beanPackage, String tableName, String update, String add, String delete, String connType) throws Exception {
        CommonController commonController = new CommonController();
        DatabaseOpt opt = new DatabaseOpt();
        Connection conn = (connType.compareTo("base") == 0) ? opt.getConnectBase() : opt.getConnect();
        if (!Units.strIsEmpty(update) && !(update.compareTo("[]") == 0)) {
            ArrayList<Integer> updateResult = commonController.dataBaseOperate(update, beanPackage, tableName, "update", conn);
            if (updateResult.get(0) != 0) {
                return Units.objectToJson(-1, "修改操作失败!", null);
            }
        }
        conn = (connType.compareTo("base") == 0) ? opt.getConnectBase() : opt.getConnect();
        if (!Units.strIsEmpty(add) && !(add.compareTo("[]") == 0)) {
            ArrayList<Integer> addResult = commonController.dataBaseOperate(add, beanPackage, tableName, "add", conn);
            if (addResult.get(0) != 0) {
                return Units.objectToJson(-1, "添加操作失败!", null);
            }
        }
        conn = (connType.compareTo("base") == 0) ? opt.getConnectBase() : opt.getConnect();
        if (!Units.strIsEmpty(delete) && !(delete.compareTo("[]") == 0)) {
            ArrayList<Integer> delResult = commonController.dataBaseOperate(delete, beanPackage, tableName, "delete", conn);
            if (delResult.get(0) != 0) {
                return Units.objectToJson(-1, "删除操作失败!", null);
            }
        }

        return Units.objectToJson(0, "操作成功!", null);
    }
}
