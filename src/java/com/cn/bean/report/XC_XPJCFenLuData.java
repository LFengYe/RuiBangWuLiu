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
public class XC_XPJCFenLuData {

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
    private int xpJC1;
    private int xpJC2;
    private int xpJC3;
    private int zdJS1;
    private int zdJS2;
    private int zdJS3;
    private int xc_ZDJSTotal;
    private int xc_XPJCTotal;

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

    public int getXpJC1() {
        return xpJC1;
    }

    public void setXpJC1(int xpJC1) {
        this.xpJC1 = xpJC1;
    }

    public int getXpJC2() {
        return xpJC2;
    }

    public void setXpJC2(int xpJC2) {
        this.xpJC2 = xpJC2;
    }

    public int getXpJC3() {
        return xpJC3;
    }

    public void setXpJC3(int xpJC3) {
        this.xpJC3 = xpJC3;
    }

    public int getZdJS1() {
        return zdJS1;
    }

    public void setZdJS1(int zdJS1) {
        this.zdJS1 = zdJS1;
    }

    public int getZdJS2() {
        return zdJS2;
    }

    public void setZdJS2(int zdJS2) {
        this.zdJS2 = zdJS2;
    }

    public int getZdJS3() {
        return zdJS3;
    }

    public void setZdJS3(int zdJS3) {
        this.zdJS3 = zdJS3;
    }

    public int getXc_ZDJSTotal() {
        return xc_ZDJSTotal;
    }

    public void setXc_ZDJSTotal(int xc_ZDJSTotal) {
        this.xc_ZDJSTotal = xc_ZDJSTotal;
    }

    public int getXc_XPJCTotal() {
        return xc_XPJCTotal;
    }

    public void setXc_XPJCTotal(int xc_XPJCTotal) {
        this.xc_XPJCTotal = xc_XPJCTotal;
    }
    
}
