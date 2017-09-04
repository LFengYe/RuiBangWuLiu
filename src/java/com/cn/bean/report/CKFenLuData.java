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
public class CKFenLuData {
    @FieldDescription(description = "供应商编号")
    private String supplierID;
    @FieldDescription(description = "供应商名称", operate = "display")
    private String supplierName;
    @FieldDescription(description = "部品件号")
    private String partCode;
    @FieldDescription(description = "部品编号", operate = "display")
    private String partID;
    @FieldDescription(description = "部品名称", operate = "display")
    private String partName;
    @FieldDescription(description = "一工厂计划出库")
    private int jhck1;
    @FieldDescription(description = "一工厂临时调货")
    private int lsdh1;
    @FieldDescription(description = "一工厂集配上线")
    private int jpsx1;
    @FieldDescription(description = "二工厂计划出库")
    private int jhck2;
    @FieldDescription(description = "二工厂临时调货")
    private int lsdh2;
    @FieldDescription(description = "二工厂集配上线")
    private int jpsx2;
    @FieldDescription(description = "三工厂计划出库")
    private int jhck3;
    @FieldDescription(description = "三工厂临时调货")
    private int lsdh3;
    @FieldDescription(description = "三工厂集配上线")
    private int jpsx3;
    @FieldDescription(description = "非计划出库")
    private int fjhck;
    @FieldDescription(description = "良品返修出库")
    private int lpfxck;
    @FieldDescription(description = "不良品返修出库")
    private int blpfxck;
    @FieldDescription(description = "出库总数")
    private int ckTotal;

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

    public int getJhck1() {
        return jhck1;
    }

    public void setJhck1(int jhck1) {
        this.jhck1 = jhck1;
    }

    public int getLsdh1() {
        return lsdh1;
    }

    public void setLsdh1(int lsdh1) {
        this.lsdh1 = lsdh1;
    }

    public int getJpsx1() {
        return jpsx1;
    }

    public void setJpsx1(int jpsx1) {
        this.jpsx1 = jpsx1;
    }

    public int getJhck2() {
        return jhck2;
    }

    public void setJhck2(int jhck2) {
        this.jhck2 = jhck2;
    }

    public int getLsdh2() {
        return lsdh2;
    }

    public void setLsdh2(int lsdh2) {
        this.lsdh2 = lsdh2;
    }

    public int getJpsx2() {
        return jpsx2;
    }

    public void setJpsx2(int jpsx2) {
        this.jpsx2 = jpsx2;
    }

    public int getJhck3() {
        return jhck3;
    }

    public void setJhck3(int jhck3) {
        this.jhck3 = jhck3;
    }

    public int getLsdh3() {
        return lsdh3;
    }

    public void setLsdh3(int lsdh3) {
        this.lsdh3 = lsdh3;
    }

    public int getJpsx3() {
        return jpsx3;
    }

    public void setJpsx3(int jpsx3) {
        this.jpsx3 = jpsx3;
    }

    public int getFjhck() {
        return fjhck;
    }

    public void setFjhck(int fjhck) {
        this.fjhck = fjhck;
    }

    public int getLpfxck() {
        return lpfxck;
    }

    public void setLpfxck(int lpfxck) {
        this.lpfxck = lpfxck;
    }

    public int getBlpfxck() {
        return blpfxck;
    }

    public void setBlpfxck(int blpfxck) {
        this.blpfxck = blpfxck;
    }

    public int getCkTotal() {
        return ckTotal;
    }

    public void setCkTotal(int ckTotal) {
        this.ckTotal = ckTotal;
    }
}
