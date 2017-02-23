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
public class PartBaseInfo {

    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }

    @FieldDescription(description = "部品编号")
    private String partID;
    @FieldDescription(description = "部品名称")
    private String partName;
    @FieldDescription(description = "部品件号")
    private String partCode;
    @FieldDescription(description = "使用车型")
    private String autoStyling;
    @FieldDescription(description = "部品单位")
    private String partUnit;
    @FieldDescription(description = "单车用量")
    private int dCAmount;
    @FieldDescription(description = "入库盛具")
    private String inboundContainer;
    @FieldDescription(description = "入库包装数量")
    private int inboundPackageAmount;
    @FieldDescription(description = "出库盛具")
    private String outboundContainer;
    @FieldDescription(description = "出库包装数量")
    private int outboundPackageAmount;
    @FieldDescription(description = "是否停用")
    private boolean disabled;
    @FieldDescription(description = "拼音助计")
    private String pinyinAbb;
    @FieldDescription(description = "部品分类")
    private String partCategory;
    @FieldDescription(description = "部品图片")
    private String partPicture;
    @FieldDescription(description = "备注")
    private String partBaseInfoRemark;
    @FieldDescription(description = "配送地址1")
    private String pSAddress1;
    @FieldDescription(description = "配送地址2")
    private String pSAddress2;
    @FieldDescription(description = "配送地址3")
    private String pSAddress3;

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

    public String getAutoStyling() {
        return autoStyling;
    }

    public void setAutoStyling(String autoStyling) {
        this.autoStyling = autoStyling;
    }

    public String getPartUnit() {
        return partUnit;
    }

    public void setPartUnit(String partUnit) {
        this.partUnit = partUnit;
    }

    public int getDCAmount() {
        return dCAmount;
    }

    public void setDCAmount(int dCAmount) {
        this.dCAmount = dCAmount;
    }

    public String getInboundContainer() {
        return inboundContainer;
    }

    public void setInboundContainer(String inboundContainer) {
        this.inboundContainer = inboundContainer;
    }

    public int getInboundPackageAmount() {
        return inboundPackageAmount;
    }

    public void setInboundPackageAmount(int inboundPackageAmount) {
        this.inboundPackageAmount = inboundPackageAmount;
    }

    public String getOutboundContainer() {
        return outboundContainer;
    }

    public void setOutboundContainer(String outboundContainer) {
        this.outboundContainer = outboundContainer;
    }

    public int getOutboundPackageAmount() {
        return outboundPackageAmount;
    }

    public void setOutboundPackageAmount(int outboundPackageAmount) {
        this.outboundPackageAmount = outboundPackageAmount;
    }

    public boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getPinyinAbb() {
        return pinyinAbb;
    }

    public void setPinyinAbb(String pinyinAbb) {
        this.pinyinAbb = pinyinAbb;
    }

    public String getPartCategory() {
        return partCategory;
    }

    public void setPartCategory(String partCategory) {
        this.partCategory = partCategory;
    }

    public String getPartPicture() {
        return partPicture;
    }

    public void setPartPicture(String partPicture) {
        this.partPicture = partPicture;
    }

    public String getPartBaseInfoRemark() {
        return partBaseInfoRemark;
    }

    public void setPartBaseInfoRemark(String partBaseInfoRemark) {
        this.partBaseInfoRemark = partBaseInfoRemark;
    }

    public String getPSAddress1() {
        return pSAddress1;
    }

    public void setPSAddress1(String pSAddress1) {
        this.pSAddress1 = pSAddress1;
    }

    public String getPSAddress2() {
        return pSAddress2;
    }

    public void setPSAddress2(String pSAddress2) {
        this.pSAddress2 = pSAddress2;
    }

    public String getPSAddress3() {
        return pSAddress3;
    }

    public void setPSAddress3(String pSAddress3) {
        this.pSAddress3 = pSAddress3;
    }

}
