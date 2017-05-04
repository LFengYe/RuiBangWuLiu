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
@ClassDescription(classDesc = "客户档案")
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
    private String customerTypeName;
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
    @FieldDescription(description = "接收短信号码")
    private String smsNumbers;
    @FieldDescription(description = "短信发送时间")
    private String smsSendTime;
    @FieldDescription(description = "送货方式")
    private String shMethodName;
    @FieldDescription(description = "运输周期(小时)")
    private int transportCycle;
    @FieldDescription(description = "拼音助记")
    private String pinyinAbb;
    @FieldDescription(description = "备注")
    private String customerRemark;

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

    public String getCustomerTypeName() {
        return customerTypeName;
    }

    public void setCustomerTypeName(String customerTypeName) {
        this.customerTypeName = customerTypeName;
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

    public String getCustomerRemark() {
        return customerRemark;
    }

    public void setCustomerRemark(String customerRemark) {
        this.customerRemark = customerRemark;
    }

    public String getShMethodName() {
        return shMethodName;
    }

    public void setShMethodName(String shMethodName) {
        this.shMethodName = shMethodName;
    }

    public String getSmsNumbers() {
        return smsNumbers;
    }

    public void setSmsNumbers(String smsNumbers) {
        this.smsNumbers = smsNumbers;
    }

    public String getSmsSendTime() {
        return smsSendTime;
    }

    public void setSmsSendTime(String smsSendTime) {
        this.smsSendTime = smsSendTime;
    }
}
