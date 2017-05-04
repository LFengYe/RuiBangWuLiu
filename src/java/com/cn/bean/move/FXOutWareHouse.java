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
public class FXOutWareHouse {

    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    @FieldDescription(description = "返修出库单号")
    private String fxOutWareHouseID;
    @FieldDescription(description = "供应商代码")
    private String supplierID;
    @FieldDescription(description = "供应商名称")
    private String supplierName;
    @FieldDescription(description = "返修出库部品状态")
    private String partState;
    @FieldDescription(description = "制单人员姓名")
    private String fxCKProducerName;
    @FieldDescription(description = "制单时间")
    private String fxCKProduceTime;
    @FieldDescription(description = "审核人员姓名")
    private String fxCKAuditStaffName;
    @FieldDescription(description = "审核时间")
    private String fxCKAuditTime;
    @FieldDescription(description = "打印标志")
    private String printFlag;
    @FieldDescription(description = "备注")
    private String fxOutWareHouseRemark;

    public String getFxOutWareHouseID() {
        return fxOutWareHouseID;
    }

    public void setFxOutWareHouseID(String fxOutWareHouseID) {
        this.fxOutWareHouseID = fxOutWareHouseID;
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

    public String getFxCKProducerName() {
        return fxCKProducerName;
    }

    public void setFxCKProducerName(String fxCKProducerName) {
        this.fxCKProducerName = fxCKProducerName;
    }

    public String getFxCKProduceTime() {
        return fxCKProduceTime;
    }

    public void setFxCKProduceTime(String fxCKProduceTime) {
        this.fxCKProduceTime = fxCKProduceTime;
    }

    public String getFxCKAuditStaffName() {
        return fxCKAuditStaffName;
    }

    public void setFxCKAuditStaffName(String fxCKAuditStaffName) {
        this.fxCKAuditStaffName = fxCKAuditStaffName;
    }

    public String getFxCKAuditTime() {
        return fxCKAuditTime;
    }

    public void setFxCKAuditTime(String fxCKAuditTime) {
        this.fxCKAuditTime = fxCKAuditTime;
    }

    public String getPrintFlag() {
        return printFlag;
    }

    public void setPrintFlag(String printFlag) {
        this.printFlag = printFlag;
    }

    public String getFxOutWareHouseRemark() {
        return fxOutWareHouseRemark;
    }

    public void setFxOutWareHouseRemark(String fxOutWareHouseRemark) {
        this.fxOutWareHouseRemark = fxOutWareHouseRemark;
    }
}
