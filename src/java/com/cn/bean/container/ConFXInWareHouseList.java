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
public class ConFXInWareHouseList {

    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    @FieldDescription(description = "供应商代码")
    private String supplierID;
    @FieldDescription(description = "供应商名称", operate = "display")
    private String supplierName;
    @FieldDescription(description = "盛具名称")
    private String containerName;
    @FieldDescription(description = "盛具编号")
    private String containerCode;
    @FieldDescription(description = "返修出批次")
    private String fxOutBatch;
    @FieldDescription(description = "返修出库数量")
    private int fxRKAmount;
    @FieldDescription(description = "返修出库最大数量", operate = "display")
    private int operateMaxAmount;
    @FieldDescription(description = "返修入库单号")
    private String conFXInWareHouseID;
    @FieldDescription(description = "备注")
    private String conFXInWareHouseListRemark;
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

    public int getFxRKAmount() {
        return fxRKAmount;
    }

    public void setFxRKAmount(int fxRKAmount) {
        this.fxRKAmount = fxRKAmount;
    }

    public String getConFXInWareHouseID() {
        return conFXInWareHouseID;
    }

    public void setConFXInWareHouseID(String conFXInWareHouseID) {
        this.conFXInWareHouseID = conFXInWareHouseID;
    }

    public String getConFXInWareHouseListRemark() {
        return conFXInWareHouseListRemark;
    }

    public void setConFXInWareHouseListRemark(String conFXInWareHouseListRemark) {
        this.conFXInWareHouseListRemark = conFXInWareHouseListRemark;
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

    public int getOperateMaxAmount() {
        return operateMaxAmount;
    }

    public void setOperateMaxAmount(int operateMaxAmount) {
        this.operateMaxAmount = operateMaxAmount;
    }

    public String getFxOutBatch() {
        return fxOutBatch;
    }

    public void setFxOutBatch(String fxOutBatch) {
        this.fxOutBatch = fxOutBatch;
    }

}
