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
public class Customer {

    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    @FieldDescription(description = "客户代码")
    private String customerID;
    @FieldDescription(description = "客户名称")
    private String customerName;
    @FieldDescription(description = "客户名称简称")
    private String customerAbbName;
    @FieldDescription(description = "客户类型")
    private String customerType;
    @FieldDescription(description = "经办人")
    private String customerManager;
    @FieldDescription(description = "经办人电话")
    private String customerManagerPhone;
    @FieldDescription(description = "办公电话")
    private String customerOfficePhone;
    @FieldDescription(description = "传真")
    private String customerOfficeFax;
    @FieldDescription(description = "邮箱")
    private String customerEmail;
    @FieldDescription(description = "送货方式")
    private String sHMethod;
    @FieldDescription(description = "运输周期(小时)")
    private int transportCycle;
    @FieldDescription(description = "拼音助记")
    private String pinyinAbb;
    @FieldDescription(description = "备注")
    private String cutomerRemark;

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerAbbName() {
        return customerAbbName;
    }

    public void setCustomerAbbName(String customerAbbName) {
        this.customerAbbName = customerAbbName;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getCustomerManager() {
        return customerManager;
    }

    public void setCustomerManager(String customerManager) {
        this.customerManager = customerManager;
    }

    public String getCustomerManagerPhone() {
        return customerManagerPhone;
    }

    public void setCustomerManagerPhone(String customerManagerPhone) {
        this.customerManagerPhone = customerManagerPhone;
    }

    public String getCustomerOfficePhone() {
        return customerOfficePhone;
    }

    public void setCustomerOfficePhone(String customerOfficePhone) {
        this.customerOfficePhone = customerOfficePhone;
    }

    public String getCustomerOfficeFax() {
        return customerOfficeFax;
    }

    public void setCustomerOfficeFax(String customerOfficeFax) {
        this.customerOfficeFax = customerOfficeFax;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getSHMethod() {
        return sHMethod;
    }

    public void setSHMethod(String sHMethod) {
        this.sHMethod = sHMethod;
    }

    public int getTransportCycle() {
        return transportCycle;
    }

    public void setTransportCycle(int transportCycle) {
        this.transportCycle = transportCycle;
    }

    public String getPinyinAbb() {
        return pinyinAbb;
    }

    public void setPinyinAbb(String pinyinAbb) {
        this.pinyinAbb = pinyinAbb;
    }

    public String getCutomerRemark() {
        return cutomerRemark;
    }

    public void setCutomerRemark(String cutomerRemark) {
        this.cutomerRemark = cutomerRemark;
    }
}
