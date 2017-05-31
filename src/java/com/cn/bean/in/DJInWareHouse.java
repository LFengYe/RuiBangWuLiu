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
public class DJInWareHouse {

    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    @FieldDescription(description = "待检入库单号")
    private String djInWareHouseID;
    @FieldDescription(description = "供应商代码")
    private String supplierID;
    @FieldDescription(description = "供应商名称", operate = "display")
    private String supplierName;
    @FieldDescription(description = "入库批次")
    private String inboundBatch;
    @FieldDescription(description = "制单人员姓名")
    private String djRKProducerName;
    @FieldDescription(description = "制单时间", type = "date")
    private String djRKProduceTime;
    @FieldDescription(description = "审核人员姓名")
    private String djRKAuditStaffName;
    @FieldDescription(description = "审核时间")
    private String djRKAuditTime;
    @FieldDescription(description = "打印标志")
    private String printFlag;
    @FieldDescription(description = "备注")
    private String djINWareHousRemark;

    public String getDjInWareHouseID() {
        return djInWareHouseID;
    }

    public void setDjInWareHouseID(String djInWareHouseID) {
        this.djInWareHouseID = djInWareHouseID;
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

    public String getInboundBatch() {
        return inboundBatch;
    }

    public void setInboundBatch(String inboundBatch) {
        this.inboundBatch = inboundBatch;
    }

    public String getDjRKProducerName() {
        return djRKProducerName;
    }

    public void setDjRKProducerName(String djRKProducerName) {
        this.djRKProducerName = djRKProducerName;
    }

    public String getDjRKProduceTime() {
        return djRKProduceTime;
    }

    public void setDjRKProduceTime(String djRKProduceTime) {
        this.djRKProduceTime = djRKProduceTime;
    }

    public String getDjRKAuditStaffName() {
        return djRKAuditStaffName;
    }

    public void setDjRKAuditStaffName(String djRKAuditStaffName) {
        this.djRKAuditStaffName = djRKAuditStaffName;
    }

    public String getDjRKAuditTime() {
        return djRKAuditTime;
    }

    public void setDjRKAuditTime(String djRKAuditTime) {
        this.djRKAuditTime = djRKAuditTime;
    }

    public String getPrintFlag() {
        return printFlag;
    }

    public void setPrintFlag(String printFlag) {
        this.printFlag = printFlag;
    }

    public String getDjINWareHousRemark() {
        return djINWareHousRemark;
    }

    public void setDjINWareHousRemark(String djINWareHousRemark) {
        this.djINWareHousRemark = djINWareHousRemark;
    }
}
