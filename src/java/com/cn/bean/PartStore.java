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
@ClassDescription(classDesc = "部品存放地址")
public class PartStore {

    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    
    @FieldDescription(description = "供应商代码")
    private String supplierID;
//    @FieldDescription(description = "供应商名称")
//    private String supplierName;
//    @FieldDescription(description = "部品代码")
//    private String partID;
//    @FieldDescription(description = "部品名称")
//    private String partName;
    @FieldDescription(description = "部品件号")
    private String partCode;
    @FieldDescription(description = "库房存放地址")
    private String kfCFAddress;
    @FieldDescription(description = "集配区存放地址")
    private String jpqCFAddress;
    @FieldDescription(description = "备注")
    private String partStoreRemark;

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

    public String getPartStoreRemark() {
        return partStoreRemark;
    }

    public void setPartStoreRemark(String partStoreRemark) {
        this.partStoreRemark = partStoreRemark;
    }

    public String getKfCFAddress() {
        return kfCFAddress;
    }

    public void setKfCFAddress(String kfCFAddress) {
        this.kfCFAddress = kfCFAddress;
    }

    public String getJpqCFAddress() {
        return jpqCFAddress;
    }

    public void setJpqCFAddress(String jpqCFAddress) {
        this.jpqCFAddress = jpqCFAddress;
    }
}
