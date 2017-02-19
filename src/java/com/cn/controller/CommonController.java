/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.dom4j.Element;

/**
 *
 * @author LFeng
 */
public class CommonController {

    private static final Logger logger = Logger.getLogger(CommonController.class);
    private static final String beanPackage = "com.cn.bean.";
    ArrayList<String> roleCodeList = new ArrayList<>();

    /**
     * 数据库操作
     *
     * @param datas
     * @param tableName
     * @param operate
     * @param conn
     * @return 0 -- 操作成功 | -1 -- 操作失败 | 1 -- 传入参数错误 | 2 -- 传入数据为空
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public int dataBaseOperate(String datas, String tableName, String operate, Connection conn) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        
        JSONArray arrayData = JSONArray.parseArray(datas);
        if (arrayData == null || arrayData.isEmpty()) {
            return 2;
        }
        Class objClass = Class.forName(beanPackage + tableName);
        switch (operate) {
            //<editor-fold desc="数据添加操作">
            case "add": {
                StringBuilder builder;
                CallableStatement statement = null;
                try {
                    builder = new StringBuilder("insert into tbl" + tableName + " () values ()");
                    JSONObject firstObj = arrayData.getJSONObject(0);
                    Iterator<String> keysIterator = firstObj.keySet().iterator();
                    while (keysIterator.hasNext()) {
                        String key = keysIterator.next();

                        if (builder.indexOf(",") == -1) {
                            builder.insert(builder.indexOf("(") + 1, key + ",");
                            builder.insert(builder.lastIndexOf("(") + 1, "?,");
                        } else {
                            builder.insert(builder.indexOf(",)") + 1, key + ",");
                            builder.insert(builder.lastIndexOf(",)") + 1, "?,");
                        }
                    }
                    builder.deleteCharAt(builder.indexOf((",)")));
                    builder.deleteCharAt(builder.lastIndexOf(",)"));
                    String sql = builder.toString();
                    System.out.println("add sql:" + sql);

                    conn.setAutoCommit(false);
                    statement = conn.prepareCall(sql);

                    //批量设置参数
                    for (int i = 0; i < arrayData.size(); i++) {
                        JSONObject object = arrayData.getJSONObject(i);
                        keysIterator = object.keySet().iterator();
                        int itemCount = 1;
                        while (keysIterator.hasNext()) {
                            String key = keysIterator.next();
                            try {
                                setFieldValue(objClass, key, object.getString(key), statement, itemCount);
                            } catch (NoSuchFieldException ex) {
                                logger.error("未找到指定字段", ex);
                                statement.setString(itemCount, object.getString(key));
                            }
                            itemCount++;
                        }
                        statement.addBatch();
                    }

                    statement.executeBatch();
                    conn.commit();
                    return 0;
                } catch (SQLException ex) {
                    try {
                        if (conn != null) {conn.rollback();}
                    } catch (SQLException ex1) {
                        logger.error("数据库回滚错误", ex1);
                    }
                    logger.error("数据库执行出错", ex);
                } finally {
                    try {
                        if (statement != null) {statement.close();}
                        if (conn != null) {conn.close();}
                    } catch (SQLException ex) {
                        logger.error("数据库关闭连接错误", ex);
                    }
                }
                break;
            }
            //</editor-fold>

            //<editor-fold desc="数据更新操作">
            case "update": {
                if (arrayData.size() % 2 != 0) {
                    return 1;//数据格式不正确
                }
                StringBuilder builder;
                CallableStatement statement = null;
                try {
                    builder = new StringBuilder("update tbl" + tableName);
                    JSONObject firstSetObj = arrayData.getJSONObject(0);
                    JSONObject firstWhereObj = arrayData.getJSONObject(1);
                    
                    //拼接set字段sql
                    int itemCount = 0;
                    Iterator<String> keysIterator = firstSetObj.keySet().iterator();
                    while (keysIterator.hasNext()) {
                        String key = keysIterator.next();
                        if (itemCount > 0) {
                            builder.append(",").append(key).append(" = ").append("?");
                        } else {
                            builder.append(" set ").append(key).append(" = ").append("?");
                        }
                        itemCount++;
                    }
                    //拼接where条件sql
                    itemCount = 0;
                    keysIterator = firstWhereObj.keySet().iterator();
                    while (keysIterator.hasNext()) {
                        String key = keysIterator.next();
                        if (itemCount > 0) {
                            builder.append(" and ").append(key).append(" = ").append("?");
                        } else {
                            builder.append(" where ").append(key).append(" = ").append("?");
                        }
                        itemCount++;
                    }
                    
                    String sql = builder.toString();
                    System.out.println("update sql:" + sql);

                    conn.setAutoCommit(false);
                    statement = conn.prepareCall(sql);

                    //批量设置参数
                    for (int i = 0; i < arrayData.size(); i = i + 2) {
                        JSONObject setObject = arrayData.getJSONObject(i);
                        JSONObject whereObject = arrayData.getJSONObject(i + 1);
                        
                        //设置set的参数值
                        itemCount = 1;
                        keysIterator = setObject.keySet().iterator();
                        while (keysIterator.hasNext()) {
                            String key = keysIterator.next();
                            try {
                                setFieldValue(objClass, key, setObject.getString(key), statement, itemCount);
                            } catch (NoSuchFieldException ex) {
                                logger.error("未找到指定字段", ex);
                                statement.setString(itemCount, setObject.getString(key));
                            }
                            itemCount++;
                        }
                        
                        //设置where的参数值
                        //itemCount = 1;
                        keysIterator = whereObject.keySet().iterator();
                        while (keysIterator.hasNext()) {
                            String key = keysIterator.next();
                            try {
                                setFieldValue(objClass, key, setObject.getString(key), statement, itemCount);
                            } catch (NoSuchFieldException ex) {
                                logger.error("未找到指定字段", ex);
                                statement.setString(itemCount, setObject.getString(key));
                            }
                            itemCount++;
                        }
                        
                        statement.addBatch();
                    }

                    statement.executeBatch();
                    conn.commit();
                    return 0;
                } catch (SQLException ex) {
                    try {
                        if (conn != null) {conn.rollback();}
                    } catch (SQLException ex1) {
                        logger.error("数据库回滚错误", ex1);
                    }
                    logger.error("数据库执行出错", ex);
                } finally {
                    try {
                        if (statement != null) {statement.close();}
                        if (conn != null) {conn.close();}
                    } catch (SQLException ex) {
                        logger.error("数据库关闭连接错误", ex);
                    }
                }
                break;
            }
            //</editor-fold>

            //<editor-fold desc="数据删除操作">
            case "delete": {
                StringBuilder builder;
                CallableStatement statement = null;
                try {
                    builder = new StringBuilder("delete from tbl" + tableName);
                    JSONObject firstObj = arrayData.getJSONObject(0);
                    
                    //拼接set字段sql
                    int itemCount = 0;
                    Iterator<String> keysIterator = firstObj.keySet().iterator();
                    while (keysIterator.hasNext()) {
                        String key = keysIterator.next();
                        if (itemCount > 0) {
                            builder.append(" and ").append(key).append(" = ").append("?");
                        } else {
                            builder.append(" where ").append(key).append(" = ").append("?");
                        }
                        itemCount++;
                    }
                    
                    String sql = builder.toString();
                    System.out.println("delete sql:" + sql);

                    conn.setAutoCommit(false);
                    statement = conn.prepareCall(sql);

                    //批量设置参数
                    for (int i = 0; i < arrayData.size(); i++) {
                        JSONObject setObject = arrayData.getJSONObject(i);
                        
                        //设置set的参数值
                        itemCount = 1;
                        keysIterator = setObject.keySet().iterator();
                        while (keysIterator.hasNext()) {
                            String key = keysIterator.next();
                            try {
                                setFieldValue(objClass, key, setObject.getString(key), statement, itemCount);
                            } catch (NoSuchFieldException ex) {
                                logger.error("未找到指定字段", ex);
                                statement.setString(itemCount, setObject.getString(key));
                            }
                            itemCount++;
                        }
                        
                        statement.addBatch();
                    }

                    statement.executeBatch();
                    conn.commit();
                    return 0;
                } catch (SQLException ex) {
                    try {
                        if (conn != null) {conn.rollback();}
                    } catch (SQLException ex1) {
                        logger.error("数据库回滚错误", ex1);
                    }
                    logger.error("数据库执行出错", ex);
                } finally {
                    try {
                        if (statement != null) {statement.close();}
                        if (conn != null) {conn.close();}
                    } catch (SQLException ex) {
                        logger.error("数据库关闭连接错误", ex);
                    }
                }
                break;
            }
            //</editor-fold>

            //<editor-fold desc="数据查询操作">
            case "select": {
                break;
            }
            //</editor-fold>
        }
        return -1;
    }

    /**
     * 数据库查询操作
     * @param tableName
     * @param fields
     * @param wherecase
     * @param pageSize
     * @param pageIndex
     * @param orderField
     * @param orderFlag
     * @param conn
     * @return
     * @throws Exception 
     */
    public List<Object> dataBaseQuery(String type, String tableName, String fields, String wherecase, int pageSize, int pageIndex, String orderField, int orderFlag,
            Connection conn) throws Exception {
        CallableStatement statement = null;
        ArrayList<Object> result;
        Class objClass = Class.forName(beanPackage + tableName);
        try {
            statement = conn.prepareCall("{call tbGetRecordPageList(?, ?, ?, ?, ?, ?, ?, ?)}");
            if (type.compareTo("table") == 0)
                statement.setString("tableName", "tbl" + tableName);
            if (type.compareTo("view") == 0)
                statement.setString("tableName", "view" + tableName);
            statement.setString("fields", fields);
            statement.setString("wherecase", wherecase);
            statement.setInt("pageSize", pageSize);
            statement.setInt("pageIndex", pageIndex);
            statement.setString("orderField", orderField);
            statement.setInt("orderFlag", orderFlag);
            statement.registerOutParameter("recordCount", Types.INTEGER);
            ResultSet set = statement.executeQuery();
            Method[] methods = objClass.getMethods();
            result = new ArrayList<>();
            while (set.next()) {
                Object object = objClass.newInstance();
                for (Method method : methods) {
                    String methodName = method.getName();
                    
                    if (methodName.startsWith("set") && !Modifier.isStatic(method.getModifiers())) {
                        // 根据方法名字得到数据表格中字段的名字
                        String columnName = methodName.substring(3,methodName.length());
                        // 得到方法的参数类型
                        Class[] parmts = method.getParameterTypes();
                        if (parmts[0] == int.class) {
                            method.invoke(object, set.getInt(columnName));
                        } else if (parmts[0] == boolean.class) {
                            method.invoke(object, set.getBoolean(columnName));
                        } else if (parmts[0] == float.class) {
                            method.invoke(object, set.getFloat(columnName));
                        } else if (parmts[0] == double.class) {
                            method.invoke(object, set.getDouble(columnName));
                        } else {
                            method.invoke(object, set.getString(columnName));
                        }
                    }
                }
                
                result.add(object);
            }
            objClass.getMethod("setRecordCount", int.class).invoke(null, statement.getInt("recordCount"));
            
            return result;
        } catch (SQLException ex) {
            logger.error("数据库执行出错", ex);
        }
        return null;
    }
    
