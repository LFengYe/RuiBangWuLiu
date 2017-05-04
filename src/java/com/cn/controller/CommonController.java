/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cn.bean.FieldDescription;
import com.cn.util.Units;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.BatchUpdateException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Element;

/**
 *
 * @author LFeng
 */
public class CommonController {

    private static final Logger logger = Logger.getLogger(CommonController.class);

    /**
     * 数据库操作
     *
     * @param datas
     * @param beanPackage
     * @param tableName
     * @param operate
     * @param conn
     * @return 0 -- 操作成功 | -1 -- 操作失败 | 1 -- 传入参数错误 | 2 -- 传入数据为空
     */
    public ArrayList<Integer> dataBaseOperate(String datas, String beanPackage, String tableName, String operate, Connection conn) throws Exception {
        ArrayList<Integer> results = new ArrayList<>();
        int[] exeResult = null;
        JSONArray arrayData = JSONArray.parseArray(datas);
        if (arrayData == null || arrayData.isEmpty()) {
            results.add(0, 2);
            return results;
        }
        Class objClass = Class.forName(beanPackage + tableName);
        CallableStatement statement = null;
        try {
            switch (operate) {
                //<editor-fold desc="数据添加操作">
                case "add": {
                    StringBuilder builder;
                    builder = new StringBuilder("insert into tbl" + tableName + " () values ()");
                    JSONObject firstObj = arrayData.getJSONObject(0);
                    Iterator<String> keysIterator = firstObj.keySet().iterator();
                    while (keysIterator.hasNext()) {
                        String key = keysIterator.next();
                        if (!isInput(objClass, key)) {
                            continue;
                        }

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
                            if (!isInput(objClass, key)) {
                                continue;
                            }

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
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="数据更新操作">
                case "update": {
                    if (arrayData.size() % 2 != 0) {
                        results.add(0, 1);
                        return results;//数据格式不正确
                    }
                    StringBuilder builder;
                    builder = new StringBuilder("update tbl" + tableName);
                    JSONObject firstSetObj = arrayData.getJSONObject(0);
                    JSONObject firstWhereObj = arrayData.getJSONObject(1);

                    //拼接set字段sql
                    int itemCount = 0;
                    Iterator<String> keysIterator = firstSetObj.keySet().iterator();
                    while (keysIterator.hasNext()) {
                        String key = keysIterator.next();
                        if (!hasField(objClass, key)) {
                            continue;
                        }
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
                        if (!hasField(objClass, key)) {
                            continue;
                        }
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
                            if (!hasField(objClass, key)) {
                                continue;
                            }
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
                            if (!hasField(objClass, key)) {
                                continue;
                            }
                            try {
                                setFieldValue(objClass, key, whereObject.getString(key), statement, itemCount);
                            } catch (NoSuchFieldException ex) {
                                logger.error("未找到指定字段", ex);
                                statement.setString(itemCount, whereObject.getString(key));
                            }
                            itemCount++;
                        }

                        statement.addBatch();
                    }
                    break;
                }
                //</editor-fold>

                //<editor-fold desc="数据删除操作">
                case "delete": {
                    StringBuilder builder;
                    builder = new StringBuilder("delete from tbl" + tableName);
                    JSONObject firstObj = arrayData.getJSONObject(0);

                    //拼接set字段sql
                    int itemCount = 0;
                    Iterator<String> keysIterator = firstObj.keySet().iterator();
                    while (keysIterator.hasNext()) {
                        String key = keysIterator.next();
                        if (!hasField(objClass, key)) {
                            continue;
                        }
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
                            if (!hasField(objClass, key)) {
                                continue;
                            }
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
                    break;
                }
                //</editor-fold>
            }

            if (statement != null) {
                try {
                    exeResult = statement.executeBatch();
                    conn.commit();
                    results.add(0, 0);
                    if (exeResult != null) {
                        for (int i = 0; i < exeResult.length; i++) {
                            results.add(exeResult[i]);
                        }
                    }
                    return results;
                } catch (BatchUpdateException e) {
                    //外码不存在异常代码: 547
                    //Check条件不满足异常代码: 547
                    //重复键异常代码: 2627
                    logger.error("批处理执行异常:" + e.getErrorCode() + "," + e.getMessage());
                    exeResult = e.getUpdateCounts();
                    results.add(0, e.getErrorCode());
                    if (exeResult != null) {
                        for (int i = 0; i < exeResult.length; i++) {
                            results.add(exeResult[i]);
                        }
                    }
                    return results;
                }
            } else {
                results.add(0, 1);
                return results;//传入参数错误, 没有对应的操作方法
            }
        } catch (SQLException ex) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex1) {
                logger.error("数据库回滚错误", ex1);
            }
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

        //兼容按照-1判断失败的函数
        results.add(0, -1);
        if (exeResult != null) {
            for (int i = 0; i < exeResult.length; i++) {
                results.add(exeResult[i]);
            }
        }
        return results;
    }

    /**
     * 数据库查询操作
     *
     * @param type -- table表示查询的是数据库表, tableName加tbl前缀进行查询; view表示查询的是师徒,
     * tableName加view前缀进行查询
     * @param beanPackage
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
    public List<Object> dataBaseQuery(String type, String beanPackage, String tableName, String fields, String wherecase, int pageSize, int pageIndex, String orderField, int orderFlag,
            Connection conn) throws Exception {
//        System.out.println("wherecase:" + wherecase);
        CallableStatement statement = null;
        ArrayList<Object> result;
        Class objClass = Class.forName(beanPackage + tableName);
        try {
            statement = conn.prepareCall("{call tbGetRecordPageList(?, ?, ?, ?, ?, ?, ?, ?)}");
            if (type.compareTo("table") == 0) {
                statement.setString("tableName", "tbl" + tableName);
            }
            if (type.compareTo("view") == 0) {
                statement.setString("tableName", "view" + tableName);
            }
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
                        String columnName = methodName.substring(3, methodName.length());
                        if (Units.isExistColumn(set, columnName)) {
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
                }

                result.add(object);
            }
            objClass.getMethod("setRecordCount", int.class).invoke(null, statement.getInt("recordCount"));

            return result;
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
            int fieldIndex) throws Exception {

        //String fieldType = objClass.getDeclaredField(fieldName).getGenericType().toString();
        if (objClass.getDeclaredField(fieldName).getType() == int.class) {
            statement.setInt(fieldIndex, Integer.valueOf(Units.strIsEmpty(fieldValue) ? ("0") : (fieldValue)));
        } else if (objClass.getDeclaredField(fieldName).getType() == float.class) {
            statement.setFloat(fieldIndex, Float.valueOf(Units.strIsEmpty(fieldValue) ? ("0") : (fieldValue)));
        } else if (objClass.getDeclaredField(fieldName).getType() == double.class) {
            statement.setDouble(fieldIndex, Double.valueOf(Units.strIsEmpty(fieldValue) ? ("0") : (fieldValue)));
        } else if (objClass.getDeclaredField(fieldName).getType() == boolean.class) {
//            System.out.println("fieldName:" + fieldName + ",fieldValue:" + fieldValue);
            statement.setString(fieldIndex, Boolean.valueOf(fieldValue) ? "1" : "0");
        } else {
            statement.setString(fieldIndex, Units.strIsEmpty(fieldValue) ? null : (fieldValue));
        }
    }

    /**
     * 产生查询条件
     *
     * @param objClass
     * @param keyWord 通用查询条件字符串
     * @param rely 特定条件查询字符串(json对象)
     * @param isAll 是否使用通用查询条件查询
     * @return
     */
    public String getWhereSQLStr(Class objClass, String keyWord, String rely, boolean isAll) {
        String result = null;
        Field[] fields = objClass.getDeclaredFields();
        JSONObject object = JSONObject.parseObject(rely);
        Iterator<String> iterator = object.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (hasField(objClass, key)) {
                if (result == null) {
                    result = "(" + key + " = '" + object.getString(key) + "')";
                } else {
                    result += " and " + "(" + key + " = '" + object.getString(key) + "')";
                }
            }
        }

        if (!isAll) {
            return (result == null) ? "" : result;
        }

        String commonResult = null;
        Set<String> keySet = object.keySet();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            //如果特定字段中包含改字段, 则拼接相等条件
            if (keySet != null && keySet.contains(field.getName())) {
                continue;
            }

            String fieldType = field.getGenericType().toString();
            if (fieldType.contains("Integer") || fieldType.contains("Double") || fieldType.contains("Float")) {
                if (commonResult == null) {
                    commonResult = "(" + field.getName() + " = " + keyWord + ")";
                } else {
                    commonResult += " or " + "(" + field.getName() + " = " + keyWord + ")";
                }
            } else if (fieldType.contains("String")) {
                if (commonResult == null) {
                    commonResult = "(" + field.getName() + " like '%" + keyWord + "%')";
                } else {
                    commonResult += " or " + "(" + field.getName() + " like '%" + keyWord + "%')";
                }
            }
        }

