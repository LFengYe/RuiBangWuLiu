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
public class RKFenLuData {

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
    @FieldDescription(description = "供应商部品入库")
    private int gySBPrk;
    @FieldDescription(description = "良品入库")
    private int lprk;
    @FieldDescription(description = "不良品入库")
    private int blprk;
    @FieldDescription(description = "返修良品入库")
    private int fxlprk;
    @FieldDescription(description = "返修不良品入库")
    private int fxblprk;
    @FieldDescription(description = "送检出库")
    private int sjck;
    @FieldDescription(description = "送检退库")
    private int sjtk;

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

    public int getGySBPrk() {
        return gySBPrk;
    }

    public void setGySBPrk(int gySBPrk) {
        this.gySBPrk = gySBPrk;
    }

    public int getLprk() {
        return lprk;
    }

    public void setLprk(int lprk) {
        this.lprk = lprk;
    }

    public int getBlprk() {
        return blprk;
    }

    public void setBlprk(int blprk) {
        this.blprk = blprk;
    }

    public int getFxlprk() {
        return fxlprk;
    }

    public void setFxlprk(int fxlprk) {
        this.fxlprk = fxlprk;
    }

    public int getFxblprk() {
        return fxblprk;
    }

    public void setFxblprk(int fxblprk) {
        this.fxblprk = fxblprk;
    }

    public int getSjck() {
        return sjck;
    }

    public void setSjck(int sjck) {
        this.sjck = sjck;
    }

    public int getSjtk() {
        return sjtk;
    }

    public void setSjtk(int sjtk) {
        this.sjtk = sjtk;
    }
}
