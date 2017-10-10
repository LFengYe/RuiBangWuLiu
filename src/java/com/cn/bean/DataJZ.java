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
public class DataJZ {
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    
    @FieldDescription(description = "客户角色名称", operate = "display")
    private String jzyMonth;
    @FieldDescription(description = "客户角色名称", operate = "display")
    private String jzDateTime;
    @FieldDescription(description = "客户角色名称", operate = "display")
    private String jzRemark;

    public String getJzyMonth() {
        return jzyMonth;
    }

    public void setJzyMonth(String jzyMonth) {
        this.jzyMonth = jzyMonth;
    }

    public String getJzDateTime() {
        return jzDateTime;
    }

    public void setJzDateTime(String jzDateTime) {
        this.jzDateTime = jzDateTime;
    }

    public String getJzRemark() {
        return jzRemark;
    }

    public void setJzRemark(String jzRemark) {
        this.jzRemark = jzRemark;
    }
    
}
