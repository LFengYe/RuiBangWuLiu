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
public class PlatformCompanyInfo {
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    
    private String companyID;
    private String companyName;
    private String companyNameAPP;
    private String companyAddress;
    private String legalPersonName;
    private String legalPersonPhone;
    private String contactPersonName;
    private String contactPersonPhone;
    private String companyCode;
    private String busenessState;
    private String companyWebSite;
    private String companyRemark;

    public String getCompanyID() {
        return companyID;
    }

    public void setCompanyID(String compantyID) {
        this.companyID = compantyID;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String CompanyName) {
        this.companyName = CompanyName;
    }

    public String getCompanyNameAPP() {
        return companyNameAPP;
    }

    public void setCompanyNameAPP(String CompanyNameAPP) {
        this.companyNameAPP = CompanyNameAPP;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String CompanyAddress) {
        this.companyAddress = CompanyAddress;
    }

    public String getLegalPersonName() {
        return legalPersonName;
    }

    public void setLegalPersonName(String LegalPersonName) {
        this.legalPersonName = LegalPersonName;
    }

    public String getLegalPersonPhone() {
        return legalPersonPhone;
    }

    public void setLegalPersonPhone(String LegalPersonPhone) {
        this.legalPersonPhone = LegalPersonPhone;
    }

    public String getContactPersonName() {
        return contactPersonName;
    }

    public void setContactPersonName(String ContactPersonName) {
        this.contactPersonName = ContactPersonName;
    }

    public String getContactPersonPhone() {
        return contactPersonPhone;
    }

    public void setContactPersonPhone(String ContactPersonPhone) {
        this.contactPersonPhone = ContactPersonPhone;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String CompanyCode) {
        this.companyCode = CompanyCode;
    }

    public String getBusenessState() {
        return busenessState;
    }

    public void setBusenessState(String BusenessState) {
        this.busenessState = BusenessState;
    }

    public String getCompanyWebSite() {
        return companyWebSite;
    }

    public void setCompanyWebSite(String CompanyWebSite) {
        this.companyWebSite = CompanyWebSite;
    }

    public String getCompanyRemark() {
        return companyRemark;
    }

    public void setCompanyRemark(String CompanyRemark) {
        this.companyRemark = CompanyRemark;
    }
}
