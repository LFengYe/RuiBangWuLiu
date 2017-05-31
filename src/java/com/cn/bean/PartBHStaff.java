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
@ClassDescription(classDesc = "部品人员")
public class PartBHStaff {
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    
    @FieldDescription(description = "部品件号", operate = "import")
    private String partCode;
    @FieldDescription(description = "备货员", operate = "import")
    private String bhEmployeeName;
    @FieldDescription(description = "领货员", operate = "import")
    private String lhEmployeeName;
    @FieldDescription(description = "配送员", operate = "import")
    private String psEmployeeName;
    @FieldDescription(description = "备注", operate = "import")
    private String remarker;

    public String getPartCode() {
        return partCode;
    }

    public void setPartCode(String partCode) {
        this.partCode = partCode;
    }

    public String getRemarker() {
        return remarker;
    }

    public void setRemarker(String remarker) {
        this.remarker = remarker;
    }

    public String getBhEmployeeName() {
        return bhEmployeeName;
    }

    public void setBhEmployeeName(String bhEmployeeName) {
        this.bhEmployeeName = bhEmployeeName;
    }

    public String getLhEmployeeName() {
        return lhEmployeeName;
    }

    public void setLhEmployeeName(String lhEmployeeName) {
        this.lhEmployeeName = lhEmployeeName;
    }

    public String getPsEmployeeName() {
        return psEmployeeName;
    }

    public void setPsEmployeeName(String psEmployeeName) {
        this.psEmployeeName = psEmployeeName;
    }
    
}
