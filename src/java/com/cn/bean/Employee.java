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
public class Employee {

    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
//    @FieldDescription(description = "雇员编号")
//    private String employeeId;
    @FieldDescription(description = "雇员姓名", operate = "import")
    private String employeeName;
    @FieldDescription(description = "雇员登录密码", operate = "import")
    private String employeePassword;
    @FieldDescription(description = "雇员类型代码", operate = "import")
    private String employeeTypeCode;
    @FieldDescription(description = "雇员类型", operate = "import")
    private String employeeType;
    @FieldDescription(description = "雇员手机", operate = "import")
    private String employeePhone;
    @FieldDescription(description = "雇员身份证号", operate = "import")
    private String employeeIdentityCard;
    @FieldDescription(description = "备注", operate = "import")
    private String employeeRemark;

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeType() {
        return employeeType;
    }

    public void setEmployeeType(String employeeType) {
        this.employeeType = employeeType;
    }

    public String getEmployeePhone() {
        return employeePhone;
    }

    public void setEmployeePhone(String employeePhone) {
        this.employeePhone = employeePhone;
    }

    public String getEmployeeIdentityCard() {
        return employeeIdentityCard;
    }

    public void setEmployeeIdentityCard(String employeeIdentityCard) {
        this.employeeIdentityCard = employeeIdentityCard;
    }

    public String getEmployeeRemark() {
        return employeeRemark;
    }

    public void setEmployeeRemark(String employeeRemark) {
        this.employeeRemark = employeeRemark;
    }

    public String getEmployeeTypeCode() {
        return employeeTypeCode;
    }

    public void setEmployeeTypeCode(String employeeTypeCode) {
        this.employeeTypeCode = employeeTypeCode;
    }

    public String getEmployeePassword() {
        return employeePassword;
    }

    public void setEmployeePassword(String employeePassword) {
        this.employeePassword = employeePassword;
    }
}
