/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.controller;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.dom4j.Element;

/**
 *
 * @author LFeng
 */
public class CommonController {

    private static final String beanPackage = "com.cn.bean.";
    ArrayList<String> roleCodeList = new ArrayList<>();

    public void dataBaseOperate(List<Object> datas, String objectName, String operate, Connection conn) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String sql;
        Class c = Class.forName(beanPackage + objectName);
        switch (operate) {
            case "add": {
                StringBuilder builder = new StringBuilder("insert into tbl" + objectName + " () values ()");
                for (Field field : c.getDeclaredFields()) {
                    if (builder.indexOf(",") == -1) {
                        builder.insert(builder.indexOf("(") + 1, field.getName() + ",");
                        builder.insert(builder.lastIndexOf("(") + 1, "?,");
                    } else {
                        builder.insert(builder.indexOf(",)") + 1, field.getName() + ",");
                        builder.insert(builder.lastIndexOf(",)") + 1, "?,");
                    }
                }
                builder.deleteCharAt(builder.indexOf((",)")));
                builder.deleteCharAt(builder.lastIndexOf(",)"));

                Object obj = c.newInstance();

                break;
            }
            case "update": {
                StringBuilder builder = new StringBuilder("update tbl" + objectName);
                for (Field field : c.getDeclaredFields()) {

                }
                break;
            }
            case "delete": {
                StringBuilder builder = new StringBuilder("delete from tbl" + objectName);
                break;
            }
            case "select": {
                break;
            }
        }
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
