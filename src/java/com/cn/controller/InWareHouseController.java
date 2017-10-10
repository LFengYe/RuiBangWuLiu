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
public class InWareHouseController {
    private static final Logger logger = Logger.getLogger(JHOutWareHouseController.class);
    
    /**
     * 获取供应商所有产品的最小入库批次
     * @param supplierID
     * @return 
     */
    public String getSupplierInboundBatch(String supplierID) {
        DatabaseOpt opt = new DatabaseOpt();
        CallableStatement statement = null;
        Connection conn = null;
        try {
            //String sql = "select MIN(InboundBatch) AS InboundBatch from tblDJInWareHouseList where SupplierID = '" + supplierID + "'";
            conn = opt.getConnect();
            statement = conn.prepareCall("{call tbGetMinInboundBatch(?, ?)}");
            statement.setString("SupplierID", supplierID);
            //statement.setString("PartState", partState);
            statement.registerOutParameter("MinInBoundBatch", Types.CHAR);
            statement.execute();
            return statement.getString("MinInBoundBatch");
        } catch (SQLException e) {
            logger.error("数据库执行出错", e);
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
}
