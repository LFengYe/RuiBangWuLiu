/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.controller;

import com.cn.bean.PlatformRight;
import com.cn.util.DatabaseOpt;
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
public class PlatformRightController {
    private static final Logger logger = Logger.getLogger(PlatformRightController.class);

    public ArrayList<PlatformRight> getPlatformRightData() {
        DatabaseOpt opt;
        Connection conn = null;
        CallableStatement statement = null;
        try {
            opt = new DatabaseOpt();
            conn = opt.getConnectBase();
            statement = conn.prepareCall("select * from tblPlatformRight");
            ResultSet set = statement.executeQuery();
            ArrayList<PlatformRight> result = new ArrayList<>();
            while (set.next()) {
                PlatformRight role = new PlatformRight();

                role.RightCode = set.getString("RightCode");
                role.RightName = set.getString("RightName");
                role.Righthyperlnk = set.getString("Righthyperlnk");
                
                result.add(role);
            }
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
}
