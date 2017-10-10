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
public class ContainerRepair {
    @FieldDescription(description = "供应商代码")
    private String supplierID;
    @FieldDescription(description = "供应商名称", operate = "display")
    private String supplierName;
    @FieldDescription(description = "盛具状态")
    private String containerStatus;
    private String containerName;
    private String fxOutBatch;
    @FieldDescription(description = "返修时间")
    private String conFXCKAuditTime;
    private String fxCKAmount;
    private String fxRKAmount;
    private String expiredTime;
    private String conFXOutWareHouseListRemark;

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

    public String getContainerStatus() {
        return containerStatus;
    }

    public void setContainerStatus(String containerStatus) {
        this.containerStatus = containerStatus;
    }

    public String getFxOutBatch() {
        return fxOutBatch;
    }

    public void setFxOutBatch(String fxOutBatch) {
        this.fxOutBatch = fxOutBatch;
    }

    public String getConFXCKAuditTime() {
        return conFXCKAuditTime;
    }

    public void setConFXCKAuditTime(String conFXCKAuditTime) {
        this.conFXCKAuditTime = conFXCKAuditTime;
    }

    public String getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(String expiredTime) {
        this.expiredTime = expiredTime;
    }

    public String getFxCKAmount() {
        return fxCKAmount;
    }

    public void setFxCKAmount(String fxCKAmount) {
        this.fxCKAmount = fxCKAmount;
    }

    public String getFxRKAmount() {
        return fxRKAmount;
    }

    public void setFxRKAmount(String fxRKAmount) {
        this.fxRKAmount = fxRKAmount;
    }

    public String getConFXOutWareHouseListRemark() {
        return conFXOutWareHouseListRemark;
    }

    public void setConFXOutWareHouseListRemark(String conFXOutWareHouseListRemark) {
        this.conFXOutWareHouseListRemark = conFXOutWareHouseListRemark;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }
    
}
