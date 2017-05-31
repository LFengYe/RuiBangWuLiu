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
public class XCJSList {

    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    @FieldDescription(description = "供应商ID")
    private String supplierID;
    @FieldDescription(description = "供应商姓名", operate = "display")
    private String supplierName;
    @FieldDescription(description = "部品编号", operate = "display")
    private String partID;
    @FieldDescription(description = "部品名称", operate = "display")
    private String partName;
    @FieldDescription(description = "部品件号")
    private String partCode;
    @FieldDescription(description = "车型", operate = "display")
    private String autoStylingName;
    @FieldDescription(description = "单位", operate = "display")
    private String partUnit;
    @FieldDescription(description = "结算数量")
    private int xcJSAmount;
    @FieldDescription(description = "现场结算单据号")
    private String xcJSID;
    @FieldDescription(description = "现场结算备注")
    private String xcJSListRemark;

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

    public String getAutoStylingName() {
        return autoStylingName;
    }

    public void setAutoStylingName(String autoStylingName) {
        this.autoStylingName = autoStylingName;
    }

    public String getPartUnit() {
        return partUnit;
    }

    public void setPartUnit(String partUnit) {
        this.partUnit = partUnit;
    }

    public int getXcJSAmount() {
        return xcJSAmount;
    }

    public void setXcJSAmount(int xcJSAmount) {
        this.xcJSAmount = xcJSAmount;
    }

    public String getXcJSID() {
        return xcJSID;
    }

    public void setXcJSID(String xcJSID) {
        this.xcJSID = xcJSID;
    }

    public String getXcJSListRemark() {
        return xcJSListRemark;
    }

    public void setXcJSListRemark(String xcJSListRemark) {
        this.xcJSListRemark = xcJSListRemark;
    }
}
