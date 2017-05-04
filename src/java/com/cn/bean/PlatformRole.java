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
@ClassDescription(classDesc = "定义角色")
public class PlatformRole {
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    
    @FieldDescription(description = "角色代码")
    private String roleCode;
    @FieldDescription(description = "角色名称")
    private String roleName;

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String RoleCode) {
        this.roleCode = RoleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String RoleName) {
        this.roleName = RoleName;
    }
}
