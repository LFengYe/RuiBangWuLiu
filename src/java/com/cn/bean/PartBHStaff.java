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
@ClassDescription(classDesc = "部品备货人员")
public class PartBHStaff {
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    
    @FieldDescription(description = "供应商代码", operate = "import")
    private String supplierID;
    @FieldDescription(description = "部品件号", operate = "import")
    private String partCode;
    @FieldDescription(description = "备货员", operate = "import")
    private String employeeName;
    @FieldDescription(description = "备注", operate = "import")
    private String remarker;

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

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getRemarker() {
        return remarker;
    }

    public void setRemarker(String remarker) {
        this.remarker = remarker;
    }
    
}
