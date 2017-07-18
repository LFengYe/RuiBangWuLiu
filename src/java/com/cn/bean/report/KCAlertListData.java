/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean.report;

/**
 *
 * @author LFeng
 */
public class KCAlertListData {

    private String supplierID;
    private String supplierName;
    private String partID;
    private String partName;
    private String partCode;
    private int kcHighBound;
    private int kcLowBound;
    private int kfJCTotal;
    private int hgKCAmount;
    private int krYAmount;
    private int qjJYCAmount;
    private int kcAlertRemark;

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

    public int getKcHighBound() {
        return kcHighBound;
    }

    public void setKcHighBound(int kcHighBound) {
        this.kcHighBound = kcHighBound;
    }

    public int getKcLowBound() {
        return kcLowBound;
    }

    public void setKcLowBound(int kcLowBound) {
        this.kcLowBound = kcLowBound;
    }

    public int getKfJCTotal() {
        return kfJCTotal;
    }

    public void setKfJCTotal(int kfJCTotal) {
        this.kfJCTotal = kfJCTotal;
    }

    public int getHgKCAmount() {
        return hgKCAmount;
    }

    public void setHgKCAmount(int hgKCAmount) {
        this.hgKCAmount = hgKCAmount;
    }

    public int getKrYAmount() {
        return krYAmount;
    }

    public void setKrYAmount(int krYAmount) {
        this.krYAmount = krYAmount;
    }

    public int getQjJYCAmount() {
        return qjJYCAmount;
    }

    public void setQjJYCAmount(int qjJYCAmount) {
        this.qjJYCAmount = qjJYCAmount;
    }

    public int getKcAlertRemark() {
        return kcAlertRemark;
    }

    public void setKcAlertRemark(int kcAlertRemark) {
        this.kcAlertRemark = kcAlertRemark;
    }
}
