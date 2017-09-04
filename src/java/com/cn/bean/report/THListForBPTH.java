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
public class THListForBPTH {

    @FieldDescription(description = "审核时间")
    private String bpTHAuditTime;
    @FieldDescription(description = "退货出库单号")
    private String bpTHOutWareHoseID;
    @FieldDescription(description = "出入库类型")
    private String outInType;
    @FieldDescription(description = "出入库方式")
    private String outInMethod;
    @FieldDescription(description = "供应商代码")
    private String supplierID;
    @FieldDescription(description = "供应商名称", operate = "display")
    private String supplierName;
    @FieldDescription(description = "部品件号")
    private String partCode;
    @FieldDescription(description = "部品名称", operate = "display")
    private String partName;
    @FieldDescription(description = "部品编号", operate = "display")
    private String partID;
    @FieldDescription(description = "入库批次")
    private String inboundBatch;
    @FieldDescription(description = "退货出库数量")
    private String thCKAmount;
    @FieldDescription(description = "制单人")
    private String bpTHProducerName;
    @FieldDescription(description = "备注")
    private String bpTHOutWareHouseListRemark;

    public String getBpTHAuditTime() {
        return bpTHAuditTime;
    }

    public void setBpTHAuditTime(String bpTHAuditTime) {
        this.bpTHAuditTime = bpTHAuditTime;
    }

    public String getBpTHOutWareHoseID() {
        return bpTHOutWareHoseID;
    }

    public void setBpTHOutWareHoseID(String bpTHOutWareHoseID) {
        this.bpTHOutWareHoseID = bpTHOutWareHoseID;
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

    public String getThCKAmount() {
        return thCKAmount;
    }

    public void setThCKAmount(String thCKAmount) {
        this.thCKAmount = thCKAmount;
    }

    public String getBpTHProducerName() {
        return bpTHProducerName;
    }

    public void setBpTHProducerName(String bpTHProducerName) {
        this.bpTHProducerName = bpTHProducerName;
    }

    public String getBpTHOutWareHouseListRemark() {
        return bpTHOutWareHouseListRemark;
    }

    public void setBpTHOutWareHouseListRemark(String bpTHOutWareHouseListRemark) {
        this.bpTHOutWareHouseListRemark = bpTHOutWareHouseListRemark;
    }
}
