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
public class RKListForDjpRK {

    @FieldDescription(description = "入库时间")
    private String djRKProduceTime;
    @FieldDescription(description = "入库单号")
    private String djInWareHouseID;
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
    @FieldDescription(description = "原厂批次")
    private String originalBatch;
    @FieldDescription(description = "入库数量")
    private String djRKAmount;
    @FieldDescription(description = "制单员")
    private String djRKProducerName;
    @FieldDescription(description = "备注")
    private String djInWareHouseListRemark;

    public String getDjRKProduceTime() {
        return djRKProduceTime;
    }

    public void setDjRKProduceTime(String djRKProduceTime) {
        this.djRKProduceTime = djRKProduceTime;
    }

    public String getDjInWareHouseID() {
        return djInWareHouseID;
    }

    public void setDjInWareHouseID(String djInWareHouseID) {
        this.djInWareHouseID = djInWareHouseID;
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

    public String getDjRKAmount() {
        return djRKAmount;
    }

    public void setDjRKAmount(String djRKAmount) {
        this.djRKAmount = djRKAmount;
    }

    public String getDjRKProducerName() {
        return djRKProducerName;
    }

    public void setDjRKProducerName(String djRKProducerName) {
        this.djRKProducerName = djRKProducerName;
    }

    public String getDjInWareHouseListRemark() {
        return djInWareHouseListRemark;
    }

    public void setDjInWareHouseListRemark(String djInWareHouseListRemark) {
        this.djInWareHouseListRemark = djInWareHouseListRemark;
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

    public String getOriginalBatch() {
        return originalBatch;
    }

    public void setOriginalBatch(String originalBatch) {
        this.originalBatch = originalBatch;
    }
}
