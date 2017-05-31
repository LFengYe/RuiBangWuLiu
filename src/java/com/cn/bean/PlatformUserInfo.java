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
@ClassDescription(classDesc = "注册平台用户")
public class PlatformUserInfo {

    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    
    @FieldDescription(description = "用户名", operate = "import")
    private String userLoginAccount;
    @FieldDescription(description = "密码", operate = "import")
    private String userLoginPassWord;
    @FieldDescription(description = "公司编号", operate = "import")
    private String companyID;
    @FieldDescription(description = "数据库名", operate = "import")
    private String userLoginDBName;
    @FieldDescription(description = "备注", operate = "import")
    private String userInfoRemark;

    public String getUserLoginAccount() {
        return userLoginAccount;
    }

    public void setUserLoginAccount(String UserLoginAccount) {
        this.userLoginAccount = UserLoginAccount;
    }

    public String getUserLoginPassWord() {
        return userLoginPassWord;
    }

    public void setUserLoginPassWord(String UserLoginPassWord) {
        this.userLoginPassWord = UserLoginPassWord;
    }

    public String getUserLoginDBName() {
        return userLoginDBName;
    }

    public void setUserLoginDBName(String UserLoginDBName) {
        this.userLoginDBName = UserLoginDBName;
    }

    public String getUserInfoRemark() {
        return userInfoRemark;
    }

    public void setUserInfoRemark(String UserInfoRemark) {
        this.userInfoRemark = UserInfoRemark;
    }

    public String getCompanyID() {
        return companyID;
    }

    public void setCompanyID(String companyID) {
        this.companyID = companyID;
    }
}
