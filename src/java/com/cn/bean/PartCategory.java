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
@ClassDescription(classDesc = "部品类别")
public class PartCategory {
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    
    @FieldDescription(description = "部品类别", operate = "import")
    private String partCategoryName;
    @FieldDescription(description = "库管员", operate = "import")
    private String wareHouseManagerName;

    public String getPartCategoryName() {
        return partCategoryName;
    }

    public void setPartCategoryName(String partCategoryName) {
        this.partCategoryName = partCategoryName;
    }

    public String getWareHouseManagerName() {
        return wareHouseManagerName;
    }

    public void setWareHouseManagerName(String wareHouseManagerName) {
        this.wareHouseManagerName = wareHouseManagerName;
    }
}
