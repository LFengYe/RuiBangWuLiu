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
public class DHPlan {

    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    @FieldDescription(description = "调货计划单号")
    private String dhPlanID;
    @FieldDescription(description = "供应商代码")
    private String supplierID;
    @FieldDescription(description = "供应商名称")
    private String supplierName;
    @FieldDescription(description = "到货时间")
    private String dhTime;
    @FieldDescription(description = "制单人员姓名")
    private String dhProducerName;
    @FieldDescription(description = "制单时间")
    private String dhProduceTime;
    @FieldDescription(description = "审核人员姓名")
    private String dhAuditStaffName;
    @FieldDescription(description = "审核时间")
    private String dhAuditTime;
    @FieldDescription(description = "确认标识")
    private int shAcknowledge;
    @FieldDescription(description = "打印标志")
    private String printFlag;
    @FieldDescription(description = "备注")
    private String dhPlanRemark;

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

    public String getDhTime() {
        return dhTime;
    }

    public void setDhTime(String dhTime) {
        this.dhTime = dhTime;
    }

    public String getDhProducerName() {
        return dhProducerName;
    }

    public void setDhProducerName(String dhProducerName) {
        this.dhProducerName = dhProducerName;
    }

    public String getDhProduceTime() {
        return dhProduceTime;
    }

    public void setDhProduceTime(String dhProduceTime) {
        this.dhProduceTime = dhProduceTime;
    }

    public String getDhAuditStaffName() {
        return dhAuditStaffName;
    }

    public void setDhAuditStaffName(String dhAuditStaffName) {
        this.dhAuditStaffName = dhAuditStaffName;
    }

    public String getDhAuditTime() {
        return dhAuditTime;
    }

    public void setDhAuditTime(String dhAuditTime) {
        this.dhAuditTime = dhAuditTime;
    }

    public int getShAcknowledge() {
        return shAcknowledge;
    }

    public void setShAcknowledge(int shAcknowledge) {
        this.shAcknowledge = shAcknowledge;
    }

    public String getPrintFlag() {
        return printFlag;
    }

    public void setPrintFlag(String printFlag) {
        this.printFlag = printFlag;
    }

    public String getDhPlanRemark() {
        return dhPlanRemark;
    }

    public void setDhPlanRemark(String dhPlanRemark) {
        this.dhPlanRemark = dhPlanRemark;
    }
}