        if (result != null) {
            result += " and (" + commonResult + ")";
        } else {
            result = commonResult;
        }

//        System.out.println("where SQL:" + result);
        return (result == null) ? "" : "(" + result + ")";
    }

    /**
     * 产生包含日期查询的查询条件
     *
     * @param objClass
     * @param keyWord 通用查询条件字符串
     * @param rely 特定日期条件查询字符串(json对象:{start:startTime, end: endTime})
     * @param isAll 是否使用通用查询条件查询
     * @return
     */
    public String getWhereSQLStrWithDate(Class objClass, String keyWord, String rely, boolean isAll) {
        String result = null;

        JSONObject object = JSONObject.parseObject(rely);
        String commonResult = null;
        Field[] fields = objClass.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            //日期特定搜索条件
            if (field.isAnnotationPresent(FieldDescription.class)) {
                FieldDescription description = field.getAnnotation(FieldDescription.class);
                if (description.type() != null && description.type().compareTo("date") == 0
                        && !Units.strIsEmpty(object.getString("start")) && !Units.strIsEmpty(object.getString("end"))) {
                    if (result == null) {
                        result = "(" + field.getName() + " between '" + object.getString("start") + "' and '" + object.getString("end") + "')";
                    } else {
                        result += " and " + "(" + field.getName() + " between '" + object.getString("start") + "' and '" + object.getString("end") + "')";
                    }
                    continue;
                }
            }

            String fieldType = field.getGenericType().toString();
            if (fieldType.contains("Integer") || fieldType.contains("Double") || fieldType.contains("Float")) {
                if (commonResult == null) {
                    commonResult = "(" + field.getName() + " = " + keyWord + ")";
                } else {
                    commonResult += " or " + "(" + field.getName() + " = " + keyWord + ")";
                }
            } else if (fieldType.contains("String")) {
                if (commonResult == null) {
                    commonResult = "(" + field.getName() + " like '%" + keyWord + "%')";
                } else {
                    commonResult += " or " + "(" + field.getName() + " like '%" + keyWord + "%')";
                }
            }
        }

        if (result != null) {
            result += " and (" + commonResult + ")";
        } else {
            result = commonResult;
        }

        return (result == null) ? "" : "(" + result + ")";
    }

    /**
     * 获取不同类型字段名的SQL字符串
     *
     * @param objClass
     * @param fieldName
     * @param fieldValue
     * @return
     * @throws NoSuchFieldException
     */
    public String getFieldSQLStr(Class objClass, String fieldName, String fieldValue) throws Exception {
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

    /**
     * 根据角色权限代码将指定节点先的内容转成json喘
     *
     * @param element
     * @param roleRightList
     * @return
     */
    public String hasRight(Element element, ArrayList<String> roleRightList) {
        String menuJson = "";
        String roleCode = element.attributeValue("id");
        if (roleRightList.contains(roleCode)) {
            if (element.elementIterator().hasNext()) {
                menuJson += "\"" + element.attributeValue("text") + "\":{";
                Iterator<Element> iterator = element.elementIterator();
                while (iterator.hasNext()) {
                    menuJson += hasRight(iterator.next(), roleRightList);
                }
                menuJson = menuJson.substring(0, menuJson.length() - 1);
                menuJson += "},";
            } else {
                menuJson += "\"" + element.attributeValue("text") + "\":";
                menuJson += "\"" + element.attributeValue("hypelnk") + ",action.do," + element.attributeValue("id") + "\",";
            }
        }
        return menuJson;
    }

    /**
     * 判断指定类名中是否包含指定的字段名
     *
     * @param objClass
     * @param fieldName
     * @return
     */
    public boolean hasField(Class objClass, String fieldName) {
        Field[] fields = objClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().compareToIgnoreCase(fieldName) == 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isInput(Class objClass, String fieldName) {
        Field[] fields = objClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().compareToIgnoreCase(fieldName) == 0) {
                if (field.isAnnotationPresent(FieldDescription.class)) {
                    FieldDescription description = field.getAnnotation(FieldDescription.class);
                    //注释的操作为input或import的字段
                    if (description.operate().compareTo("input") == 0
                            || description.operate().compareTo("import") == 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 导入数据
     *
     * @param beanPackage
     * @param className
     * @param fileName
     * @return
     * @throws Exception
     */
    public ArrayList<Object> importData(String beanPackage, String className, String fileName) throws Exception {
        //获取所有设置字段名称的字段
        Class objClass = Class.forName(beanPackage + className);
        Field[] fields = objClass.getDeclaredFields();
        ArrayList<Field> accessFields = new ArrayList<>();
        ArrayList<String> fieldDes = new ArrayList<>();

        for (Field field : fields) {
            if (field.isAnnotationPresent(FieldDescription.class)) {
                FieldDescription description = field.getAnnotation(FieldDescription.class);
                if (description.operate().compareTo("import") == 0) {
                    fieldDes.add(description.description());
                    accessFields.add(field);
                }
            }
        }

        //从文件读入数据, 生成Excel解析
        InputStream inputStream = null;
        File file = new File(fileName);
        inputStream = new FileInputStream(file);
        Sheet sheet;
        if (fileName.endsWith(".xls")) {
            HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
            sheet = workbook.getSheetAt(0);
        } else if (fileName.endsWith(".xlsx")) {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            sheet = workbook.getSheetAt(0);
        } else {
            logger.info("导入的文件不是Excel文件!");
            return null;
        }

        Row headerRow = sheet.getRow(0);
        //如果第一行标题行为空或上传数据列数和类的字段描述长度不一致, 返回数据格式不正确
//        System.out.println("cells num:" + headerRow.getPhysicalNumberOfCells() + ",des size:" + fieldDes.size());
        if (headerRow == null || headerRow.getPhysicalNumberOfCells() != fieldDes.size()) {
            //json = Units.objectToJson(-1, "上传数据格式不正确, 请先下载模板, 按照模板格式录入数据", null);
            return null;
        }

        //根据表头名称与类字段名称找到对应关系
        int[] templateDataIndex = new int[fieldDes.size()];
        for (int i = 0; i < fieldDes.size(); i++) {
            Cell cell = headerRow.getCell(i);
            cell.setCellType(Cell.CELL_TYPE_STRING);
            String fieldName = cell.getStringCellValue();
            if (fieldDes.indexOf(fieldName) != -1) {
                templateDataIndex[fieldDes.indexOf(fieldName)] = i;
            } else {
                return null;
            }
        }

        ArrayList<Object> result = new ArrayList<>();
        //解析表格数据, 存入List中
        for (int i = 1; i <= sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }

            Object object = objClass.newInstance();
            for (int j = 0; j < accessFields.size(); j++) {
                Field field = accessFields.get(j);
                field.setAccessible(true);
                Cell cell = row.getCell(templateDataIndex[j]);
//                String fieldType = field.getGenericType().toString();

                if (field.getType() == int.class) {
                    if (cell == null) {
                        field.set(object, 0);
                    } else {
                        row.getCell(templateDataIndex[j]).setCellType(Cell.CELL_TYPE_NUMERIC);
                        field.set(object, (int) row.getCell(templateDataIndex[j]).getNumericCellValue());
                    }
                } else if (field.getType() == float.class) {
                    if (cell == null) {
                        field.set(object, 0);
                    } else {
                        row.getCell(templateDataIndex[j]).setCellType(Cell.CELL_TYPE_NUMERIC);
                        field.set(object, (float) row.getCell(templateDataIndex[j]).getNumericCellValue());
                    }
                } else if (field.getType() == double.class) {
                    if (cell == null) {
                        field.set(object, 0);
                    } else {
                        row.getCell(templateDataIndex[j]).setCellType(Cell.CELL_TYPE_NUMERIC);
                        field.set(object, row.getCell(templateDataIndex[j]).getNumericCellValue());
                    }
                } else if (field.getType() == boolean.class) {
                    if (cell == null) {
                        field.set(object, false);
                    } else {
                        row.getCell(templateDataIndex[j]).setCellType(Cell.CELL_TYPE_BOOLEAN);
                        field.set(object, row.getCell(templateDataIndex[j]).getBooleanCellValue());
                    }
                } else {
                    if (cell == null) {
                        field.set(object, "");
                    } else {
                        row.getCell(templateDataIndex[j]).setCellType(Cell.CELL_TYPE_STRING);
                        field.set(object, row.getCell(templateDataIndex[j]).getStringCellValue());
                    }

                }
            }

            result.add(object);
        }

        return result;
    }
}
