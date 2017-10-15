/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.led;

import com.cn.bean.FieldDescription;

/**
 *
 * @author LFeng
 */
public class LedPlan {
    @FieldDescription(description = "供应商ID")
    private String supplierID;
    @FieldDescription(description = "供应商姓名")
    private String supplierName;
    @FieldDescription(description = "部品名称")
    private String partName;
    @FieldDescription(description = "部品代码")
    private String partCode;
    @FieldDescription(description = "入库批次")
    private String inboundBatch;
    @FieldDescription(description = "计划数量")
    private String planNum;
    @FieldDescription(description = "换装盛具")
    private String container;
    @FieldDescription(description = "换装数量")
    private String containerAmount;
    @FieldDescription(description = "换装箱数")
    private String containerBoxAmount;
    @FieldDescription(description = "仓位号")
    private String areaCode;

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

    public String getPlanNum() {
        return planNum;
    }

    public void setPlanNum(String planNum) {
        this.planNum = planNum;
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }

    public String getContainerAmount() {
        return containerAmount;
    }

    public void setContainerAmount(String containerAmount) {
        this.containerAmount = containerAmount;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getInboundBatch() {
        return inboundBatch;
    }

    public void setInboundBatch(String inboundBatch) {
        this.inboundBatch = inboundBatch;
    }

    public String getContainerBoxAmount() {
        return containerBoxAmount;
    }

    public void setContainerBoxAmount(String containerBoxAmount) {
        this.containerBoxAmount = containerBoxAmount;
    }
}
