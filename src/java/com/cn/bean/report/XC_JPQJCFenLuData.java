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
public class XC_JPQJCFenLuData {

    @FieldDescription(description = "供应商编号")
    private String supplierID;
    @FieldDescription(description = "供应商名称", operate = "display")
    private String supplierName;
    @FieldDescription(description = "部品编号", operate = "display")
    private String partID;
    @FieldDescription(description = "部品名称", operate = "display")
    private String partName;
    @FieldDescription(description = "部品件号")
    private String partCode;
    private int jpJC1;
    private int jpJC2;
    private int jpJC3;
    private int xc_JPQJCTotal;

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

    public int getJpJC1() {
        return jpJC1;
    }

    public void setJpJC1(int jpJC1) {
        this.jpJC1 = jpJC1;
    }

    public int getJpJC2() {
        return jpJC2;
    }

    public void setJpJC2(int jpJC2) {
        this.jpJC2 = jpJC2;
    }

    public int getJpJC3() {
        return jpJC3;
    }

    public void setJpJC3(int jpJC3) {
        this.jpJC3 = jpJC3;
    }

    public int getXc_JPQJCTotal() {
        return xc_JPQJCTotal;
    }

    public void setXc_JPQJCTotal(int xc_JPQJCTotal) {
        this.xc_JPQJCTotal = xc_JPQJCTotal;
    }
    
}
