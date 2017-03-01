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
@ClassDescription(classDesc = "库存安全")
public class KCQuota {

    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    @FieldDescription(description = "供应商代码")
    private String supplierID;
    @FieldDescription(description = "部品件号")
    private String partCode;
    @FieldDescription(description = "定额上限")
    private int kCHighBound;
    @FieldDescription(description = "定额下限")
    private int kCLowBound;
    @FieldDescription(description = "备注")
    private String kCQuotaRemark;

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

    public int getKCHighBound() {
        return kCHighBound;
    }

    public void setKCHighBound(int kCHighBound) {
        this.kCHighBound = kCHighBound;
    }

    public int getKCLowBound() {
        return kCLowBound;
    }

    public void setKCLowBound(int kCLowBound) {
        this.kCLowBound = kCLowBound;
    }

    public String getKCQuotaRemark() {
        return kCQuotaRemark;
    }

    public void setKCQuotaRemark(String kCQuotaRemark) {
        this.kCQuotaRemark = kCQuotaRemark;
    }
    
}
