/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean.pro;

/**
 *
 * @author LFeng
 */
public class KFJCLPForBPTH {
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    private String partCode;
    private String partID;
    private String partName;
    private String inboundBatch;
    private String kfJCLpAmount;
    private String wareHouseManagerName;

    public String getPartCode() {
        return partCode;
    }

    public void setPartCode(String partCode) {
        this.partCode = partCode;
    }

    public String getPartID() {
        return partID;
    }

    public void setPartID(String partID) {
        this.partID = partID;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getInboundBatch() {
        return inboundBatch;
    }

    public void setInboundBatch(String inboundBatch) {
        this.inboundBatch = inboundBatch;
    }

    public String getKfJCLpAmount() {
        return kfJCLpAmount;
    }

    public void setKfJCLpAmount(String kfJCLpAmount) {
        this.kfJCLpAmount = kfJCLpAmount;
    }

    public String getWareHouseManagerName() {
        return wareHouseManagerName;
    }

    public void setWareHouseManagerName(String wareHouseManagerName) {
        this.wareHouseManagerName = wareHouseManagerName;
    }
    private String checkBox;
    public String getCheckBox() {
        return "";
    }

    public void setCheckBox(String checkBox) {
        this.checkBox = checkBox;
    }
}
