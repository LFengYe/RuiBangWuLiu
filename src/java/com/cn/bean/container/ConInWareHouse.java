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
public class ConInWareHouse {
    
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    
    @FieldDescription(description = "盛具入库单号")
    private String conInWareHouseID;
    @FieldDescription(description = "供应商代码")
    private String supplierID;
    @FieldDescription(description = "供应商名称", operate = "display")
    private String supplierName;
    @FieldDescription(description = "制单人员姓名")
    private String conRKProducerName;
    @FieldDescription(description = "制单时间")
    private String conRKProduceTime;
    @FieldDescription(description = "审核人员姓名")
    private String conRKAuditStaffName;
    @FieldDescription(description = "审核时间")
    private String conRKAuditTime;
    @FieldDescription(description = "备注")
    private String conInWareHousRemark;

    public String getConInWareHouseID() {
        return conInWareHouseID;
    }

    public void setConInWareHouseID(String conInWareHouseID) {
        this.conInWareHouseID = conInWareHouseID;
    }

    public String getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(String supplierID) {
        this.supplierID = supplierID;
    }

    public String getConRKProducerName() {
        return conRKProducerName;
    }

    public void setConRKProducerName(String conRKProducerName) {
        this.conRKProducerName = conRKProducerName;
    }

    public String getConRKProduceTime() {
        return conRKProduceTime;
    }

    public void setConRKProduceTime(String conRKProduceTime) {
        this.conRKProduceTime = conRKProduceTime;
    }

    public String getConRKAuditStaffName() {
        return conRKAuditStaffName;
    }

    public void setConRKAuditStaffName(String conRKAuditStaffName) {
        this.conRKAuditStaffName = conRKAuditStaffName;
    }

    public String getConRKAuditTime() {
        return conRKAuditTime;
    }

    public void setConRKAuditTime(String conRKAuditTime) {
        this.conRKAuditTime = conRKAuditTime;
    }

    public String getConInWareHousRemark() {
        return conInWareHousRemark;
    }

    public void setConInWareHousRemark(String conInWareHousRemark) {
        this.conInWareHousRemark = conInWareHousRemark;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
    
}
