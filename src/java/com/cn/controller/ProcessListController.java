/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.controller;

import com.cn.util.DatabaseOpt;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import org.apache.log4j.Logger;

/**
 *
 * @author LFeng
 */
public class ProcessListController {
    private static final Logger logger = Logger.getLogger(ProcessListController.class);
    
    /**
     * 
     * @param jhOutWareHouseID
     * @param PartCode
     * @param supplierID
     * @param inboundBatch
     * @param jhStatus
     * @param remark
     * @return 
     */
    public int bhConfirmForKGY(String jhOutWareHouseID, String PartCode, String supplierID, String inboundBatch, int jhStatus, String remark) {
        DatabaseOpt opt;
        Connection conn = null;
        CallableStatement statement = null;
        
        try {
            opt = new DatabaseOpt();
            conn = opt.getConnect();//获取数据库连接
            statement = conn.prepareCall("{call tbConfirmBHListForKGYWithItem(?, ?, ?, ?, ?, ?, ?)}");
            statement.setString("JHOutWareHouseID", jhOutWareHouseID);
            statement.setString("PartCode", PartCode);
            statement.setString("SupplierID", supplierID);
            statement.setString("InboundBatch", inboundBatch);
            statement.setString("Remark", remark);
            statement.setInt("JHStatus", jhStatus);
            statement.registerOutParameter("result", Types.INTEGER);
            statement.executeUpdate();
            return statement.getInt("result");
        } catch (SQLException ex) {
            logger.error("数据库执行出错", ex);
        } finally {
            //关闭数据库连接
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
    
    public int bhConfirmForBHY(String jhOutWareHouseID, String PartCode, String supplierID, int pickingNumber, String inboundBatch) {
        DatabaseOpt opt;
        Connection conn = null;
        CallableStatement statement = null;
        
        try {
            opt = new DatabaseOpt();
            conn = opt.getConnect();
            statement = conn.prepareCall("{call tbConfirmBHListForBHY(?, ?, ?, ?, ?, ?)}");
            statement.setString("JHOutWareHouseID", jhOutWareHouseID);
            statement.setString("PartCode", PartCode);
            statement.setString("SupplierID", supplierID);
            statement.setString("InboundBatch", inboundBatch);
            statement.setInt("PackingNumber", pickingNumber);
            statement.registerOutParameter("confirmRes", Types.INTEGER);
            statement.executeUpdate();
            return statement.getInt("confirmRes");
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
