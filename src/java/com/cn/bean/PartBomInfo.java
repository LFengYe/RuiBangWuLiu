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
public class PartBomInfo {
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    @FieldDescription(description = "总成件号", operate = "import")
    private String zcPartCode;
    @FieldDescription(description = "明细件号", operate = "import")
    private String detailPartCode;
    @FieldDescription(description = "供应商", operate = "import")
    private String supplierID;
    @FieldDescription(description = "单车用量", operate = "import")
    private int dcAmount;
    @FieldDescription(description = "备注", operate = "import")
    private String remarker;

    public String getZcPartCode() {
        return zcPartCode;
    }

    public void setZcPartCode(String zcPartCode) {
        this.zcPartCode = zcPartCode;
    }

    public String getDetailPartCode() {
        return detailPartCode;
    }

    public void setDetailPartCode(String detailPartCode) {
        this.detailPartCode = detailPartCode;
    }

    public String getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(String supplierID) {
        this.supplierID = supplierID;
    }

    public int getDcAmount() {
        return dcAmount;
    }

    public void setDcAmount(int dcAmount) {
        this.dcAmount = dcAmount;
    }

    public String getRemarker() {
        return remarker;
    }

    public void setRemarker(String remarker) {
        this.remarker = remarker;
    }
}
