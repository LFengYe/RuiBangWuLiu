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
public class FZOutWareHouseList {
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
    private int fzCKAmount;
    private String fzOutWareHouseListRemark;
    @FieldDescription(description = "返修入库单号")
    private String fzOutWareHouseID;

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

    public int getFzCKAmount() {
        return fzCKAmount;
    }

    public void setFzCKAmount(int fzCKAmount) {
        this.fzCKAmount = fzCKAmount;
    }

    public String getFzOutWareHouseListRemark() {
        return fzOutWareHouseListRemark;
    }

    public void setFzOutWareHouseListRemark(String fzOutWareHouseListRemark) {
        this.fzOutWareHouseListRemark = fzOutWareHouseListRemark;
    }

    public String getFzOutWareHouseID() {
        return fzOutWareHouseID;
    }

    public void setFzOutWareHouseID(String fzOutWareHouseID) {
        this.fzOutWareHouseID = fzOutWareHouseID;
    }
}
