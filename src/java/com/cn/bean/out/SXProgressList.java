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
public class SXProgressList {
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
    @FieldDescription(description = "上线时间")
    private String sxTime;
    @FieldDescription(description = "计划出库单号")
    private String jhOutWareHouseID;
    @FieldDescription(description = "备注")
    private String sxProgressListRemark;
    
    @FieldDescription(description = "完成时间", operate = "display")
    private String finishTime;
    @FieldDescription(description = "备注", operate = "display")
    private String progressListRemark;
    @FieldDescription(description = "操作人员", operate = "display")
    private String staffName;
    @FieldDescription(description = "计划状态", operate = "display")
    private int jhStatus;
    @FieldDescription(description = "需求时间", operate = "display")
    private String jhDemandTime;
    @FieldDescription(description = "库房存放地址", operate = "display")
    private String kfCFAddress;

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

    public String getJhOutWareHouseID() {
        return jhOutWareHouseID;
    }

    public void setJhOutWareHouseID(String jhOutWareHouseID) {
        this.jhOutWareHouseID = jhOutWareHouseID;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public String getProgressListRemark() {
        return progressListRemark;
    }

    public void setProgressListRemark(String progressListRemark) {
        this.progressListRemark = progressListRemark;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }
    
    public int getJhStatus() {
        return -1;
    }

    public void setJhStatus(int jhStatus) {
        this.jhStatus = jhStatus;
    }

    public String getSxTime() {
        return sxTime;
    }

    public void setSxTime(String sxTime) {
        this.sxTime = sxTime;
    }

    public String getSxProgressListRemark() {
        return sxProgressListRemark;
    }

    public void setSxProgressListRemark(String sxProgressListRemark) {
        this.sxProgressListRemark = sxProgressListRemark;
    }

    public String getJhDemandTime() {
        return jhDemandTime;
    }

    public void setJhDemandTime(String jhDemandTime) {
        this.jhDemandTime = jhDemandTime;
    }

    public String getKfCFAddress() {
        return kfCFAddress;
    }

    public void setKfCFAddress(String kfCFAddress) {
        this.kfCFAddress = kfCFAddress;
    }
}