    /**
     * 根据JavaBean反射得到指定字段名的类型, 然后根据字段类型设置会话中对应参数的参数值
     *
     * @param objClass
     * @param fieldName
     * @param fieldValue
     * @param statement
     * @param fieldIndex
     * @throws NoSuchFieldException
     * @throws java.sql.SQLException
     */
    public void setFieldValue(Class objClass, String fieldName, String fieldValue, CallableStatement statement,
            int fieldIndex) throws NoSuchFieldException, SQLException {
        
        String fieldType = objClass.getDeclaredField(fieldName).getGenericType().toString();
        if (fieldType.contains("Integer")) {
            //fieldSQLStr = fieldValue;
            statement.setInt(fieldIndex, Integer.valueOf(fieldValue));
        } else if (fieldType.contains("Float")) {
            statement.setFloat(fieldIndex, Float.valueOf(fieldValue));
        } else if (fieldType.contains("Double")) {
            statement.setDouble(fieldIndex, Double.valueOf(fieldValue));
        } else if (fieldType.contains("Boolean")) {
            statement.setBoolean(fieldIndex, Boolean.valueOf(fieldValue));
        } else {
            //fieldSQLStr = "'" + fieldValue + "'";
            statement.setString(fieldIndex, fieldValue);
        }
    }
    
