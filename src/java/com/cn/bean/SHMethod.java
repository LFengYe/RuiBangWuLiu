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
    
    @FieldDescription(description = "送货方式", operate = "import")
    private String shMethodName;

    public String getShMethodName() {
        return shMethodName;
    }

    public void setShMethodName(String shMethodName) {
        this.shMethodName = shMethodName;
    }
}
