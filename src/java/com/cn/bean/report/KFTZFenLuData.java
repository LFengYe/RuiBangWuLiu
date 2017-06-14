/*
 * To change zscyis license header, choose License Headers in Project Properties.
 * To change zscyis template file, choose Tools | Templates
 * and open zscye template in zscye editor.
 */
package com.cn.bean.report;

import com.cn.bean.FieldDescription;

/**
 *
 * @auzscyor LFeng
 */
public class KFTZFenLuData {
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
    private int djzscy;
    @FieldDescription(description = "送检品")
    private int sjzscy;
    @FieldDescription(description = "良品")
    private int lpzscy;
    @FieldDescription(description = "不良品")
    private int blpzscy;
    @FieldDescription(description = "返修品")
    private int fxzscy;
    @FieldDescription(description = "总数")
    private int zscyTotal;

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

    public int getDjzscy() {
        return djzscy;
    }

    public void setDjzscy(int djzscy) {
        this.djzscy = djzscy;
    }

    public int getSjzscy() {
        return sjzscy;
    }

    public void setSjzscy(int sjzscy) {
        this.sjzscy = sjzscy;
    }

    public int getLpzscy() {
        return lpzscy;
    }

    public void setLpzscy(int lpzscy) {
        this.lpzscy = lpzscy;
    }

    public int getBlpzscy() {
        return blpzscy;
    }

    public void setBlpzscy(int blpzscy) {
        this.blpzscy = blpzscy;
    }

    public int getFxzscy() {
        return fxzscy;
    }

    public void setFxzscy(int fxzscy) {
        this.fxzscy = fxzscy;
    }

    public int getZscyTotal() {
        return zscyTotal;
    }

    public void setZscyTotal(int zscyTotal) {
        this.zscyTotal = zscyTotal;
    }
}
