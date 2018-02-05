/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean.report;

import com.cn.bean.FieldDescription;

/**
 *
 * @author LFeng
 */
public class CKListForJPSX {

    @FieldDescription(description = "上线时间")
    private String sxTime;
    @FieldDescription(description = "出入类型")
    private String outInType;
    @FieldDescription(description = "出入方式")
    private String outInMethod;
    @FieldDescription(description = "供应商编号")
    private String supplierID;
    @FieldDescription(description = "供应商名称", operate = "display")
    private String supplierName;
    @FieldDescription(description = "部品件号")
    private String partCode;
    @FieldDescription(description = "部品编号", operate = "display")
    private String partID;
    @FieldDescription(description = "部品名称", operate = "display")
    private String partName;
    @FieldDescription(description = "包号")
    private String packingNumber;
    @FieldDescription(description = "上线数量")
    private String sxAmount;
    @FieldDescription(description = "终端客户编号")
    private String zdCustomerID;
    @FieldDescription(description = "终端客户名称", operate = "display")
    private String zdCustomerName;
    @FieldDescription(description = "配送员")
    private String sxStaffName;
    @FieldDescription(description = "备注")
    private String sxProgressListRemark;

    public String getSxTime() {
        return sxTime;
    }

    public void setSxTime(String sxTime) {
        this.sxTime = sxTime;
    }

    public String getOutInType() {
        return outInType;
    }

    public void setOutInType(String outInType) {
        this.outInType = outInType;
    }

    public String getOutInMethod() {
        return outInMethod;
    }

    public void setOutInMethod(String outInMethod) {
        this.outInMethod = outInMethod;
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

    public String getPackingNumber() {
        return packingNumber;
    }

    public void setPackingNumber(String packingNumber) {
        this.packingNumber = packingNumber;
    }

    public String getSxAmount() {
        return sxAmount;
    }

    public void setSxAmount(String sxAmount) {
        this.sxAmount = sxAmount;
    }

    public String getZdCustomerID() {
        return zdCustomerID;
    }

    public void setZdCustomerID(String zdCustomerID) {
        this.zdCustomerID = zdCustomerID;
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

    public String getPartID() {
        return partID;
    }

    public void setPartID(String partID) {
        this.partID = partID;
    }

    public String getZdCustomerName() {
        return zdCustomerName;
    }

    public void setZdCustomerName(String zdCustomerName) {
        this.zdCustomerName = zdCustomerName;
    }
}
