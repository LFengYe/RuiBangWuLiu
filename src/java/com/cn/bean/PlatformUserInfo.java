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
public class PlatformUserInfo {

    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    
    @FieldDescription(description = "用户名")
    private String UserLoginAccount;
    @FieldDescription(description = "密码")
    private String UserLoginPassWord;
    @FieldDescription(description = "数据库名")
    private String UserLoginDBName;
    @FieldDescription(description = "备注")
    private String UserInfoRemark;

    public String getUserLoginAccount() {
        return UserLoginAccount;
    }

    public void setUserLoginAccount(String UserLoginAccount) {
        this.UserLoginAccount = UserLoginAccount;
    }

    public String getUserLoginPassWord() {
        return UserLoginPassWord;
    }

    public void setUserLoginPassWord(String UserLoginPassWord) {
        this.UserLoginPassWord = UserLoginPassWord;
    }

    public String getUserLoginDBName() {
        return UserLoginDBName;
    }

    public void setUserLoginDBName(String UserLoginDBName) {
        this.UserLoginDBName = UserLoginDBName;
    }

    public String getUserInfoRemark() {
        return UserInfoRemark;
    }

    public void setUserInfoRemark(String UserInfoRemark) {
        this.UserInfoRemark = UserInfoRemark;
    }
}
