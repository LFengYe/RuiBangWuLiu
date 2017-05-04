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
@ClassDescription(classDesc = "免检信息")
public class ExemptionInfo {

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
    @FieldDescription(description = " 免检标志")
    private String exemptionFlag;
    @FieldDescription(description = "备注")
    private String exemptionInfoRemark;

    public String getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(String SupplierID) {
        this.supplierID = SupplierID;
    }

    public String getPartCode() {
        return partCode;
    }

    public void setPartCode(String PartCode) {
        this.partCode = PartCode;
    }

    public String getExemptionFlag() {
        return exemptionFlag;
    }

    public void setExemptionFlag(String ExemptionFlag) {
        this.exemptionFlag = ExemptionFlag;
    }

    public String getExemptionInfoRemark() {
        return exemptionInfoRemark;
    }

    public void setExemptionInfoRemark(String ExemptionInfoRemark) {
        this.exemptionInfoRemark = ExemptionInfoRemark;
    }
    
}
