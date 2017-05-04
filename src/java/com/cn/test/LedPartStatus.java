/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.test;

import com.cn.bean.FieldDescription;

/**
 *
 * @author LFeng
 */
public class LedPartStatus {
    @FieldDescription(description = "状态")
    private String partStatus;
    @FieldDescription(description = "供应商ID")
    private String supplierID;
    @FieldDescription(description = "供应商姓名")
    private String supplierName;
    @FieldDescription(description = "部品名称")
    private String partName;
    @FieldDescription(description = "部品代码")
    private String partCode;
    @FieldDescription(description = "入库批次")
    private String inboundBatch;
    @FieldDescription(description = "禁用原因")
    private String rejectReason;

    public String getPartStatus() {
        return partStatus;
    }

    public void setPartStatus(String partStatus) {
        this.partStatus = partStatus;
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

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }
}
