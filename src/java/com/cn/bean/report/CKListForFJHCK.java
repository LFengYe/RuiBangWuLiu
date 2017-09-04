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
public class CKListForFJHCK {

    private String fjHCKTime;
    private String fjHOutWareHouseID;
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
    private String fjHCKAmount;
    private String fzDCustomerID;
    private String fzDCustomerName;
    private String fjHCKProducerName;
    private String fjHOutWareHouseListRemark;

    public String getFjHCKTime() {
        return fjHCKTime;
    }

    public void setFjHCKTime(String fjHCKTime) {
        this.fjHCKTime = fjHCKTime;
    }

    public String getFjHOutWareHouseID() {
        return fjHOutWareHouseID;
    }

    public void setFjHOutWareHouseID(String fjHOutWareHouseID) {
        this.fjHOutWareHouseID = fjHOutWareHouseID;
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

    public String getFjHCKAmount() {
        return fjHCKAmount;
    }

    public void setFjHCKAmount(String fjHCKAmount) {
        this.fjHCKAmount = fjHCKAmount;
    }

    public String getFzDCustomerID() {
        return fzDCustomerID;
    }

    public void setFzDCustomerID(String fzDCustomerID) {
        this.fzDCustomerID = fzDCustomerID;
    }

    public String getFzDCustomerName() {
        return fzDCustomerName;
    }

    public void setFzDCustomerName(String fzDCustomerName) {
        this.fzDCustomerName = fzDCustomerName;
    }

    public String getFjHCKProducerName() {
        return fjHCKProducerName;
    }

    public void setFjHCKProducerName(String fjHCKProducerName) {
        this.fjHCKProducerName = fjHCKProducerName;
    }

    public String getFjHOutWareHouseListRemark() {
        return fjHOutWareHouseListRemark;
    }

    public void setFjHOutWareHouseListRemark(String fjHOutWareHouseListRemark) {
        this.fjHOutWareHouseListRemark = fjHOutWareHouseListRemark;
    }
}
