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
    @FieldDescription(description = "失败原因")
    private String failedReason;
    @FieldDescription(description = "供应商代码")
    private String supplierID;
    @FieldDescription(description = "供应商名称", operate = "display")
    private String supplierName;
//    @FieldDescription(description = "部品编号")
    private String partID;
//    @FieldDescription(description = "部品名称")
    private String partName;
    @FieldDescription(description = "部品件号", operate = "import")
    private String partCode;
//    @FieldDescription(description = "部品单位")
    private String partUnit;
    @FieldDescription(description = "部品状态")
    private String partState;
//    @FieldDescription(description = "车型")
    private String autoStylingName;
    @FieldDescription(description = "入库数量", operate = "import")
    private int inboundAmount;
    @FieldDescription(description = "入库箱数")
    private int inboundBoxAmount;
//    @FieldDescription(description = "入库包装数量")
    private int inboundPackageAmount;
    @FieldDescription(description = "入库批次")
    private String inboundBatch;
    @FieldDescription(description = "原厂批次", operate = "import")
    private String originalBatch;
    @FieldDescription(description = "备注", operate = "import")
    private String djInWareHouseListRemark;
    @FieldDescription(description = "待检入库单号")
    private String djInWareHouseID;

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

    public String getAutoStylingName() {
        return autoStylingName;
    }

    public void setAutoStylingName(String autoStylingName) {
        this.autoStylingName = autoStylingName;
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

    public int getInboundPackageAmount() {
        return inboundPackageAmount;
    }

    public void setInboundPackageAmount(int inboundPackageAmount) {
        this.inboundPackageAmount = inboundPackageAmount;
    }

    public String getFailedReason() {
        return failedReason;
    }

    public void setFailedReason(String failedReason) {
        this.failedReason = failedReason;
    }
}
