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
public class LHProgressList {
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
    @FieldDescription(description = "部品编号", operate = "display")
    private String partID;
    @FieldDescription(description = "部品名称", operate = "display")
    private String partName;
    @FieldDescription(description = "部品件号")
    private String partCode;
    @FieldDescription(description = "入库批次")
    private String inboundBatch;
    @FieldDescription(description = "包装编号")
    private int packingNumber;
    @FieldDescription(description = "包装数量")
    private int packingAmount;
    @FieldDescription(description = "领货时间")
    private String lhTime;
    @FieldDescription(description = "领货人")
    private String lhStaffName;
    @FieldDescription(description = "计划出库单号")
    private String jhOutWareHouseID;
    @FieldDescription(description = "备注")
    private String lhProgressListRemark;

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

    public int getPackingNumber() {
        return packingNumber;
    }

    public void setPackingNumber(int packingNumber) {
        this.packingNumber = packingNumber;
    }

    public int getPackingAmount() {
        return packingAmount;
    }

    public void setPackingAmount(int packingAmount) {
        this.packingAmount = packingAmount;
    }

    public String getLhTime() {
        return lhTime;
    }

    public void setLhTime(String lhTime) {
        this.lhTime = lhTime;
    }

    public String getLhStaffName() {
        return lhStaffName;
    }

    public void setLhStaffName(String lhStaffName) {
        this.lhStaffName = lhStaffName;
    }

    public String getJhOutWareHouseID() {
        return jhOutWareHouseID;
    }

    public void setJhOutWareHouseID(String jhOutWareHouseID) {
        this.jhOutWareHouseID = jhOutWareHouseID;
    }

    public String getLhProgressListRemark() {
        return lhProgressListRemark;
    }

    public void setLhProgressListRemark(String lhProgressListRemark) {
        this.lhProgressListRemark = lhProgressListRemark;
    }
    
}
