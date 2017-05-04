/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean.move;

import com.cn.bean.FieldDescription;

/**
 *
 * @author LFeng
 */
public class FXInWareHouseList {

    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    @FieldDescription(description = "单内显示序号")
    private int listNumber;
    @FieldDescription(description = "供应商代码")
    private String supplierID;
    @FieldDescription(description = "供应商名称")
    private String supplierName;
    @FieldDescription(description = "部品编号")
    private String partID;
    @FieldDescription(description = "部品名称")
    private String partName;
    @FieldDescription(description = "部品件号")
    private String partCode;
    @FieldDescription(description = "入库部品原入库批次")
    private String inboundBatch;
    @FieldDescription(description = "部品单位 ")
    private String partUnit;
    @FieldDescription(description = "返修入库数量")
    private int fxRKAmount;
    @FieldDescription(description = "部品状态")
    private String partState;
    @FieldDescription(description = "备注")
    private String fxInWareHouseListRemark;
    @FieldDescription(description = "方便付老师处理用,不参与报表统计")
    private String newInboundBatch;
    @FieldDescription(description = "返修入库单号")
    private String fxInWareHouseID;
    @FieldDescription(description = "库管员姓名")
    private String wareHouseManagerName;

    public int getListNumber() {
        return listNumber;
    }

    public void setListNumber(int listNumber) {
        this.listNumber = listNumber;
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

    public String getInboundBatch() {
        return inboundBatch;
    }

    public void setInboundBatch(String inboundBatch) {
        this.inboundBatch = inboundBatch;
    }

    public String getPartUnit() {
        return partUnit;
    }

    public void setPartUnit(String partUnit) {
        this.partUnit = partUnit;
    }

    public int getFxRKAmount() {
        return fxRKAmount;
    }

    public void setFxRKAmount(int fxRKAmount) {
        this.fxRKAmount = fxRKAmount;
    }

    public String getPartState() {
        return partState;
    }

    public void setPartState(String partState) {
        this.partState = partState;
    }

    public String getFxInWareHouseListRemark() {
        return fxInWareHouseListRemark;
    }

    public void setFxInWareHouseListRemark(String fxInWareHouseListRemark) {
        this.fxInWareHouseListRemark = fxInWareHouseListRemark;
    }

    public String getNewInboundBatch() {
        return newInboundBatch;
    }

    public void setNewInboundBatch(String newInboundBatch) {
        this.newInboundBatch = newInboundBatch;
    }

    public String getFxInWareHouseID() {
        return fxInWareHouseID;
    }

    public void setFxInWareHouseID(String fxInWareHouseID) {
        this.fxInWareHouseID = fxInWareHouseID;
    }

    public String getWareHouseManagerName() {
        return wareHouseManagerName;
    }

    public void setWareHouseManagerName(String wareHouseManagerName) {
        this.wareHouseManagerName = wareHouseManagerName;
    }
}
