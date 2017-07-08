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
@ClassDescription(classDesc = "车型档案")
public class AutoStyling {
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    
    @FieldDescription(description = "车型名称", operate = "import")
    private String autoStylingName;
    @FieldDescription(description = "排序", operate = "import")
    private int orderFlag;

    public String getAutoStylingName() {
        return autoStylingName;
    }

    public void setAutoStylingName(String AutoStyling) {
        this.autoStylingName = AutoStyling;
    }

    public int getOrderFlag() {
        return orderFlag;
    }

    public void setOrderFlag(int orderFlag) {
        this.orderFlag = orderFlag;
    }
    
}
