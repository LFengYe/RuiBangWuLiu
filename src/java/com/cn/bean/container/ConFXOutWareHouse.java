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
public class ConFXOutWareHouse {
    
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    
    @FieldDescription(description = "返修出库单号")
    private String conFXOutWareHouseID;
    @FieldDescription(description = "供应商代码")
    private String supplierID;
    @FieldDescription(description = "供应商名称", operate = "display")
    private String supplierName;
    @FieldDescription(description = "盛具状态")
    private String containerStatus;
    @FieldDescription(description = "制单人员姓名")
    private String conFXCKProducerName;
    @FieldDescription(description = "制单时间")
    private String conFXCKProduceTime;
    @FieldDescription(description = "审核人员姓名")
    private String conFXCKAuditStaffName;
    @FieldDescription(description = "审核时间")
    private String conFXCKAuditTime;
    @FieldDescription(description = "备注")
    private String conFXOutWareHousRemark;

    public String getConFXOutWareHouseID() {
        return conFXOutWareHouseID;
    }

    public void setConFXOutWareHouseID(String conFXOutWareHouseID) {
        this.conFXOutWareHouseID = conFXOutWareHouseID;
    }

    public String getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(String supplierID) {
        this.supplierID = supplierID;
    }

    public String getConFXCKProducerName() {
        return conFXCKProducerName;
    }

    public void setConFXCKProducerName(String conFXCKProducerName) {
        this.conFXCKProducerName = conFXCKProducerName;
    }

    public String getConFXCKProduceTime() {
        return conFXCKProduceTime;
    }

    public void setConFXCKProduceTime(String conFXCKProduceTime) {
        this.conFXCKProduceTime = conFXCKProduceTime;
    }

    public String getConFXCKAuditStaffName() {
        return conFXCKAuditStaffName;
    }

    public void setConFXCKAuditStaffName(String conFXCKAuditStaffName) {
        this.conFXCKAuditStaffName = conFXCKAuditStaffName;
    }

    public String getConFXCKAuditTime() {
        return conFXCKAuditTime;
    }

    public void setConFXCKAuditTime(String conFXCKAuditTime) {
        this.conFXCKAuditTime = conFXCKAuditTime;
    }

    public String getConFXOutWareHousRemark() {
        return conFXOutWareHousRemark;
    }

    public void setConFXOutWareHousRemark(String conFXOutWareHousRemark) {
        this.conFXOutWareHousRemark = conFXOutWareHousRemark;
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
