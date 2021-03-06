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
@ClassDescription(classDesc = "客户类别")
public class CustomerType {
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    
    @FieldDescription(description = "客户类别", operate = "import")
    private String customerTypeName;
    @FieldDescription(description = "客户角色代码", operate = "import")
    private int customerRoleCode;
    @FieldDescription(description = "客户角色名称", operate = "import")
    private String customerRoleName;

    public String getCustomerTypeName() {
        return customerTypeName;
    }

    public void setCustomerTypeName(String customerTypeName) {
        this.customerTypeName = customerTypeName;
    }

    public int getCustomerRoleCode() {
        return customerRoleCode;
    }

    public void setCustomerRoleCode(int customerRoleCode) {
        this.customerRoleCode = customerRoleCode;
    }

    public String getCustomerRoleName() {
        return customerRoleName;
    }

    public void setCustomerRoleName(String customerRoleName) {
        this.customerRoleName = customerRoleName;
    }
}
