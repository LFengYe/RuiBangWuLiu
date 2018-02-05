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
public class KCAlertListData {
    
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
    @FieldDescription(description = "库存最高值")
    private int kcHighBound;
    @FieldDescription(description = "库存最低值")
    private int kcLowBound;
    @FieldDescription(description = "库存总数")
    private int kfJCTotal;
    @FieldDescription(description = "合格库存数")
    private int hgKCAmount;
    @FieldDescription(description = "库容余量")
    private int krYAmount;
    @FieldDescription(description = "缺件及溢出数")
    private int qjJYCAmount;
    @FieldDescription(description = "备注")
    private String kcAlertRemark;

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

    public String getKcAlertRemark() {
        return kcAlertRemark;
    }

    public void setKcAlertRemark(String kcAlertRemark) {
        this.kcAlertRemark = kcAlertRemark;
    }
}
