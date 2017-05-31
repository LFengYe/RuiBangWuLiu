/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean.in;

import com.cn.bean.FieldDescription;

/**
 *
 * @author LFeng
 */
public class ZDBackWareHouseList {

    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    @FieldDescription(description = "供应商ID")
    private String supplierID;
    @FieldDescription(description = "供应商名称", operate = "display")
    private String supplierName;
    @FieldDescription(description = "部品编号", operate = "display")
    private String partID;
    @FieldDescription(description = "部品名称", operate = "display")
    private String partName;
    @FieldDescription(description = "部品件号")
    private String partCode;
    @FieldDescription(description = "部品单位", operate = "display")
    private String partUnit;
    @FieldDescription(description = "退库数量")
    private int zdTKAmount;
//    @FieldDescription(description = "退库最大数量")
    private int tkAmount;
    @FieldDescription(description = "退库部品入库选择批次")
    private String inboundBatch;
    @FieldDescription(description = "终端退库明细备注")
    private String zdBackWareHouseRemark;
    @FieldDescription(description = "退库部品状态")
    private String tkPartState;
    @FieldDescription(description = "终端退库单据号")
    private String zdBackWareHouseID;
    @FieldDescription(description = "库管员")
    private String wareHouseManagerName;

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

    public String getPartCode() {
        return partCode;
    }

    public void setPartCode(String partCode) {
        this.partCode = partCode;
    }

    public String getPartUnit() {
        return partUnit;
    }

    public void setPartUnit(String partUnit) {
        this.partUnit = partUnit;
    }

    public int getZdTKAmount() {
        return zdTKAmount;
    }

    public void setZdTKAmount(int zdTKAmount) {
        this.zdTKAmount = zdTKAmount;
    }

    public String getInboundBatch() {
        return inboundBatch;
    }

    public void setInboundBatch(String inboundBatch) {
        this.inboundBatch = inboundBatch;
    }

    public String getZdBackWareHouseRemark() {
        return zdBackWareHouseRemark;
    }

    public void setZdBackWareHouseRemark(String zdBackWareHouseRemark) {
        this.zdBackWareHouseRemark = zdBackWareHouseRemark;
    }

    public String getTkPartState() {
        return tkPartState;
    }

    public void setTkPartState(String tkPartState) {
        this.tkPartState = tkPartState;
    }

    public String getZdBackWareHouseID() {
        return zdBackWareHouseID;
    }

    public void setZdBackWareHouseID(String zdBackWareHouseID) {
        this.zdBackWareHouseID = zdBackWareHouseID;
    }

    public String getWareHouseManagerName() {
        return wareHouseManagerName;
    }

    public void setWareHouseManagerName(String wareHouseManagerName) {
        this.wareHouseManagerName = wareHouseManagerName;
    }

    public int getTkAmount() {
        return tkAmount;
    }

    public void setTkAmount(int tkAmount) {
        this.tkAmount = tkAmount;
    }
}
