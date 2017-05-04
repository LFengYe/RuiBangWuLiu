/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean.out;

import com.cn.bean.ClassDescription;
import com.cn.bean.FieldDescription;

/**
 *
 * @author LFeng
 */
@ClassDescription(classDesc = "下线信息")
public class OffLineInfo {

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
    @FieldDescription(description = "下线数量")
    private int offLineAmount;
    @FieldDescription(description = "生产班次")
    private String produceShift;
    @FieldDescription(description = "下线日期")
    private String offLineDate;
    @FieldDescription(description = " 输入者")
    private String inputerName;
    @FieldDescription(description = " 备注")
    private String offLineInfoRemark;

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

    public int getOffLineAmount() {
        return offLineAmount;
    }

    public void setOffLineAmount(int offLineAmount) {
        this.offLineAmount = offLineAmount;
    }

    public String getProduceShift() {
        return produceShift;
    }

    public void setProduceShift(String produceShift) {
        this.produceShift = produceShift;
    }

    public String getOffLineDate() {
        return offLineDate;
    }

    public void setOffLineDate(String offLineDate) {
        this.offLineDate = offLineDate;
    }

    public String getInputerName() {
        return inputerName;
    }

    public void setInputerName(String inputerName) {
        this.inputerName = inputerName;
    }

    public String getOffLineInfoRemark() {
        return offLineInfoRemark;
    }

    public void setOffLineInfoRemark(String offLineInfoRemark) {
        this.offLineInfoRemark = offLineInfoRemark;
    }
}
