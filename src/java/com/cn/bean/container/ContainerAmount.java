/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean.container;

import com.cn.bean.FieldDescription;

/**
 *
 * @author LFeng
 */
public class ContainerAmount {
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    @FieldDescription(description = "供应商编号")
    private String supplierID;
    @FieldDescription(description = "供应商名称", operate = "display")
    private String supplierName;
    private String containerName;
    private String fxOutBatch;
    private int containerIn;
    private int containerFXOut;
    private int containerFXIn;
    private int containerFX;
    private int containerOut;
    private int containerTotal;

    public String getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(String supplierID) {
        this.supplierID = supplierID;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public int getContainerIn() {
        return containerIn;
    }

    public void setContainerIn(int containerIn) {
        this.containerIn = containerIn;
    }

    public int getContainerFXOut() {
        return containerFXOut;
    }

    public void setContainerFXOut(int containerFXOut) {
        this.containerFXOut = containerFXOut;
    }

    public int getContainerFXIn() {
        return containerFXIn;
    }

    public void setContainerFXIn(int containerFXIn) {
        this.containerFXIn = containerFXIn;
    }

    public int getContainerFX() {
        return containerFX;
    }

    public void setContainerFX(int containerFX) {
        this.containerFX = containerFX;
    }

    public int getContainerOut() {
        return containerOut;
    }

    public void setContainerOut(int containerOut) {
        this.containerOut = containerOut;
    }

    public int getContainerTotal() {
        return containerTotal;
    }

    public void setContainerTotal(int containerTotal) {
        this.containerTotal = containerTotal;
    }

    public String getFxOutBatch() {
        return fxOutBatch;
    }

    public void setFxOutBatch(String fxOutBatch) {
        this.fxOutBatch = fxOutBatch;
    }
    
    
}
