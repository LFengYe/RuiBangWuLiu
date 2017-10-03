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
public class RKListForZDTK {

    private String zdCustomerID;
    @FieldDescription(description = "终端客户名称", operate = "display")
    private String zdCustomerName;
    private String zdBackWareHouseID;
    private String ycFLocation;
    private String zdTKType;
    private String zdTKAuditTime;
    private String zdTKAuditStaffName;
    private String zdTKProduceTime;
    private String zdTKProducerName;
    private String outInType;
    private String outInWethod;
    @FieldDescription(description = "供应商编号")
    private String supplierID;
    @FieldDescription(description = "供应商名称", operate = "display")
    private String supplierName;
    @FieldDescription(description = "部品编号", operate = "display")
    private String partID;
    @FieldDescription(description = "部品名称", operate = "display")
    private String partName;
    @FieldDescription(description = "部品件号")
    private String partCode;
    private String inboundBatch;
    private int zdTKAmount;
    private String wareHouseManagerName;
    private String zdBackWareHouseRemark;

    public String getZdCustomerID() {
        return zdCustomerID;
    }

    public void setZdCustomerID(String zdCustomerID) {
        this.zdCustomerID = zdCustomerID;
    }

    public String getZdBackWareHouseID() {
        return zdBackWareHouseID;
    }

    public void setZdBackWareHouseID(String zdBackWareHouseID) {
        this.zdBackWareHouseID = zdBackWareHouseID;
    }

    public String getYcFLocation() {
        return ycFLocation;
    }

    public void setYcFLocation(String ycFLocation) {
        this.ycFLocation = ycFLocation;
    }

    public String getZdTKType() {
        return zdTKType;
    }

    public void setZdTKType(String zdTKType) {
        this.zdTKType = zdTKType;
    }

    public String getZdTKAuditTime() {
        return zdTKAuditTime;
    }

    public void setZdTKAuditTime(String zdTKAuditTime) {
        this.zdTKAuditTime = zdTKAuditTime;
    }

    public String getZdTKAuditStaffName() {
        return zdTKAuditStaffName;
    }

    public void setZdTKAuditStaffName(String zdTKAuditStaffName) {
        this.zdTKAuditStaffName = zdTKAuditStaffName;
    }

    public String getZdTKProduceTime() {
        return zdTKProduceTime;
    }

    public void setZdTKProduceTime(String zdTKProduceTime) {
        this.zdTKProduceTime = zdTKProduceTime;
    }

    public String getZdTKProducerName() {
        return zdTKProducerName;
    }

    public void setZdTKProducerName(String zdTKProducerName) {
        this.zdTKProducerName = zdTKProducerName;
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

    public int getZdTKAmount() {
        return zdTKAmount;
    }

    public void setZdTKAmount(int zdTKAmount) {
        this.zdTKAmount = zdTKAmount;
    }

    public String getWareHouseManagerName() {
        return wareHouseManagerName;
    }

    public void setWareHouseManagerName(String wareHouseManagerName) {
        this.wareHouseManagerName = wareHouseManagerName;
    }

    public String getZdBackWareHouseRemark() {
        return zdBackWareHouseRemark;
    }

    public void setZdBackWareHouseRemark(String zdBackWareHouseRemark) {
        this.zdBackWareHouseRemark = zdBackWareHouseRemark;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
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

    public String getZdCustomerName() {
        return zdCustomerName;
    }

    public void setZdCustomerName(String zdCustomerName) {
        this.zdCustomerName = zdCustomerName;
    }
    
}
