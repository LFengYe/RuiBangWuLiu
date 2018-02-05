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
public class CKListForFxpCK {

    @FieldDescription(description = "返修出库时间")
    private String fxCKAuditTime;
    @FieldDescription(description = "返修出库单号")
    private String fxOutWareHouseID;
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
    @FieldDescription(description = "返修出库数量")
    private String fxCKAmount;
    @FieldDescription(description = "制单员")
    private String fxCKProducerName;
    @FieldDescription(description = "备注")
    private String fxOutWareHouseListRemark;

    public String getFxCKAuditTime() {
        return fxCKAuditTime;
    }

    public void setFxCKAuditTime(String fxCKAuditTime) {
        this.fxCKAuditTime = fxCKAuditTime;
    }

    public String getFxOutWareHouseID() {
        return fxOutWareHouseID;
    }

    public void setFxOutWareHouseID(String fxOutWareHouseID) {
        this.fxOutWareHouseID = fxOutWareHouseID;
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

    public String getInboundBatch() {
        return inboundBatch;
    }

    public void setInboundBatch(String inboundBatch) {
        this.inboundBatch = inboundBatch;
    }

    public String getFxCKAmount() {
        return fxCKAmount;
    }

    public void setFxCKAmount(String fxCKAmount) {
        this.fxCKAmount = fxCKAmount;
    }

    public String getFxCKProducerName() {
        return fxCKProducerName;
    }

    public void setFxCKProducerName(String fxCKProducerName) {
        this.fxCKProducerName = fxCKProducerName;
    }

    public String getFxOutWareHouseListRemark() {
        return fxOutWareHouseListRemark;
    }

    public void setFxOutWareHouseListRemark(String fxOutWareHouseListRemark) {
        this.fxOutWareHouseListRemark = fxOutWareHouseListRemark;
    }
}
