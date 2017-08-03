/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean.report;

/**
 *
 * @author LFeng
 */
public class ContainerAmount {
    private String supplierID;
    private String supplierName;
    private String containerName;
    private int containerIn;
    private int containerFXOut;
    private int containerFXIn;
    private int containerOut;
    private int containerTotal;
    private int containerLPTotal;
    private int containerBLPTotal;

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
    
}
