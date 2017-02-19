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
public class AutoStyling {
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    
    private String autoStyling;

    public String getAutoStyling() {
        return autoStyling;
    }

    public void setAutoStyling(String AutoStyling) {
        this.autoStyling = AutoStyling;
    }
    
}
