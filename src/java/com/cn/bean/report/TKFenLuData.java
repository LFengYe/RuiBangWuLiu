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
public class TKFenLuData {

    @FieldDescription(description = "供应商编号")
    private String supplierID;
    @FieldDescription(description = "供应商名称")
    private String supplierName;
    @FieldDescription(description = "部品编号")
    private String partID;
    @FieldDescription(description = "部品名称")
    private String partName;
    @FieldDescription(description = "部品件号")
    private String partCode;
    @FieldDescription(description = "一工厂良品退库")
    private int lpJP1;
    @FieldDescription(description = "二工厂良品退库")
    private int lpJP2;
    @FieldDescription(description = "三工厂良品退库")
    private int lpJP3;
    @FieldDescription(description = "一工厂不良品退库")
    private int blPJP1;
    @FieldDescription(description = "二工厂不良品退库")
    private int blPJP2;
    @FieldDescription(description = "三工厂不良品退库")
    private int blPJP3;
    @FieldDescription(description = "退货总数")
    private int tkTotal;

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

    public int getLpJP1() {
        return lpJP1;
    }

    public void setLpJP1(int lpJP1) {
        this.lpJP1 = lpJP1;
    }

    public int getLpJP2() {
        return lpJP2;
    }

    public void setLpJP2(int lpJP2) {
        this.lpJP2 = lpJP2;
    }

    public int getLpJP3() {
        return lpJP3;
    }

    public void setLpJP3(int lpJP3) {
        this.lpJP3 = lpJP3;
    }

    public int getBlPJP1() {
        return blPJP1;
    }

    public void setBlPJP1(int blPJP1) {
        this.blPJP1 = blPJP1;
    }

    public int getBlPJP2() {
        return blPJP2;
    }

    public void setBlPJP2(int blPJP2) {
        this.blPJP2 = blPJP2;
    }

    public int getBlPJP3() {
        return blPJP3;
    }

    public void setBlPJP3(int blPJP3) {
        this.blPJP3 = blPJP3;
    }

    public int getTkTotal() {
        return tkTotal;
    }

    public void setTkTotal(int tkTotal) {
        this.tkTotal = tkTotal;
    }
}
