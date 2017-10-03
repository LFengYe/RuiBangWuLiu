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
public class XC_JPQQCFenLuData {

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
    private int jp1;
    private int jp2;
    private int jp3;
    private int xc_JPQQCTotal;

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

    public int getJp1() {
        return jp1;
    }

    public void setJp1(int jp1) {
        this.jp1 = jp1;
    }

    public int getJp2() {
        return jp2;
    }

    public void setJp2(int jp2) {
        this.jp2 = jp2;
    }

    public int getJp3() {
        return jp3;
    }

    public void setJp3(int jp3) {
        this.jp3 = jp3;
    }

    public int getXc_JPQQCTotal() {
        return xc_JPQQCTotal;
    }

    public void setXc_JPQQCTotal(int xc_JPQQCTotal) {
        this.xc_JPQQCTotal = xc_JPQQCTotal;
    }
    
}
