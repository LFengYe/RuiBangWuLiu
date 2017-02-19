/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.controller;

import com.cn.util.DatabaseOpt;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 *
 * @author LFeng
 */
public class PlatformUserInfoController {
    private static final Logger logger = Logger.getLogger(PlatformUserInfoController.class);

    /**
     * 
     * @param username
     * @param password
     * @return -1 -- 登陆出错 | 0 -- 登陆成功 | 1 -- 用户名不存在 | 2 -- 用户名或密码错误
     */
    public int userLogin(String username, String password) {
        DatabaseOpt opt;
        Connection conn = null;
        CallableStatement statement = null;
        
        try {
            opt = new DatabaseOpt();
            conn = opt.getConnectBase();
            statement = conn.prepareCall("select * from tblPlatformUserInfo where UserLoginAccount = ?");
            statement.setString(1, username);
            ResultSet set = statement.executeQuery();
            
            while (set.next()) {
                if (password.compareTo(set.getString("UserLoginPassWord")) == 0) {
                    return 0;
                } else {
                    return 2;
                }
            }
            
            return 1;
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
    
    /*
    public ArrayList<PlatformUserInfo> getPlatformUserInfoData() {
        DatabaseOpt opt;
        Connection conn = null;
        CallableStatement statement = null;
        try {
            opt = new DatabaseOpt();
            conn = opt.getConnectBase();
            statement = conn.prepareCall("select * from tblPlatformUserInfo");
            ResultSet set = statement.executeQuery();
            ArrayList<PlatformUserInfo> result = new ArrayList<>();
            while (set.next()) {
                PlatformUserInfo role = new PlatformUserInfo();
                
                role.UserLoginAccount = set.getString("UserLoginAccount");
                role.UserLoginDBName = set.getString("UserLoginDBName");
                role.UserLoginPassWord = set.getString("UserLoginPassWord");
                role.UserInfoRemark = set.getString("UserInfoRemark");
                
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
    */
}
