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
public class LedPlanDisplay {
    @FieldDescription(description = "供应商ID")
    private String supplierID;
    @FieldDescription(description = "部品代码")
    private String partCode;
    @FieldDescription(description = "入库批次")
    private String inboundBatch;
    @FieldDescription(description = "计划数量")
    private String planNum;
    @FieldDescription(description = "换装数量")
    private String containerAmount;
//    @FieldDescription(description = "换装箱数")
//    private String containerBoxAmount;
    @FieldDescription(description = "换装盛具")
    private String container;
//    @FieldDescription(description = "仓位号")
//    private String areaCode;

    public String getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(String supplierID) {
        this.supplierID = supplierID;
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

    public String getInboundBatch() {
        return inboundBatch;
    }

    public void setInboundBatch(String inboundBatch) {
        this.inboundBatch = inboundBatch;
    }
}
