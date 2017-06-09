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
public class FJHOutWareHouseList {

    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    @FieldDescription(description = "行号", operate = "display")
    private int listNumber;
    @FieldDescription(description = "失败原因", operate = "display")
    private String failedReason;
//    @FieldDescription(description = "非终端客户")
//    private String fzdCustomerID;
//    @FieldDescription(description = "非终端客户")
//    private String fzdCustomerName;
    @FieldDescription(description = "供应商代码", operate = "import")
    private String supplierID;
    @FieldDescription(description = "供应商名称", operate = "display")
    private String supplierName;
    @FieldDescription(description = "部品编号", operate = "display")
    private String partID;
    @FieldDescription(description = "部品名称", operate = "display")
    private String partName;
    @FieldDescription(description = "部品件号", operate = "import")
    private String partCode;
    @FieldDescription(description = "入库批次")
    private String inboundBatch;
    @FieldDescription(description = "部品单位", operate = "display")
    private String partUnit;
    @FieldDescription(description = "车型", operate = "display")
    private String autoStylingName;
    @FieldDescription(description = "数量", operate = "import")
    private int fjhCKAmount;
    @FieldDescription(description = "非计划出库完成时间")
    private String fjhCKTime;
    @FieldDescription(description = "备注", operate = "import")
    private String fjhOutWareHouseListRemark;
    @FieldDescription(description = "非计划出库单号")
    private String fjhOutWareHouseID;
    @FieldDescription(description = "库管员")
    private String wareHouseManagerName;

    public String getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(String supplierID) {
        this.supplierID = supplierID;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
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

    public String getPartCode() {
        return partCode;
    }

    public void setPartCode(String partCode) {
        this.partCode = partCode;
    }

    public String getInboundBatch() {
        return inboundBatch;
    }

    public void setInboundBatch(String inboundBatch) {
        this.inboundBatch = inboundBatch;
    }

    public String getPartUnit() {
        return partUnit;
    }

    public void setPartUnit(String partUnit) {
        this.partUnit = partUnit;
    }

    public String getAutoStylingName() {
        return autoStylingName;
    }

    public void setAutoStylingName(String autoStylingName) {
        this.autoStylingName = autoStylingName;
    }

    public int getFjhCKAmount() {
        return fjhCKAmount;
    }

    public void setFjhCKAmount(int fjhCKAmount) {
        this.fjhCKAmount = fjhCKAmount;
    }

    public String getFjhCKTime() {
        return fjhCKTime;
    }

    public void setFjhCKTime(String fjhCKTime) {
        this.fjhCKTime = fjhCKTime;
    }

    public String getFjhOutWareHouseListRemark() {
        return fjhOutWareHouseListRemark;
    }

    public void setFjhOutWareHouseListRemark(String fjhOutWareHouseListRemark) {
        this.fjhOutWareHouseListRemark = fjhOutWareHouseListRemark;
    }

    public String getFjhOutWareHouseID() {
        return fjhOutWareHouseID;
    }

    public void setFjhOutWareHouseID(String fjhOutWareHouseID) {
        this.fjhOutWareHouseID = fjhOutWareHouseID;
    }

    public String getWareHouseManagerName() {
        return wareHouseManagerName;
    }

    public void setWareHouseManagerName(String wareHouseManagerName) {
        this.wareHouseManagerName = wareHouseManagerName;
    }

    public String getFailedReason() {
        return failedReason;
    }

    public void setFailedReason(String failedReason) {
        this.failedReason = failedReason;
    }

    public int getListNumber() {
        return listNumber;
    }

    public void setListNumber(int listNumber) {
        this.listNumber = listNumber;
    }
}
