/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean.plan;

import com.cn.bean.FieldDescription;

/**
 *
 * @author LFeng
 */
public class DHPlanList {

    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }

    @FieldDescription(description = "单内显示序号")
    private int listNumber;
    @FieldDescription(description = "供应商代码")
    private String supplierID;
    @FieldDescription(description = "供应商名称")
    private String supplierName;
    @FieldDescription(description = "部品编号")
    private String partID;
    @FieldDescription(description = "部品名称")
    private String partName;
    @FieldDescription(description = "部品件号")
    private String partCode;
    @FieldDescription(description = "部品单位")
    private String partUnit;
    @FieldDescription(description = "计划数量")
    private int dhAmount;
    @FieldDescription(description = "备注")
    private String dhPlanListRemark;
    @FieldDescription(description = "调货计划单号")
    private String dhPlanID;

    public int getListNumber() {
        return listNumber;
    }

    public void setListNumber(int listNumber) {
        this.listNumber = listNumber;
    }

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

    public String getPartUnit() {
        return partUnit;
    }

    public void setPartUnit(String partUnit) {
        this.partUnit = partUnit;
    }

    public int getDhAmount() {
        return dhAmount;
    }

    public void setDhAmount(int dhAmount) {
        this.dhAmount = dhAmount;
    }

    public String getDhPlanListRemark() {
        return dhPlanListRemark;
    }

    public void setDhPlanListRemark(String dhPlanListRemark) {
        this.dhPlanListRemark = dhPlanListRemark;
    }

    public String getDhPlanID() {
        return dhPlanID;
    }

    public void setDhPlanID(String dhPlanID) {
        this.dhPlanID = dhPlanID;
    }
}
