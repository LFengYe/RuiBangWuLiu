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
public class DJInWareHouseList {

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
    @FieldDescription(description = "部品单位")
    private String partUnit;
    @FieldDescription(description = "部品状态")
    private String partState;
    @FieldDescription(description = "车型")
    private String autoStyling;
    @FieldDescription(description = "入库数量")
    private int inboundAmount;
    @FieldDescription(description = "入库箱数")
    private int inboundBoxAmount;
    @FieldDescription(description = "存放地址")
    private String cfAddress;
    @FieldDescription(description = "入库批次")
    private String inboundBatch;
    @FieldDescription(description = "原厂批次")
    private String originalBatch;
    @FieldDescription(description = "备注")
    private String djInWareHouseListRemark;
    @FieldDescription(description = "待检入库单号")
    private String djInWareHouseID;
    @FieldDescription(description = "余额")
    private int jhBalance;

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

    public String getPartUnit() {
        return partUnit;
    }

    public void setPartUnit(String partUnit) {
        this.partUnit = partUnit;
    }

    public String getPartState() {
        return partState;
    }

    public void setPartState(String partState) {
        this.partState = partState;
    }

    public String getAutoStyling() {
        return autoStyling;
    }

    public void setAutoStyling(String autoStyling) {
        this.autoStyling = autoStyling;
    }

    public int getInboundAmount() {
        return inboundAmount;
    }

    public void setInboundAmount(int inboundAmount) {
        this.inboundAmount = inboundAmount;
    }

    public int getInboundBoxAmount() {
        return inboundBoxAmount;
    }

    public void setInboundBoxAmount(int inboundBoxAmount) {
        this.inboundBoxAmount = inboundBoxAmount;
    }

    public String getCfAddress() {
        return cfAddress;
    }

    public void setCfAddress(String cfAddress) {
        this.cfAddress = cfAddress;
    }

    public String getInboundBatch() {
        return inboundBatch;
    }

    public void setInboundBatch(String inboundBatch) {
        this.inboundBatch = inboundBatch;
    }

    public String getOriginalBatch() {
        return originalBatch;
    }

    public void setOriginalBatch(String originalBatch) {
        this.originalBatch = originalBatch;
    }

    public String getDjInWareHouseListRemark() {
        return djInWareHouseListRemark;
    }

    public void setDjInWareHouseListRemark(String djInWareHouseListRemark) {
        this.djInWareHouseListRemark = djInWareHouseListRemark;
    }

    public String getDjInWareHouseID() {
        return djInWareHouseID;
    }

    public void setDjInWareHouseID(String djInWareHouseID) {
        this.djInWareHouseID = djInWareHouseID;
    }

    public int getJhBalance() {
        return jhBalance;
    }

    public void setJhBalance(int jhBalance) {
        this.jhBalance = jhBalance;
    }
}
