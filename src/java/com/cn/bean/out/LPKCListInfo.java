/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean.out;

import com.cn.bean.FieldDescription;

/**
 *
 * @author LFeng
 */
public class LPKCListInfo {
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    
    @FieldDescription(description = "供应商编号")
    private String supplierID;//供应商ID
    @FieldDescription(description = "供应商名称")
    private String supplierName;
    @FieldDescription(description = "部品件号")
    private String partCode;//件号
    @FieldDescription(description = "部品名称")
    private String partName;
    @FieldDescription(description = "部品编号")
    private String partID;
    @FieldDescription(description = "入库批次")
    private String inboundBatch;//入库批次
    @FieldDescription(description = "良品数量")
    private int lpAmount;//良品数量
    @FieldDescription(description = "出库包装盛具")
    private String outboundContainerName;
    @FieldDescription(description = "出库包装数量")
    private int outboundPackageAmount;

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

    public String getInboundBatch() {
        return inboundBatch;
    }

    public void setInboundBatch(String inboundBatch) {
        this.inboundBatch = inboundBatch;
    }

    public int getLpAmount() {
        return lpAmount;
    }

    public void setLpAmount(int lpAmount) {
        this.lpAmount = lpAmount;
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

    public String getPartID() {
        return partID;
    }

    public void setPartID(String partID) {
        this.partID = partID;
    }

    public String getOutboundContainerName() {
        return outboundContainerName;
    }

    public void setOutboundContainerName(String outboundContainerName) {
        this.outboundContainerName = outboundContainerName;
    }

    public int getOutboundPackageAmount() {
        return outboundPackageAmount;
    }

    public void setOutboundPackageAmount(int outboundPackageAmount) {
        this.outboundPackageAmount = outboundPackageAmount;
    }
    
    public String getType() {
        return this.supplierID + "_" + this.partCode.toLowerCase();
    }
}
