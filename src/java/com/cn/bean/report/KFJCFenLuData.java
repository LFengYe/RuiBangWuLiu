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
public class KFJCFenLuData {

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
    private int kfjcdjp;
    @FieldDescription(description = "送检品")
    private int kfjcsjp;
    @FieldDescription(description = "良品")
    private int kfjclp;
    @FieldDescription(description = "不良品")
    private int kfjCblp;
    @FieldDescription(description = "返修品")
    private int kfjcfxp;
    @FieldDescription(description = "总数")
    private int kfJCTotal;

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

    public int getKfjcdjp() {
        return kfjcdjp;
    }

    public void setKfjcdjp(int kfjcdjp) {
        this.kfjcdjp = kfjcdjp;
    }

    public int getKfjcsjp() {
        return kfjcsjp;
    }

    public void setKfjcsjp(int kfjcsjp) {
        this.kfjcsjp = kfjcsjp;
    }

    public int getKfjclp() {
        return kfjclp;
    }

    public void setKfjclp(int kfjclp) {
        this.kfjclp = kfjclp;
    }

    public int getKfjCblp() {
        return kfjCblp;
    }

    public void setKfjCblp(int kfjCblp) {
        this.kfjCblp = kfjCblp;
    }

    public int getKfjcfxp() {
        return kfjcfxp;
    }

    public void setKfjcfxp(int kfjcfxp) {
        this.kfjcfxp = kfjcfxp;
    }

    public int getKfJCTotal() {
        return kfJCTotal;
    }

    public void setKfJCTotal(int kfJCTotal) {
        this.kfJCTotal = kfJCTotal;
    }

}
