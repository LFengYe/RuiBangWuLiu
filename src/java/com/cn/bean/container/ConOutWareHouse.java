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
public class ConOutWareHouse {
    
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    
    @FieldDescription(description = "出库单号")
    private String conOutWareHouseID;
    @FieldDescription(description = "供应商代码")
    private String supplierID;
    @FieldDescription(description = "供应商名称", operate = "display")
    private String supplierName;
    @FieldDescription(description = "盛具状态")
    private String containerStatus;
    @FieldDescription(description = "制单人员姓名")
    private String conCKProducerName;
    @FieldDescription(description = "制单时间")
    private String conCKProduceTime;
    @FieldDescription(description = "审核人员姓名")
    private String conCKAuditStaffName;
    @FieldDescription(description = "审核时间")
    private String conCKAuditTime;
    @FieldDescription(description = "备注")
    private String conOutWareHousRemark;

    public String getConOutWareHouseID() {
        return conOutWareHouseID;
    }

    public void setConOutWareHouseID(String conOutWareHouseID) {
        this.conOutWareHouseID = conOutWareHouseID;
    }

    public String getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(String supplierID) {
        this.supplierID = supplierID;
    }

    public String getConCKProducerName() {
        return conCKProducerName;
    }

    public void setConCKProducerName(String conCKProducerName) {
        this.conCKProducerName = conCKProducerName;
    }

    public String getConCKProduceTime() {
        return conCKProduceTime;
    }

    public void setConCKProduceTime(String conCKProduceTime) {
        this.conCKProduceTime = conCKProduceTime;
    }

    public String getConCKAuditStaffName() {
        return conCKAuditStaffName;
    }

    public void setConCKAuditStaffName(String conCKAuditStaffName) {
        this.conCKAuditStaffName = conCKAuditStaffName;
    }

    public String getConCKAuditTime() {
        return conCKAuditTime;
    }

    public void setConCKAuditTime(String conCKAuditTime) {
        this.conCKAuditTime = conCKAuditTime;
    }

    public String getConOutWareHousRemark() {
        return conOutWareHousRemark;
    }

    public void setConOutWareHousRemark(String conOutWareHousRemark) {
        this.conOutWareHousRemark = conOutWareHousRemark;
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
