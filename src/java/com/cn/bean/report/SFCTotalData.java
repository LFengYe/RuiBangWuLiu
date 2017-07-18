/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean.report;

import com.cn.bean.FieldDescription;

/**
 *
 * @author LFeng
 */
public class SFCTotalData {

    @FieldDescription(description = "供应商编号")
    private String supplierID;
    @FieldDescription(description = "供应商名称")
    private String supplierName;
    @FieldDescription(description = "部品编号")
    private String partID;
    @FieldDescription(description = "部品名称")
    private String partName;
    @FieldDescription(description = "部品件号")
    private String partCode;
    @FieldDescription(description = "车型")
    private String autoStylingName;
    @FieldDescription(description = "单车用量")
    private String dcAmount;
    @FieldDescription(description = "部品单位")
    private String partUnit;
    @FieldDescription(description = "库房期初")
    private String kfQC;
    @FieldDescription(description = "集配区期初")
    private String jpqQC;
    @FieldDescription(description = "线旁期初")
    private String xpQC;
    @FieldDescription(description = "入库数量")
    private String rk;
    @FieldDescription(description = "出库数量")
    private String ck;
    @FieldDescription(description = "临时调货")
    private String lsDH;
    @FieldDescription(description = "非生产领料")
    private String ll;
    @FieldDescription(description = "退货数量")
    private String th;
    @FieldDescription(description = "退库数量")
    private String tk;
    @FieldDescription(description = "库房结存")
    private String kfJC;
    @FieldDescription(description = "库房调账")
    private String kfTZ;
    @FieldDescription(description = "现场调账")
    private String xcTZ;
    @FieldDescription(description = "现场结算")
    private String xcJS;
    @FieldDescription(description = "集配区结存")
    private String jpqJC;
    @FieldDescription(description = "线旁结存")
    private String xpJC;
    @FieldDescription(description = "结存总数")
    private String jcTotal;

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

    public String getAutoStylingName() {
        return autoStylingName;
    }

    public void setAutoStylingName(String autoStylingName) {
        this.autoStylingName = autoStylingName;
    }

    public String getDcAmount() {
        return dcAmount;
    }

    public void setDcAmount(String dcAmount) {
        this.dcAmount = dcAmount;
    }

    public String getPartUnit() {
        return partUnit;
    }

    public void setPartUnit(String partUnit) {
        this.partUnit = partUnit;
    }

    public String getKfQC() {
        return kfQC;
    }

    public void setKfQC(String kfQC) {
        this.kfQC = kfQC;
    }

    public String getJpqQC() {
        return jpqQC;
    }

    public void setJpqQC(String jpqQC) {
        this.jpqQC = jpqQC;
    }

    public String getRk() {
        return rk;
    }

    public void setRk(String rk) {
        this.rk = rk;
    }

    public String getCk() {
        return ck;
    }

    public void setCk(String ck) {
        this.ck = ck;
    }

    public String getLsDH() {
        return lsDH;
    }

    public void setLsDH(String lsDH) {
        this.lsDH = lsDH;
    }

    public String getLl() {
        return ll;
    }

    public void setLl(String ll) {
        this.ll = ll;
    }

    public String getTh() {
        return th;
    }

    public void setTh(String th) {
        this.th = th;
    }

    public String getTk() {
        return tk;
    }

    public void setTk(String tk) {
        this.tk = tk;
    }

    public String getKfJC() {
        return kfJC;
    }

    public void setKfJC(String kfJC) {
        this.kfJC = kfJC;
    }

    public String getKfTZ() {
        return kfTZ;
    }

    public void setKfTZ(String kfTZ) {
        this.kfTZ = kfTZ;
    }

    public String getXcTZ() {
        return xcTZ;
    }

    public void setXcTZ(String xcTZ) {
        this.xcTZ = xcTZ;
    }

    public String getXcJS() {
        return xcJS;
    }

    public void setXcJS(String xcJS) {
        this.xcJS = xcJS;
    }

    public String getXpQC() {
        return xpQC;
    }

    public void setXpQC(String xpQC) {
        this.xpQC = xpQC;
    }

    public String getJpqJC() {
        return jpqJC;
    }

    public void setJpqJC(String jpqJC) {
        this.jpqJC = jpqJC;
    }

    public String getXpJC() {
        return xpJC;
    }

    public void setXpJC(String xpJC) {
        this.xpJC = xpJC;
    }

    public String getJcTotal() {
        return jcTotal;
    }

    public void setJcTotal(String jcTotal) {
        this.jcTotal = jcTotal;
    }
}
