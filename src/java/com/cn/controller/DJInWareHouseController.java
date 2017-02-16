/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.controller;

import com.alibaba.fastjson.JSONObject;
import com.cn.bean.DJWareHouse;
import com.cn.util.DatabaseOpt;
import com.cn.util.Units;
import java.io.FileNotFoundException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 *
 * @author LFeng
 */
public class DJInWareHouseController {
    private static final Logger logger = Logger.getLogger(DJInWareHouseController.class);
    
    public String getInWareHouseData() {
        DatabaseOpt opt;
        Connection conn = null;
        CallableStatement statement = null;
        try {
            opt = new DatabaseOpt();
            conn = opt.getConnect();
            statement = conn.prepareCall("select * from tblDJInWareHouse");
            ResultSet set = statement.executeQuery();
            ArrayList<DJWareHouse> result = new ArrayList<>();
            while (set.next()) {
                DJWareHouse dJWareHouse = new DJWareHouse();
                
                dJWareHouse.DJInWareHouseID = set.getString("DJInWareHouseID");
                dJWareHouse.SupplierID = set.getInt("SupplierID");
                dJWareHouse.SupplierName = set.getString("SupplierName");
                dJWareHouse.InboundBatch = set.getString("InboundBatch");
                dJWareHouse.DJRKProducerName = set.getString("DJRKProducerName");
                dJWareHouse.DJRKProduceTime = set.getString("DJRKProduceTime");
                dJWareHouse.DJRKAuditStaffName = set.getString("DJRKAuditStaffName");
                dJWareHouse.DJRKAuditTime = set.getString("DJRKAuditTime");
                dJWareHouse.PrintFlag = set.getString("PrintFlag");
                dJWareHouse.DJINWareHousRemark = set.getString("DJINWareHousRemark");
                
                result.add(dJWareHouse);
            }
            
            String path = this.getClass().getClassLoader().getResource("/").getPath().replaceAll("%20", " ");
            StringBuilder builder = new StringBuilder(Units.returnFileContext(path, "DJInWareHouse.json"));
            builder.insert(builder.lastIndexOf("}"), ", \"datas\":" + JSONObject.toJSONString(result, Units.features));
            
            System.out.println(":" + builder.toString());
            return builder.toString();
        } catch (SQLException ex) {
            logger.error("数据库执行出错", ex);
        } catch (FileNotFoundException ex) {
            logger.error("找不到指定文件", ex);
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
    
    public int addInWareHouseData(DJWareHouse dJWareHouse) {
        DatabaseOpt opt;
        Connection conn = null;
        CallableStatement statement = null;
        try {
            opt = new DatabaseOpt();
            conn = opt.getConnect();
            statement = conn.prepareCall("insert into tblDJInWareHouse(DJInWareHouseID,SupplierID,SupplierName,InboundBatch,DJRKProducerName,DJRKProduceTime,DJRKAuditStaffName,DJRKAuditTime,PrintFlag,DJINWareHousRemark) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            statement.setString(1, dJWareHouse.DJInWareHouseID);
            System.out.println("houseID:" + dJWareHouse.DJInWareHouseID);
            statement.setInt(2, dJWareHouse.SupplierID);
            statement.setString(3, dJWareHouse.SupplierName);
            statement.setString(4, dJWareHouse.InboundBatch);
            statement.setString(5, dJWareHouse.DJRKProducerName);
            statement.setString(6, dJWareHouse.DJRKProduceTime);
            statement.setString(7, dJWareHouse.DJRKAuditStaffName);
            statement.setString(8, null);
            statement.setString(9, null);
            statement.setString(10, dJWareHouse.DJINWareHousRemark);
            statement.executeUpdate();
            
            return 0;
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
        return -1;
    }
}
