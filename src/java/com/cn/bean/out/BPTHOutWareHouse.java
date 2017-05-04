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
public class BPTHOutWareHouse {

    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    @FieldDescription(description = "部品退货出库单据号")
    private String bpTHOutWareHoseID;
    @FieldDescription(description = "供应商代码")
    private String supplierID;
    @FieldDescription(description = "供应商名称")
    private String supplierName;
    @FieldDescription(description = "部品状态")
    private String thPartState;
    @FieldDescription(description = "库管员姓名")
    private String wareHouseManagerName;
    @FieldDescription(description = "制单员姓名")
    private String bpTHProducerName;
    @FieldDescription(description = "制单时间")
    private String bpTHProduceTime;
    @FieldDescription(description = "审核员姓名")
    private String bpTHAuditStaffName;
    @FieldDescription(description = "审核时间")
    private String bpTHAuditTime;
    @FieldDescription(description = "打印标志")
    private String printFlag;
    @FieldDescription(description = "备注")
    private String bpTHOutWareHouseRemark;

    public String getBpTHOutWareHoseID() {
        return bpTHOutWareHoseID;
    }

    public void setBpTHOutWareHoseID(String bpTHOutWareHoseID) {
        this.bpTHOutWareHoseID = bpTHOutWareHoseID;
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

    public String getThPartState() {
        return thPartState;
    }

    public void setThPartState(String thPartState) {
        this.thPartState = thPartState;
    }

    public String getWareHouseManagerName() {
        return wareHouseManagerName;
    }

    public void setWareHouseManagerName(String wareHouseManagerName) {
        this.wareHouseManagerName = wareHouseManagerName;
    }

    public String getBpTHProducerName() {
        return bpTHProducerName;
    }

    public void setBpTHProducerName(String bpTHProducerName) {
        this.bpTHProducerName = bpTHProducerName;
    }

    public String getBpTHProduceTime() {
        return bpTHProduceTime;
    }

    public void setBpTHProduceTime(String bpTHProduceTime) {
        this.bpTHProduceTime = bpTHProduceTime;
    }

    public String getBpTHAuditStaffName() {
        return bpTHAuditStaffName;
    }

    public void setBpTHAuditStaffName(String bpTHAuditStaffName) {
        this.bpTHAuditStaffName = bpTHAuditStaffName;
    }

    public String getBpTHAuditTime() {
        return bpTHAuditTime;
    }

    public void setBpTHAuditTime(String bpTHAuditTime) {
        this.bpTHAuditTime = bpTHAuditTime;
    }

    public String getPrintFlag() {
        return printFlag;
    }

    public void setPrintFlag(String printFlag) {
        this.printFlag = printFlag;
    }

    public String getBpTHOutWareHouseRemark() {
        return bpTHOutWareHouseRemark;
    }

    public void setBpTHOutWareHouseRemark(String bpTHOutWareHouseRemark) {
        this.bpTHOutWareHouseRemark = bpTHOutWareHouseRemark;
    }
}