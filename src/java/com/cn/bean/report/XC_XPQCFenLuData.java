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
public class XC_XPQCFenLuData {
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
    private int xp1;
    private int xp2;
    private int xp3;
    private int xc_XPQCTotal;

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

    public int getXp1() {
        return xp1;
    }

    public void setXp1(int xp1) {
        this.xp1 = xp1;
    }

    public int getXp2() {
        return xp2;
    }

    public void setXp2(int xp2) {
        this.xp2 = xp2;
    }

    public int getXp3() {
        return xp3;
    }

    public void setXp3(int xp3) {
        this.xp3 = xp3;
    }

    public int getXc_XPQCTotal() {
        return xc_XPQCTotal;
    }

    public void setXc_XPQCTotal(int xc_XPQCTotal) {
        this.xc_XPQCTotal = xc_XPQCTotal;
    }
    
}
