/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean.container;

import com.cn.bean.FieldDescription;

/**
 *
 * @author LFeng
 */
public class ConFXInWareHouse {
    
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    
    @FieldDescription(description = "返修入库单号")
    private String conFXInWareHouseID;
    @FieldDescription(description = "供应商代码")
    private String supplierID;
    @FieldDescription(description = "供应商名称", operate = "display")
    private String supplierName;
    @FieldDescription(description = "盛具状态")
    private String containerStatus;
    @FieldDescription(description = "制单人员姓名")
    private String conFXRKProducerName;
    @FieldDescription(description = "制单时间")
    private String conFXRKProduceTime;
    @FieldDescription(description = "审核人员姓名")
    private String conFXRKAuditStaffName;
    @FieldDescription(description = "审核时间")
    private String conFXRKAuditTime;
    @FieldDescription(description = "备注")
    private String conFXInWareHousRemark;

    public String getConFXInWareHouseID() {
        return conFXInWareHouseID;
    }

    public void setConFXInWareHouseID(String conFXInWareHouseID) {
        this.conFXInWareHouseID = conFXInWareHouseID;
    }

    public String getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(String supplierID) {
        this.supplierID = supplierID;
    }

    public String getConFXRKProducerName() {
        return conFXRKProducerName;
    }

    public void setConFXRKProducerName(String conFXRKProducerName) {
        this.conFXRKProducerName = conFXRKProducerName;
    }

    public String getConFXRKProduceTime() {
        return conFXRKProduceTime;
    }

    public void setConFXRKProduceTime(String conFXRKProduceTime) {
        this.conFXRKProduceTime = conFXRKProduceTime;
    }

    public String getConFXRKAuditStaffName() {
        return conFXRKAuditStaffName;
    }

    public void setConFXRKAuditStaffName(String conFXRKAuditStaffName) {
        this.conFXRKAuditStaffName = conFXRKAuditStaffName;
    }

    public String getConFXRKAuditTime() {
        return conFXRKAuditTime;
    }

    public void setConFXRKAuditTime(String conFXRKAuditTime) {
        this.conFXRKAuditTime = conFXRKAuditTime;
    }

    public String getConFXInWareHousRemark() {
        return conFXInWareHousRemark;
    }

    public void setConFXInWareHousRemark(String conFXInWareHousRemark) {
        this.conFXInWareHousRemark = conFXInWareHousRemark;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getContainerStatus() {
        return containerStatus;
    }

    public void setContainerStatus(String containerStatus) {
        this.containerStatus = containerStatus;
    }
    
}
