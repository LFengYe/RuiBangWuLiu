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
public class SHPlan {

    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    @FieldDescription(description = "送货计划单号")
    private String shPlanID;
    @FieldDescription(description = "调货计划单号")
    private String dhPlanID;
    @FieldDescription(description = "供应商代码")
    private String supplierID;
    @FieldDescription(description = "供应商名称")
    private String supplierName;
    @FieldDescription(description = "送货时间")
    private String shTime;
    @FieldDescription(description = "送货方式")
    private String shMethod;
    @FieldDescription(description = "预计到货时间")
    private String yjDHTime;
    @FieldDescription(description = "打印标志")
    private String printFlag;
    @FieldDescription(description = "备注")
    private String shPlanRemark;

    public String getShPlanID() {
        return shPlanID;
    }

    public void setShPlanID(String shPlanID) {
        this.shPlanID = shPlanID;
    }

    public String getDhPlanID() {
        return dhPlanID;
    }

    public void setDhPlanID(String dhPlanID) {
        this.dhPlanID = dhPlanID;
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

    public String getShTime() {
        return shTime;
    }

    public void setShTime(String shTime) {
        this.shTime = shTime;
    }

    public String getShMethod() {
        return shMethod;
    }

    public void setShMethod(String shMethod) {
        this.shMethod = shMethod;
    }

    public String getYjDHTime() {
        return yjDHTime;
    }

    public void setYjDHTime(String yjDHTime) {
        this.yjDHTime = yjDHTime;
    }

    public String getPrintFlag() {
        return printFlag;
    }

    public void setPrintFlag(String printFlag) {
        this.printFlag = printFlag;
    }

    public String getShPlanRemark() {
        return shPlanRemark;
    }

    public void setShPlanRemark(String shPlanRemark) {
        this.shPlanRemark = shPlanRemark;
    }
}
