/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean.base;

import com.cn.bean.FieldDescription;

/**
 *
 * @author LFeng
 */
public class SXProgressList {

    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    @FieldDescription(description = "单内显示序号")
    private int listNumber;
    @FieldDescription(description = "供应商ID")
    private String supplierID;
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
    @FieldDescription(description = "领货数量")
    private int lhAmount;
    @FieldDescription(description = "上线数量")
    private int sxAmount;
    @FieldDescription(description = "上线时间")
    private String sxTime;
    @FieldDescription(description = "上线人员姓名")
    private String sxStaffName;
    @FieldDescription(description = "备注")
    private String sxProgressListRemark;
    @FieldDescription(description = "计划出库单号")
    private String jhOutWareHouseID;

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

    public int getLhAmount() {
        return lhAmount;
    }

    public void setLhAmount(int lhAmount) {
        this.lhAmount = lhAmount;
    }

    public int getSxAmount() {
        return sxAmount;
    }

    public void setSxAmount(int sxAmount) {
        this.sxAmount = sxAmount;
    }

    public String getSxTime() {
        return sxTime;
    }

    public void setSxTime(String sxTime) {
        this.sxTime = sxTime;
    }

    public String getSxStaffName() {
        return sxStaffName;
    }

    public void setSxStaffName(String sxStaffName) {
        this.sxStaffName = sxStaffName;
    }

    public String getSxProgressListRemark() {
        return sxProgressListRemark;
    }

    public void setSxProgressListRemark(String sxProgressListRemark) {
        this.sxProgressListRemark = sxProgressListRemark;
    }

    public String getJhOutWareHouseID() {
        return jhOutWareHouseID;
    }

    public void setJhOutWareHouseID(String jhOutWareHouseID) {
        this.jhOutWareHouseID = jhOutWareHouseID;
    }
}
