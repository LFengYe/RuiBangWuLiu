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
@ClassDescription(classDesc = "注册公司")
public class PlatformCompanyInfo {

    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }

    @FieldDescription(description = "公司编号")
    private String companyID;
    @FieldDescription(description = "公司名称")
    private String companyName;
    @FieldDescription(description = "名称缩写")
    private String companyNameAPP;
    @FieldDescription(description = "公司地址")
    private String companyAddress;
    @FieldDescription(description = "法人姓名")
    private String legalPersonName;
    @FieldDescription(description = "法人电话")
    private String legalPersonPhone;
    @FieldDescription(description = "联系人姓名")
    private String contactPersonName;
    @FieldDescription(description = "联系人电话")
    private String contactPersonPhone;
    @FieldDescription(description = "公司代码")
    private String companyCode;
    @FieldDescription(description = "是否停办业务")
    private boolean busenessState;
    @FieldDescription(description = "公司网址")
    private String companyWebSite;
    @FieldDescription(description = "备注")
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

    public boolean getBusenessState() {
        return busenessState;
    }

    public void setBusenessState(boolean BusenessState) {
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

    /*
    public static void main(String[] args) {
        try {
            Class objClass = Class.forName("com.cn.bean.PlatformCompanyInfo");
            ClassDescription description = (ClassDescription) objClass.getAnnotation(ClassDescription.class);
            System.out.println(description.classDesc());
            
            Field[] fields = objClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(FieldDescription.class)) {
                    //获取字段名  
                    String fieldNames = objClass.getSimpleName() + "." + field.getName();
                    //获取字段注解  
                    FieldDescription description = field.getAnnotation(FieldDescription.class);
                    
                    System.out.println("fieldNames:" + fieldNames + ",des:" + description.description());
                }
            }
                    
        } catch (ClassNotFoundException ex) {
            
        }
    }
    */
}
