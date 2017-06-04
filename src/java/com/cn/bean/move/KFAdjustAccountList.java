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
public class KFAdjustAccountList {

    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }

    @FieldDescription(description = "调帐编号")
    private String tzYMonth;
    @FieldDescription(description = "供应商代码")
    private String supplierID;
    @FieldDescription(description = "供应商名称", operate = "display")
    private String supplierName;
    @FieldDescription(description = "部品件号")
    private String partCode;
    @FieldDescription(description = "部品编号", operate = "display")
    private String partID;
    @FieldDescription(description = "部品名称", operate = "display")
    private String partName;
    @FieldDescription(description = "部品批次")
    private String inboundBatch;
    @FieldDescription(description = "部品状态")
    private String partState;
    @FieldDescription(description = "账面结存")
    private int accountBalance;
    @FieldDescription(description = "实物结存")
    private int realBalance;
    @FieldDescription(description = "帐实差异")
    private int zsCY;
    @FieldDescription(description = "审核人")
    private String kfTZAuditStaffName;
    @FieldDescription(description = "备注")
    private String kfTZListRemark;

    public String getTzYMonth() {
        return tzYMonth;
    }

    public void setTzYMonth(String tzYMonth) {
        this.tzYMonth = tzYMonth;
    }

    public String getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(String supplierID) {
        this.supplierID = supplierID;
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

    public String getPartState() {
        return partState;
    }

    public void setPartState(String partState) {
        this.partState = partState;
    }

    public int getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(int accountBalance) {
        this.accountBalance = accountBalance;
    }

    public int getRealBalance() {
        return realBalance;
    }

    public void setRealBalance(int realBalance) {
        this.realBalance = realBalance;
    }

    public int getZsCY() {
        return zsCY;
    }

    public void setZsCY(int zsCY) {
        this.zsCY = zsCY;
    }

    public String getKfTZAuditStaffName() {
        return kfTZAuditStaffName;
    }

    public void setKfTZAuditStaffName(String kfTZAuditStaffName) {
        this.kfTZAuditStaffName = kfTZAuditStaffName;
    }

    public String getKfTZListRemark() {
        return kfTZListRemark;
    }

    public void setKfTZListRemark(String kfTZListRemark) {
        this.kfTZListRemark = kfTZListRemark;
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
}