    /**
     * 
     * @param objClass
     * @param fieldName
     * @param fieldValue
     * @return 
     * @throws NoSuchFieldException 
     */
    public String getFieldSQLStr(Class objClass, String fieldName, String fieldValue) throws NoSuchFieldException {
        String fieldSQLStr;
        String fieldType = objClass.getField(fieldName).getGenericType().toString();
        if (fieldType.contains("Integer") || fieldType.contains("Float") || fieldType.contains("Double")) {
            fieldSQLStr = fieldValue;
        } else if (fieldType.contains("Boolean")) {
            if (Boolean.valueOf(fieldValue)) {
                fieldSQLStr = "1";
            } else {
                fieldSQLStr = "0";
            }
        } else {
            fieldSQLStr = "'" + fieldValue + "'";
        }
        return fieldSQLStr;
    }

    public String hasRight(Element element) {
        String menuJson = "";
        String roleCode = element.attributeValue("id");
        if (roleCodeList.contains(roleCode) || true) {
            if (element.elementIterator().hasNext()) {
                menuJson += "\"" + element.attributeValue("text") + "\":{";
                Iterator<Element> iterator = element.elementIterator();
                while (iterator.hasNext()) {
                    menuJson += hasRight(iterator.next());
                }
                menuJson = menuJson.substring(0, menuJson.length() - 1);
                menuJson += "},";
            } else {
                menuJson += "\"" + element.attributeValue("text") + "\":";
                menuJson += "\"" + element.attributeValue("hypelnk") + ",action.do\",";
            }
        }
        return menuJson;
    }
}
