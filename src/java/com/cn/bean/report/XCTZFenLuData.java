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
public class XCTZFenLuData {
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
    @FieldDescription(description = "一工厂集配区")
    private String jp1;
    @FieldDescription(description = "一工厂线旁")
    private String xp1;
    @FieldDescription(description = "一工厂总数")
    private String zd1Total;
    @FieldDescription(description = "二工厂集配区")
    private String jp2;
    @FieldDescription(description = "二工厂线旁")
    private String xp2;
    @FieldDescription(description = "二工厂总数")
    private String zd2Total;
    @FieldDescription(description = "三工厂集配区")
    private String jp3;
    @FieldDescription(description = "三工厂线旁")
    private String xp3;
    @FieldDescription(description = "三工厂总数")
    private String zd3Total;

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

    public String getJp1() {
        return jp1;
    }

    public void setJp1(String jp1) {
        this.jp1 = jp1;
    }

    public String getXp1() {
        return xp1;
    }

    public void setXp1(String xp1) {
        this.xp1 = xp1;
    }

    public String getZd1Total() {
        return zd1Total;
    }

    public void setZd1Total(String zd1Total) {
        this.zd1Total = zd1Total;
    }

    public String getJp2() {
        return jp2;
    }

    public void setJp2(String jp2) {
        this.jp2 = jp2;
    }

    public String getXp2() {
        return xp2;
    }

    public void setXp2(String xp2) {
        this.xp2 = xp2;
    }

    public String getZd2Total() {
        return zd2Total;
    }

    public void setZd2Total(String zd2Total) {
        this.zd2Total = zd2Total;
    }

    public String getJp3() {
        return jp3;
    }

    public void setJp3(String jp3) {
        this.jp3 = jp3;
    }

    public String getXp3() {
        return xp3;
    }

    public void setXp3(String xp3) {
        this.xp3 = xp3;
    }

    public String getZd3Total() {
        return zd3Total;
    }

    public void setZd3Total(String zd3Total) {
        this.zd3Total = zd3Total;
    }
}
