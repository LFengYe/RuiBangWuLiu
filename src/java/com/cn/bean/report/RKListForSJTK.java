/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean.report;

import com.cn.bean.FieldDescription;

/**
 *
 * @author LFeng
 */
public class RKListForSJTK {
    private String sjTKAuditTime;
    private String sjBackWareHouseID;
    @FieldDescription(description = "出入类型")
    private String outInType;
    @FieldDescription(description = "出入方式")
    private String outInWethod;
    @FieldDescription(description = "供应商编号")
    private String supplierID;
    @FieldDescription(description = "供应商名称", operate = "display")
    private String supplierName;
    @FieldDescription(description = "部品件号")
    private String partCode;
    @FieldDescription(description = "部品编号", operate = "display")
    private String partID;
    @FieldDescription(description = "部品名称", operate = "display")
    private String partName;
    @FieldDescription(description = "入库批次")
    private String inboundBatch;
    private String sjTKAmount;
    private String sjTKProducerName;
    private String sjBackWareHouseListRemark;

    public String getSjTKAuditTime() {
        return sjTKAuditTime;
    }

    public void setSjTKAuditTime(String sjTKAuditTime) {
        this.sjTKAuditTime = sjTKAuditTime;
    }

    public String getSjBackWareHouseID() {
        return sjBackWareHouseID;
    }

    public void setSjBackWareHouseID(String sjBackWareHouseID) {
        this.sjBackWareHouseID = sjBackWareHouseID;
    }

    public String getOutInType() {
        return outInType;
    }

    public void setOutInType(String outInType) {
        this.outInType = outInType;
    }

    public String getOutInWethod() {
        return outInWethod;
    }

    public void setOutInWethod(String outInWethod) {
        this.outInWethod = outInWethod;
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

    public String getSjTKAmount() {
        return sjTKAmount;
    }

    public void setSjTKAmount(String sjTKAmount) {
        this.sjTKAmount = sjTKAmount;
    }

    public String getSjTKProducerName() {
        return sjTKProducerName;
    }

    public void setSjTKProducerName(String sjTKProducerName) {
        this.sjTKProducerName = sjTKProducerName;
    }

    public String getSjBackWareHouseListRemark() {
        return sjBackWareHouseListRemark;
    }

    public void setSjBackWareHouseListRemark(String sjBackWareHouseListRemark) {
        this.sjBackWareHouseListRemark = sjBackWareHouseListRemark;
    }
    
}
