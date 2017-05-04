/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean.move;

import com.cn.bean.FieldDescription;

/**
 *
 * @author LFeng
 */
public class FXInWareHouse {
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
@FieldDescription(description = "返修入库单号")
private String fxInWareHouseID;
@FieldDescription(description = "供应商代码")
private String supplierID;
@FieldDescription(description = "供应商名称") 
private String supplierName;
@FieldDescription(description = "返修入库部品状态")
private String partState;
@FieldDescription(description = "制单人员姓名")
private String fxRKProducerName;
@FieldDescription(description = "制单时间")
private String fxRKProduceTime;
@FieldDescription(description = "审核人员姓名")
private String fxRKAuditStaffName;
@FieldDescription(description = "审核时间")
private String fxRKAuditTime;
@FieldDescription(description = "打印标志")
private String printFlag;
@FieldDescription(description = "备注")
private String fxInWareHouseRemark;

    public String getFxInWareHouseID() {
        return fxInWareHouseID;
    }

    public void setFxInWareHouseID(String fxInWareHouseID) {
        this.fxInWareHouseID = fxInWareHouseID;
    }

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

    public String getPartState() {
        return partState;
    }

    public void setPartState(String partState) {
        this.partState = partState;
    }

    public String getFxRKProducerName() {
        return fxRKProducerName;
    }

    public void setFxRKProducerName(String fxRKProducerName) {
        this.fxRKProducerName = fxRKProducerName;
    }

    public String getFxRKProduceTime() {
        return fxRKProduceTime;
    }

    public void setFxRKProduceTime(String fxRKProduceTime) {
        this.fxRKProduceTime = fxRKProduceTime;
    }

    public String getFxRKAuditStaffName() {
        return fxRKAuditStaffName;
    }

    public void setFxRKAuditStaffName(String fxRKAuditStaffName) {
        this.fxRKAuditStaffName = fxRKAuditStaffName;
    }

    public String getFxRKAuditTime() {
        return fxRKAuditTime;
    }

    public void setFxRKAuditTime(String fxRKAuditTime) {
        this.fxRKAuditTime = fxRKAuditTime;
    }

    public String getPrintFlag() {
        return printFlag;
    }

    public void setPrintFlag(String printFlag) {
        this.printFlag = printFlag;
    }

    public String getFxInWareHouseRemark() {
        return fxInWareHouseRemark;
    }

    public void setFxInWareHouseRemark(String fxInWareHouseRemark) {
        this.fxInWareHouseRemark = fxInWareHouseRemark;
    }
}
