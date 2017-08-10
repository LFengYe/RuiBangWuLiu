/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean.out;

import com.cn.bean.FieldDescription;

/**
 *
 * @author LFeng
 */
public class JHOutWareHouseShort {
    @FieldDescription(description = "供应商代码", operate = "import")
    private String supplierID;
    @FieldDescription(description = "供应商名称", operate = "display")
    private String supplierName;
    @FieldDescription(description = "部品名称", operate = "display")
    private String partName;
    @FieldDescription(description = "部品件号", operate = "import")
    private String partCode;
    @FieldDescription(description = "计划数量", operate = "import")
    private int jhCKAmount;
    @FieldDescription(description = "实物数量", operate = "display")
    private int kcCount;
    @FieldDescription(description = "差缺数量", operate = "display")
    private int shortAmount;

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

    public int getJhCKAmount() {
        return jhCKAmount;
    }

    public void setJhCKAmount(int jhCKAmount) {
        this.jhCKAmount = jhCKAmount;
    }

    public int getShortAmount() {
        return shortAmount;
    }

    public void setShortAmount(int shortAmount) {
        this.shortAmount = shortAmount;
    }

    public int getKcCount() {
        return kcCount;
    }

    public void setKcCount(int kcCount) {
        this.kcCount = kcCount;
    }
}
