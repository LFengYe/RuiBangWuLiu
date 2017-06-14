/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean.out;

import com.cn.bean.FieldDescription;

/**
 *
 * @author LFeng
 */
public class FZInWareHouseList {
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    @FieldDescription(description = "部品编号", operate = "display")
    private String partID;
    @FieldDescription(description = "部品名称", operate = "display")
    private String partName;
    @FieldDescription(description = "部品件号")
    private String partCode;
    @FieldDescription(description = "分装入库数量")
    private int fzRKAmount;
    private String fzInWareHouseListRemark;
    @FieldDescription(description = "返修入库单号")
    private String fzInWareHouseID;

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

    public String getPartCode() {
        return partCode;
    }

    public void setPartCode(String partCode) {
        this.partCode = partCode;
    }

    public int getFzRKAmount() {
        return fzRKAmount;
    }

    public void setFzRKAmount(int fzRKAmount) {
        this.fzRKAmount = fzRKAmount;
    }

    public String getFzInWareHouseListRemark() {
        return fzInWareHouseListRemark;
    }

    public void setFzInWareHouseListRemark(String fzInWareHouseListRemark) {
        this.fzInWareHouseListRemark = fzInWareHouseListRemark;
    }

    public String getFzInWareHouseID() {
        return fzInWareHouseID;
    }

    public void setFzInWareHouseID(String fzInWareHouseID) {
        this.fzInWareHouseID = fzInWareHouseID;
    }
}
