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
public class XCAdjustAccountList {

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
    @FieldDescription(description = "部品件号")
    private String partCode;
    @FieldDescription(description = "终端客户ID")
    private String zdCustomerID;
    @FieldDescription(description = "部品存放区域")
    private String cfArea;
    @FieldDescription(description = "账面结存")
    private int accountBalance;
    @FieldDescription(description = "实物结存")
    private int realBalance;
    @FieldDescription(description = "帐实差异")
    private int zsCY;
    @FieldDescription(description = "审核人")
    private String xcTZAuditStaffName;
    @FieldDescription(description = "备注")
    private String xcTZListRemark;

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

    public String getZdCustomerID() {
        return zdCustomerID;
    }

    public void setZdCustomerID(String zdCustomerID) {
        this.zdCustomerID = zdCustomerID;
    }

    public String getCfArea() {
        return cfArea;
    }

    public void setCfArea(String cfArea) {
        this.cfArea = cfArea;
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

    public String getXcTZAuditStaffName() {
        return xcTZAuditStaffName;
    }

    public void setXcTZAuditStaffName(String xcTZAuditStaffName) {
        this.xcTZAuditStaffName = xcTZAuditStaffName;
    }

    public String getXcTZListRemark() {
        return xcTZListRemark;
    }

    public void setXcTZListRemark(String xcTZListRemark) {
        this.xcTZListRemark = xcTZListRemark;
    }
}
