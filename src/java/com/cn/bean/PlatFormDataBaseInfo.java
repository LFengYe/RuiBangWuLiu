/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean;

/**
 *
 * @author LFeng
 */
@ClassDescription(classDesc = "注册数据库")
public class PlatFormDataBaseInfo {

    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }

    private String dataBaseID;
    @FieldDescription(description = "公司编号")
    private String companyID;
    private String companyName;
    @FieldDescription(description = "工作数据库")
    private String masterDataBaseName;
    @FieldDescription(description = "工作服务器")
    private String masterDataBaseServer;
    @FieldDescription(description = "历史数据库")
    private String historyDataBaseName;
    @FieldDescription(description = "历史服务器")
    private String historyDataBaseServer;
    @FieldDescription(description = "备注")
    private String dataBaseInfoRemark;

    public String getDataBaseID() {
        return dataBaseID;
    }

    public void setDataBaseID(String dataBaseID) {
        this.dataBaseID = dataBaseID;
    }

    public String getCompanyID() {
        return companyID;
    }

    public void setCompanyID(String companyID) {
        this.companyID = companyID;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getMasterDataBaseName() {
        return masterDataBaseName;
    }

    public void setMasterDataBaseName(String masterDataBaseName) {
        this.masterDataBaseName = masterDataBaseName;
    }

    public String getMasterDataBaseServer() {
        return masterDataBaseServer;
    }

    public void setMasterDataBaseServer(String masterDataBaseServer) {
        this.masterDataBaseServer = masterDataBaseServer;
    }

    public String getHistoryDataBaseName() {
        return historyDataBaseName;
    }

    public void setHistoryDataBaseName(String historyDataBaseName) {
        this.historyDataBaseName = historyDataBaseName;
    }

    public String getHistoryDataBaseServer() {
        return historyDataBaseServer;
    }

    public void setHistoryDataBaseServer(String historyDataBaseServer) {
        this.historyDataBaseServer = historyDataBaseServer;
    }

    public String getDataBaseInfoRemark() {
        return dataBaseInfoRemark;
    }

    public void setDataBaseInfoRemark(String dataBaseInfoRemark) {
        this.dataBaseInfoRemark = dataBaseInfoRemark;
    }

}
