/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.listener;

import com.alibaba.fastjson.JSONObject;
import com.cn.bean.AreaLedIPInfo;
import com.cn.bean.Customer;
import com.cn.bean.GYSPartContainerInfo;
import com.cn.bean.PartBaseInfo;
import com.cn.bean.PartBomInfo;
import com.cn.bean.PartCategory;
import com.cn.bean.PartStore;
import com.cn.controller.CommonController;
import com.cn.util.DatabaseOpt;
import com.cn.util.RedisAPI;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.log4j.Logger;

/**
 *
 * @author LFeng
 */
public class StartInit implements ServletContextListener{
    private static final Logger logger = Logger.getLogger(ServletContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("系统初始化开始...");
        
        CommonController commonController = new CommonController();
        DatabaseOpt opt = new DatabaseOpt();
        try {
            RedisAPI.flushDB();
            
            /*导入部品基础信息到Redis中*/
            List<Object> partBaseInfo = commonController.dataBaseQuery("table", "com.cn.bean.", "PartBaseInfo", "*", "", Integer.MAX_VALUE, 1, "PartCode", 0, opt.getConnect());
            Iterator<Object> iterator = partBaseInfo.iterator();
            while (iterator.hasNext()) {
                PartBaseInfo baseInfo = (PartBaseInfo) iterator.next();
                RedisAPI.set("partBaseInfo_" + baseInfo.getPartCode(), JSONObject.toJSONString(baseInfo));
            }

            /*导入客户基础信息到Redis中*/
            List<Object> customerList = commonController.dataBaseQuery("table", "com.cn.bean.", "Customer", "*", "", Integer.MAX_VALUE, 1, "CustomerID", 0, opt.getConnect());
            Iterator<Object> iterator1 = customerList.iterator();
            while (iterator1.hasNext()) {
                Customer customer = (Customer) iterator1.next();
                RedisAPI.set("customer_" + customer.getCustomerID(), JSONObject.toJSONString(customer));
            }

            /*导入出入库盛具信息到Redis中*/
            List<Object> containerInfoList = commonController.dataBaseQuery("table", "com.cn.bean.", "GYSPartContainerInfo", "*", "", Integer.MAX_VALUE, 1, "PartCode", 0, opt.getConnect());
            Iterator<Object> iterator2 = containerInfoList.iterator();
            while (iterator2.hasNext()) {
                GYSPartContainerInfo containerInfo = (GYSPartContainerInfo) iterator2.next();
                RedisAPI.set(containerInfo.getSupplierID() + "_" + containerInfo.getPartCode(), JSONObject.toJSONString(containerInfo));
            }

            /*导入存放地址信息到Redis中*/
            List<Object> ledIpInfoList = commonController.dataBaseQuery("table", "com.cn.bean.", "AreaLedIPInfo", "*", "", Integer.MAX_VALUE, 1, "addressCode", 0, opt.getConnect());
            Iterator<Object> iterator3 = ledIpInfoList.iterator();
            while (iterator3.hasNext()) {
                AreaLedIPInfo ledIpInfo = (AreaLedIPInfo) iterator3.next();
                RedisAPI.set("ledIpInfo_" + ledIpInfo.getAddressCode(), JSONObject.toJSONString(ledIpInfo));
            }

            /*导入出入库盛具信息到Redis中*/
            List<Object> partStoreList = commonController.dataBaseQuery("table", "com.cn.bean.", "PartStore", "*", "", Integer.MAX_VALUE, 1, "PartCode", 0, opt.getConnect());
            Iterator<Object> iterator4 = partStoreList.iterator();
            while (iterator4.hasNext()) {
                PartStore partStore = (PartStore) iterator4.next();
                RedisAPI.set("partStore_" + partStore.getSupplierID() + "_" + partStore.getPartCode(), JSONObject.toJSONString(partStore));
            }
            
            List<Object> partCategory = commonController.dataBaseQuery("table", "com.cn.bean.", "PartCategory", "*", "", Integer.MAX_VALUE, 1, "PartCategoryName", 0, opt.getConnect());
            Iterator<Object> iterator5 = partCategory.iterator();
            while (iterator5.hasNext()) {
                PartCategory category = (PartCategory) iterator5.next();
                RedisAPI.set("partCategory_" + category.getPartCategoryName(), JSONObject.toJSONString(category));
            }
            
            List<Object> partBomInfo = commonController.dataBaseQuery("table", "com.cn.bean.", "PartBomInfo", "*", "", Integer.MAX_VALUE, 1, "SupplierID", 0, opt.getConnect());
            Iterator<Object> iterator6 = partBomInfo.iterator();
            while (iterator6.hasNext()) {
                PartBomInfo bomInfo = (PartBomInfo) iterator6.next();
                RedisAPI.push("bomInfo_" + bomInfo.getSupplierID() + "_" + bomInfo.getZcPartCode(), JSONObject.toJSONString(bomInfo));
            }

            //logger.info("初始化成功!导入部品信息" + partBaseInfo.size() + "条,导入客户信息" + customerList.size() + "条,导入部品盛具信息" + containerInfoList.size() + "条");
            logger.info("系统初始化结束...");
        } catch (Exception e) {
            logger.error("初始化出错!", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            DriverManager.deregisterDriver(DriverManager.getDrivers().nextElement());
        } catch (SQLException ex) {
            logger.info("JDBC驱动注销出错!");
        }
        logger.info("系统停止...");
    }
    
}
