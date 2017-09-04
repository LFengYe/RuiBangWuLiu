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
public class RKListForFxpRK {

    private String fxRKAuditTime;
    private String fxInWareHouseID;
    @FieldDescription(description = "出入类型")
    private String outInType;
    @FieldDescription(description = "出入方式")
    private String outInMethod;
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
    private String fxRKAmount;
    private String fxRKProducerName;
    private String fxInWareHouseListRemark;

    public String getFxRKAuditTime() {
        return fxRKAuditTime;
    }

    public void setFxRKAuditTime(String fxRKAuditTime) {
        this.fxRKAuditTime = fxRKAuditTime;
    }

    public String getFxInWareHouseID() {
        return fxInWareHouseID;
    }

    public void setFxInWareHouseID(String fxInWareHouseID) {
        this.fxInWareHouseID = fxInWareHouseID;
    }

    public String getOutInType() {
        return outInType;
    }

    public void setOutInType(String outInType) {
        this.outInType = outInType;
    }

    public String getOutInMethod() {
        return outInMethod;
    }

    public void setOutInMethod(String outInMethod) {
        this.outInMethod = outInMethod;
    }

    public String getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(String supplierID) {
        this.supplierID = supplierID;
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

    public String getFxRKAmount() {
        return fxRKAmount;
    }

    public void setFxRKAmount(String fxRKAmount) {
        this.fxRKAmount = fxRKAmount;
    }

    public String getFxRKProducerName() {
        return fxRKProducerName;
    }

    public void setFxRKProducerName(String fxRKProducerName) {
        this.fxRKProducerName = fxRKProducerName;
    }

    public String getFxInWareHouseListRemark() {
        return fxInWareHouseListRemark;
    }

    public void setFxInWareHouseListRemark(String fxInWareHouseListRemark) {
        this.fxInWareHouseListRemark = fxInWareHouseListRemark;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getPartID() {
        return partID;
    }

    public void setPartID(String partID) {
        this.partID = partID;
    }
}
