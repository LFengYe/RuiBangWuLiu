/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.controller;

import com.cn.bean.PlatformCompanyInfo;
import com.cn.bean.PlatFormDataBaseInfo;
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
public class PlatFormDataBaseInfoController {

    private static final Logger logger = Logger.getLogger(PlatFormDataBaseInfoController.class);

    public ArrayList<PlatFormDataBaseInfo> getPlatformUserInfoData() {
        DatabaseOpt opt;
        Connection conn = null;
        CallableStatement statement = null;
        try {
            opt = new DatabaseOpt();
            conn = opt.getConnectBase();
            statement = conn.prepareCall("select * from viewPlatformDataBaseInfo");
            ResultSet set = statement.executeQuery();
            ArrayList<PlatFormDataBaseInfo> result = new ArrayList<>();
            while (set.next()) {
                PlatFormDataBaseInfo baseInfo = new PlatFormDataBaseInfo();

                baseInfo.DataBaseID = set.getString("DataBaseID");
                baseInfo.CompanyID = set.getString("CompanyID");
                baseInfo.CompanyName = set.getString("CompanyName");
                baseInfo.MasterDataBaseName = set.getString("MasterDataBaseName");
                baseInfo.MasterDataBaseServer = set.getString("MasterDataBaseServer");
                baseInfo.HistoryDataBaseName = set.getString("HistoryDataBaseName");
                baseInfo.HistoryDataBaseServer = set.getString("HistoryDataBaseServer");
                baseInfo.DataBaseInfoRemark = set.getString("DataBaseInfoRemark");

                result.add(baseInfo);
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
