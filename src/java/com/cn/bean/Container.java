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
@ClassDescription(classDesc = "盛具档案")
public class Container {
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    
    @FieldDescription(description = "盛具名称", operate = "import")
    private String containerName;
    @FieldDescription(description = "盛具面积", operate = "import")
    private float containerArea;

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public float getContainerArea() {
        return containerArea;
    }

    public void setContainerArea(float containerArea) {
        this.containerArea = containerArea;
    }
}
