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
    @FieldDescription(description = "盛具名称")
    private String containerName;
    @FieldDescription(description = "盛具状态")
    private String containerStatus;
    @FieldDescription(description = "操作类型")
    private String operateType;
    private int containerFXOutLP;
    private int containerFXOutBLP;
    private int containerFXInLP;
    private int containerFXInBLP;
    private int containerLPTotal;
    private int containerBLPTotal;
    private int containerFXLP;
    private int containerFXBLP;
    private int containerFX;

    public String getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(String supplierID) {
        this.supplierID = supplierID;
    }

    public String getContainerStatus() {
        return containerStatus;
    }

    public void setContainerStatus(String containerStatus) {
        this.containerStatus = containerStatus;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public int getContainerFXOutLP() {
        return containerFXOutLP;
    }

    public void setContainerFXOutLP(int containerFXOutLP) {
        this.containerFXOutLP = containerFXOutLP;
    }

    public int getContainerFXOutBLP() {
        return containerFXOutBLP;
    }

    public void setContainerFXOutBLP(int containerFXOutBLP) {
        this.containerFXOutBLP = containerFXOutBLP;
    }

    public int getContainerFXInLP() {
        return containerFXInLP;
    }

    public void setContainerFXInLP(int containerFXInLP) {
        this.containerFXInLP = containerFXInLP;
    }

    public int getContainerFXInBLP() {
        return containerFXInBLP;
    }

    public void setContainerFXInBLP(int containerFXInBLP) {
        this.containerFXInBLP = containerFXInBLP;
    }

    public int getContainerLPTotal() {
        return containerLPTotal;
    }

    public void setContainerLPTotal(int containerLPTotal) {
        this.containerLPTotal = containerLPTotal;
    }

    public int getContainerBLPTotal() {
        return containerBLPTotal;
    }

    public void setContainerBLPTotal(int containerBLPTotal) {
        this.containerBLPTotal = containerBLPTotal;
    }

    public int getContainerFXLP() {
        return containerFXLP;
    }

    public void setContainerFXLP(int containerFXLP) {
        this.containerFXLP = containerFXLP;
    }

    public int getContainerFXBLP() {
        return containerFXBLP;
    }

    public void setContainerFXBLP(int containerFXBLP) {
        this.containerFXBLP = containerFXBLP;
    }

    public int getContainerFX() {
        return containerFX;
    }

    public void setContainerFX(int containerFX) {
        this.containerFX = containerFX;
    }
}
