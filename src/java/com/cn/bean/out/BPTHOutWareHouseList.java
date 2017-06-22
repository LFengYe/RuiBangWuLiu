/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean.out;

import com.cn.bean.FieldDescription;

/**
 *
 * @author LFeng
 */
public class BPTHOutWareHouseList {

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
    @FieldDescription(description = "退货出库数量")
    private int thCKAmount;
    @FieldDescription(description = "最大退货数量", operate = "display")
    private int thAmount;
    @FieldDescription(description = "入库批次")
    private String inboundBatch;
    @FieldDescription(description = "备注")
    private String bpTHOutWareHouseListRemark;
    @FieldDescription(description = "部品状态")
    private String thPartState;
    @FieldDescription(description = "出库单据号")
    private String bpTHOutWareHoseID;
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

    public int getThCKAmount() {
        return thCKAmount;
    }

    public void setThCKAmount(int thCKAmount) {
        this.thCKAmount = thCKAmount;
    }

    public String getInboundBatch() {
        return inboundBatch;
    }

    public void setInboundBatch(String inboundBatch) {
        this.inboundBatch = inboundBatch;
    }

    public String getBpTHOutWareHouseListRemark() {
        return bpTHOutWareHouseListRemark;
    }

    public void setBpTHOutWareHouseListRemark(String bpTHOutWareHouseListRemark) {
        this.bpTHOutWareHouseListRemark = bpTHOutWareHouseListRemark;
    }

    public String getThPartState() {
        return thPartState;
    }

    public void setThPartState(String thPartState) {
        this.thPartState = thPartState;
    }

    public String getBpTHOutWareHoseID() {
        return bpTHOutWareHoseID;
    }

    public void setBpTHOutWareHoseID(String bpTHOutWareHoseID) {
        this.bpTHOutWareHoseID = bpTHOutWareHoseID;
    }

    public String getWareHouseManagerName() {
        return wareHouseManagerName;
    }

    public void setWareHouseManagerName(String wareHouseManagerName) {
        this.wareHouseManagerName = wareHouseManagerName;
    }

    public int getThAmount() {
        return thAmount;
    }

    public void setThAmount(int thAmount) {
        this.thAmount = thAmount;
    }
}
