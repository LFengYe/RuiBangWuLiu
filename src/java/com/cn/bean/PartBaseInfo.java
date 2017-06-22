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
@ClassDescription(classDesc = "部品档案")
public class PartBaseInfo {

    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }

    @FieldDescription(description = "部品编号", operate = "import")
    private String partID;
    @FieldDescription(description = "部品名称", operate = "import")
    private String partName;
    @FieldDescription(description = "部品件号", operate = "import")
    private String partCode;
    @FieldDescription(description = "使用车型", operate = "import")
    private String autoStylingName;
    @FieldDescription(description = "装配工位", operate = "import")
    private String assemblingStation;
    @FieldDescription(description = "部品单位", operate = "import")
    private String partUnit;
    @FieldDescription(description = "单车用量", operate = "import")
    private int dCAmount;
    @FieldDescription(description = "部品分类", operate = "import")
    private String partCategoryName;
    @FieldDescription(description = "备注", operate = "import")
    private String partBaseInfoRemark;
//    @FieldDescription(description = "是否停用")
    private boolean disabled;
//    @FieldDescription(description = "拼音助计")
    private String pinyinAbb;
//    @FieldDescription(description = "部品图片")
    private String partPicture;

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

    public int getDCAmount() {
        return dCAmount;
    }

    public void setDCAmount(int dCAmount) {
        this.dCAmount = dCAmount;
    }

    public String getPinyinAbb() {
        return pinyinAbb;
    }

    public void setPinyinAbb(String pinyinAbb) {
        this.pinyinAbb = pinyinAbb;
    }

    public String getPartCategoryName() {
        return partCategoryName;
    }

    public void setPartCategoryName(String partCategoryName) {
        this.partCategoryName = partCategoryName;
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

    public boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getAssemblingStation() {
        return assemblingStation;
    }

    public void setAssemblingStation(String assemblingStation) {
        this.assemblingStation = assemblingStation;
    }

    public int getdCAmount() {
        return dCAmount;
    }

    public void setdCAmount(int dCAmount) {
        this.dCAmount = dCAmount;
    }

}
