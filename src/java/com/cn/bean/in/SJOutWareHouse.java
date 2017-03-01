/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean.in;

import com.cn.bean.FieldDescription;

/**
 *
 * @author LFeng
 */
public class SJOutWareHouse {
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }

    @FieldDescription(description = "送检出库单号")
    private String sjOutWareHouseID;
    @FieldDescription(description = "供应商代码")
    private String supplierID;
    @FieldDescription(description = "供应商名称")
    private String supplierName;
    @FieldDescription(description = "制单人员姓名")
    private String sjCKProducerName;
    @FieldDescription(description = "制单时间", type = "date")
    private String sjCKProduceTime;
    @FieldDescription(description = "审核人员姓名")
    private String sjCKAuditStaffName;
    @FieldDescription(description = "审核时间")
    private String sjCKAuditTime;
    @FieldDescription(description = "打印标志")
    private String printFlag;
    @FieldDescription(description = "备注")
    private String sjOutBackWareHouseRemark;

    public String getSjOutWareHouseID() {
        return sjOutWareHouseID;
    }

    public void setSjOutWareHouseID(String sjOutWareHouseID) {
        this.sjOutWareHouseID = sjOutWareHouseID;
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

    public String getSjCKProducerName() {
        return sjCKProducerName;
    }

    public void setSjCKProducerName(String sjCKProducerName) {
        this.sjCKProducerName = sjCKProducerName;
    }

    public String getSjCKProduceTime() {
        return sjCKProduceTime;
    }

    public void setSjCKProduceTime(String sjCKProduceTime) {
        this.sjCKProduceTime = sjCKProduceTime;
    }

    public String getSjCKAuditStaffName() {
        return sjCKAuditStaffName;
    }

    public void setSjCKAuditStaffName(String sjCKAuditStaffName) {
        this.sjCKAuditStaffName = sjCKAuditStaffName;
    }

    public String getSjCKAuditTime() {
        return sjCKAuditTime;
    }

    public void setSjCKAuditTime(String sjCKAuditTime) {
        this.sjCKAuditTime = sjCKAuditTime;
    }

    public String getPrintFlag() {
        return printFlag;
    }

    public void setPrintFlag(String printFlag) {
        this.printFlag = printFlag;
    }

    public String getSjOutBackWareHouseRemark() {
        return sjOutBackWareHouseRemark;
    }

    public void setSjOutBackWareHouseRemark(String sjOutBackWareHouseRemark) {
        this.sjOutBackWareHouseRemark = sjOutBackWareHouseRemark;
    }

    
}
