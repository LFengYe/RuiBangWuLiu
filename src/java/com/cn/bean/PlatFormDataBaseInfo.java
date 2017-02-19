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
public class PlatFormDataBaseInfo {

    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }

    private String dataBaseID;
    private String companyID;
    private String companyName;
    private String masterDataBaseName;
    private String masterDataBaseServer;
    private String historyDataBaseName;
    private String historyDataBaseServer;
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
