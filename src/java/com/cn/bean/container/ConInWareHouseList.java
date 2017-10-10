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
public class ConInWareHouseList {

    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    
    @FieldDescription(description = "失败原因")
    private String failedReason;
    @FieldDescription(description = "供应商代码")
    private String supplierID;
    @FieldDescription(description = "供应商名称", operate = "display")
    private String supplierName;
    @FieldDescription(description = "盛具名称", operate = "import")
    private String containerName;
    @FieldDescription(description = "盛具编号")
    private String containerCode;
    @FieldDescription(description = "入库数量", operate = "import")
    private int rkAmount;
    @FieldDescription(description = "入库单号")
    private String conInWareHouseID;
    @FieldDescription(description = "备注", operate = "import")
    private String conInWareHouseListRemark;
    @FieldDescription(description = "管理员")
    private String wareHouseManagerName;

    public String getWareHouseManagerName() {
        return wareHouseManagerName;
    }

    public void setWareHouseManagerName(String wareHouseManagerName) {
        this.wareHouseManagerName = wareHouseManagerName;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public String getContainerCode() {
        return containerCode;
    }

    public void setContainerCode(String containerCode) {
        this.containerCode = containerCode;
    }

    public int getRkAmount() {
        return rkAmount;
    }

    public void setRkAmount(int rkAmount) {
        this.rkAmount = rkAmount;
    }

    public String getConInWareHouseID() {
        return conInWareHouseID;
    }

    public void setConInWareHouseID(String conInWareHouseID) {
        this.conInWareHouseID = conInWareHouseID;
    }

    public String getConInWareHouseListRemark() {
        return conInWareHouseListRemark;
    }

    public void setConInWareHouseListRemark(String conInWareHouseListRemark) {
        this.conInWareHouseListRemark = conInWareHouseListRemark;
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

    public String getFailedReason() {
        return failedReason;
    }

    public void setFailedReason(String failedReason) {
        this.failedReason = failedReason;
    }
    
}
