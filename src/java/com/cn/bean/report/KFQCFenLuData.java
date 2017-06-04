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
public class KFQCFenLuData {
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
    @FieldDescription(description = "待检品")
    private int djp;
    @FieldDescription(description = "送检品")
    private int sjp;
    @FieldDescription(description = "良品")
    private int lp;
    @FieldDescription(description = "不良品")
    private int blp;
    @FieldDescription(description = "返修品")
    private int fxp;
    @FieldDescription(description = "库房期初总数")
    private int kfQCTotal;

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

    public int getDjp() {
        return djp;
    }

    public void setDjp(int djp) {
        this.djp = djp;
    }

    public int getSjp() {
        return sjp;
    }

    public void setSjp(int sjp) {
        this.sjp = sjp;
    }

    public int getLp() {
        return lp;
    }

    public void setLp(int lp) {
        this.lp = lp;
    }

    public int getBlp() {
        return blp;
    }

    public void setBlp(int blp) {
        this.blp = blp;
    }

    public int getFxp() {
        return fxp;
    }

    public void setFxp(int fxp) {
        this.fxp = fxp;
    }

    public int getKfQCTotal() {
        return kfQCTotal;
    }

    public void setKfQCTotal(int kfQCTotal) {
        this.kfQCTotal = kfQCTotal;
    }
}
