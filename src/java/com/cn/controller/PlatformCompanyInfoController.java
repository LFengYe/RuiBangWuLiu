/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.controller;

import com.cn.bean.PlatformCompanyInfo;
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
public class PlatformCompanyInfoController {

    private static final Logger logger = Logger.getLogger(PlatformCompanyInfoController.class);

    public ArrayList<PlatformCompanyInfo> getPlatformCompanyInfoData() {
        DatabaseOpt opt;
        Connection conn = null;
        CallableStatement statement = null;
        try {
            opt = new DatabaseOpt();
            conn = opt.getConnectBase();
            statement = conn.prepareCall("select * from tblPlatformCompanyInfo");
            ResultSet set = statement.executeQuery();
            ArrayList<PlatformCompanyInfo> result = new ArrayList<>();
            while (set.next()) {
                PlatformCompanyInfo companyInfo = new PlatformCompanyInfo();

                companyInfo.CompanyID = set.getString("CompanyID");
                companyInfo.CompanyName = set.getString("CompanyName");
                companyInfo.CompanyNameAPP = set.getString("CompanyNameAPP");
                companyInfo.CompanyAddress = set.getString("CompanyAddress");
                companyInfo.LegalPersonName = set.getString("LegalPersonName");
                companyInfo.LegalPersonPhone = set.getString("LegalPersonPhone");
                companyInfo.ContactPersonName = set.getString("ContactPersonName");
                companyInfo.ContactPersonPhone = set.getString("ContactPersonPhone");
                companyInfo.CompanyCode = set.getString("CompanyCode");
                companyInfo.BusenessState = set.getString("BusenessState");
                companyInfo.CompanyWebSite = set.getString("CompanyWebSite");
                companyInfo.CompanyRemark = set.getString("CompanyRemark");

                result.add(companyInfo);
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
