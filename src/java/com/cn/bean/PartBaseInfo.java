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

    private String partID;
    private String partName;
    private String partCode;
    private String autoStyling;
    private String partUnit;
    private int dCAmount;
    private String inboundContainer;
    private int inboundPackageAmount;
    private String outboundContainer;
    private int outboundPackageAmount;
    private boolean disabled;
    private String pinyinAbb;
    private String partCategory;
    private String partPicture;
    private String partBaseInfoRemark;
    private String pSAddress1;
    private String pSAddress2;
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

    public int getdCAmount() {
        return dCAmount;
    }

    public void setdCAmount(int dCAmount) {
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

    public boolean isDisabled() {
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

    public String getpSAddress1() {
        return pSAddress1;
    }

    public void setpSAddress1(String pSAddress1) {
        this.pSAddress1 = pSAddress1;
    }

    public String getpSAddress2() {
        return pSAddress2;
    }

    public void setpSAddress2(String pSAddress2) {
        this.pSAddress2 = pSAddress2;
    }

    public String getpSAddress3() {
        return pSAddress3;
    }

    public void setpSAddress3(String pSAddress3) {
        this.pSAddress3 = pSAddress3;
    }

}
