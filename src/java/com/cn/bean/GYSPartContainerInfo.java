/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean;

/**
 *
 * @author LFeng
 */
public class GYSPartContainerInfo {
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    @FieldDescription(description = "供应商代码", operate = "import")
    private String supplierID;
    @FieldDescription(description = "供应商名称", operate = "display")
    private String supplierName;
    @FieldDescription(description = "部品件号", operate = "import")
    private String partCode;
    @FieldDescription(description = "部品名称", operate = "display")
    private String partName;
    @FieldDescription(description = "部品编号", operate = "display")
    private String partID;
    @FieldDescription(description = "车型", operate = "display")
    private String autoStylingName;
    @FieldDescription(description = "部品单位", operate = "display")
    private String partUnit;
    @FieldDescription(description = "入库盛具", operate = "import")
    private String inboundContainerName;
    @FieldDescription(description = "入库包装数量", operate = "import")
    private int inboundPackageAmount;
    @FieldDescription(description = "出库盛具", operate = "import")
    private String outboundContainerName;
    @FieldDescription(description = "出库包装数量", operate = "import")
    private int outboundPackageAmount;
    @FieldDescription(description = "部品分类", operate = "import")
    private String partCategoryName;
    @FieldDescription(description = "备注", operate = "import")
    private String gysPartContainerInfoRemark;
    
    
    public String getInboundContainerName() {
        return inboundContainerName;
    }

    public void setInboundContainerName(String inboundContainerName) {
        this.inboundContainerName = inboundContainerName;
    }

    public int getInboundPackageAmount() {
        return inboundPackageAmount;
    }

    public void setInboundPackageAmount(int inboundPackageAmount) {
        this.inboundPackageAmount = inboundPackageAmount;
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

    public String getGysPartContainerInfoRemark() {
        return gysPartContainerInfoRemark;
    }

    public void setGysPartContainerInfoRemark(String gysPartContainerInfoRemark) {
        this.gysPartContainerInfoRemark = gysPartContainerInfoRemark;
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

    public String getAutoStylingName() {
        return autoStylingName;
    }

    public void setAutoStylingName(String autoStylingName) {
        this.autoStylingName = autoStylingName;
    }

    public String getPartUnit() {
        return partUnit;
    }

    public void setPartUnit(String partUnit) {
        this.partUnit = partUnit;
    }

    public String getPartCategoryName() {
        return partCategoryName;
    }

    public void setPartCategoryName(String partCategoryName) {
        this.partCategoryName = partCategoryName;
    }
}
