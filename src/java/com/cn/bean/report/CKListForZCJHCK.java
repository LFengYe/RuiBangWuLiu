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
public class CKListForZCJHCK {

    private String jhCKProduceTime;
    private String jhOutWareHouseID;
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
    private String packingNumber;
    private String lhAmount;
    private String zdCustomerID;
    private String zdCustomerName;
    private String jhCKProducerName;
    private String lhProgressListRemark;

    public String getJhCKProduceTime() {
        return jhCKProduceTime;
    }

    public void setJhCKProduceTime(String jhCKProduceTime) {
        this.jhCKProduceTime = jhCKProduceTime;
    }

    public String getJhOutWareHouseID() {
        return jhOutWareHouseID;
    }

    public void setJhOutWareHouseID(String jhOutWareHouseID) {
        this.jhOutWareHouseID = jhOutWareHouseID;
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

    public String getPackingNumber() {
        return packingNumber;
    }

    public void setPackingNumber(String packingNumber) {
        this.packingNumber = packingNumber;
    }

    public String getLhAmount() {
        return lhAmount;
    }

    public void setLhAmount(String lhAmount) {
        this.lhAmount = lhAmount;
    }

    public String getZdCustomerID() {
        return zdCustomerID;
    }

    public void setZdCustomerID(String zdCustomerID) {
        this.zdCustomerID = zdCustomerID;
    }

    public String getJhCKProducerName() {
        return jhCKProducerName;
    }

    public void setJhCKProducerName(String jhCKProducerName) {
        this.jhCKProducerName = jhCKProducerName;
    }

    public String getLhProgressListRemark() {
        return lhProgressListRemark;
    }

    public void setLhProgressListRemark(String lhProgressListRemark) {
        this.lhProgressListRemark = lhProgressListRemark;
    }

    public String getZdCustomerName() {
        return zdCustomerName;
    }

    public void setZdCustomerName(String zdCustomerName) {
        this.zdCustomerName = zdCustomerName;
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
