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
public class THFenLuData {
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
    @FieldDescription(description = "待检退货")
    private int djth;
    @FieldDescription(description = "良品退货")
    private int lpth;
    @FieldDescription(description = "不良品退货")
    private int blpth;
    @FieldDescription(description = "返修退货")
    private int fxth;
    @FieldDescription(description = "退货总数")
    private int thTotal;

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

    public int getDjth() {
        return djth;
    }

    public void setDjth(int djth) {
        this.djth = djth;
    }

    public int getLpth() {
        return lpth;
    }

    public void setLpth(int lpth) {
        this.lpth = lpth;
    }

    public int getBlpth() {
        return blpth;
    }

    public void setBlpth(int blpth) {
        this.blpth = blpth;
    }

    public int getFxth() {
        return fxth;
    }

    public void setFxth(int fxth) {
        this.fxth = fxth;
    }

    public int getThTotal() {
        return thTotal;
    }

    public void setThTotal(int thTotal) {
        this.thTotal = thTotal;
    }
}
