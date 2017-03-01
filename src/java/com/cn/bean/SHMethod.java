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
@ClassDescription(classDesc = "送货方式")
public class SHMethod {
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    
    @FieldDescription(description = "送货方式")
    private String shMethod;

    public String getShMethod() {
        return shMethod;
    }

    public void setShMethod(String shMethod) {
        this.shMethod = shMethod;
    }
}
