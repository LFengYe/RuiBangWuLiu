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
public class JHOutWareHouseList {
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
    @FieldDescription(description = "入库批次")
    private String inboundBatch;
    @FieldDescription(description = "部品单位")
    private String partUnit;
    @FieldDescription(description = "车型")
    private String autoStyling;
    @FieldDescription(description = "计划出库数量")
    private int jhCKAmount;
    @FieldDescription(description = "出库盛具")
    private String outboundContainer;
    @FieldDescription(description = "盛具数量")
    private int containerAmount;
    @FieldDescription(description = "存放地址")
    private String cfAddress;
    @FieldDescription(description = "备注")
    private String jhOutWareHouseListRemark;
    @FieldDescription(description = "计划出库单号")
    private String jhOutWareHouseID;

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

    public String getAutoStyling() {
        return autoStyling;
    }

    public void setAutoStyling(String autoStyling) {
        this.autoStyling = autoStyling;
    }

    public int getJhCKAmount() {
        return jhCKAmount;
    }

    public void setJhCKAmount(int jhCKAmount) {
        this.jhCKAmount = jhCKAmount;
    }

    public String getOutboundContainer() {
        return outboundContainer;
    }

    public void setOutboundContainer(String outboundContainer) {
        this.outboundContainer = outboundContainer;
    }

    public int getContainerAmount() {
        return containerAmount;
    }

    public void setContainerAmount(int containerAmount) {
        this.containerAmount = containerAmount;
    }

    public String getCfAddress() {
        return cfAddress;
    }

    public void setCfAddress(String cfAddress) {
        this.cfAddress = cfAddress;
    }

    public String getJhOutWareHouseListRemark() {
        return jhOutWareHouseListRemark;
    }

    public void setJhOutWareHouseListRemark(String jhOutWareHouseListRemark) {
        this.jhOutWareHouseListRemark = jhOutWareHouseListRemark;
    }

    public String getJhOutWareHouseID() {
        return jhOutWareHouseID;
    }

    public void setJhOutWareHouseID(String jhOutWareHouseID) {
        this.jhOutWareHouseID = jhOutWareHouseID;
    }

    public int getListNumber() {
        return listNumber;
    }

    public void setListNumber(int listNumber) {
        this.listNumber = listNumber;
    }
}
