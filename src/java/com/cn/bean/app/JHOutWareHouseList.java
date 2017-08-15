/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean.app;


/**
 *
 * @author LFeng
 */
public class JHOutWareHouseList implements Comparable{
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    
    private String jhType;
    private String zdCustomerID;
    private String zdCustomerName;
    private String jhDemandTime;
    private String jhCKProducerName;
    private String supplierID;
    private String supplierName;
    private String partID;
    private String partName;
    private String partCode;
    private String autoStylingName;
    private String inboundBatch;
    private int jhCKAmount;
    private int bhFinishedAmount;
    private String bhEmployeeName;
    private String assemblingStation;
    private String outboundContainerName;
    private int outboundPackageAmount;
    private int containerAmount;
    private String jhOutWareHouseListRemark;
    private String jhOutWareHouseID;
    private int jhStatus;
    private String jhStatusName;
    
    @Override
    public int compareTo(Object o) {
        JHOutWareHouseList list = (JHOutWareHouseList) o;
        return this.inboundBatch.compareTo(list.getInboundBatch());
    }

    public String getJhType() {
        return jhType;
    }

    public void setJhType(String jhType) {
        this.jhType = jhType;
    }

    public String getZdCustomerName() {
        return zdCustomerName;
    }

    public void setZdCustomerName(String zdCustomerName) {
        this.zdCustomerName = zdCustomerName;
    }

    public String getJhDemandTime() {
        return jhDemandTime;
    }

    public void setJhDemandTime(String jhDemandTime) {
        this.jhDemandTime = jhDemandTime;
    }

    public String getJhCKProducerName() {
        return jhCKProducerName;
    }

    public void setJhCKProducerName(String jhCKProducerName) {
        this.jhCKProducerName = jhCKProducerName;
    }

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

    public String getPartID() {
        return partID;
    }

    public void setPartID(String partID) {
        this.partID = partID;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getPartCode() {
        return partCode;
    }

    public void setPartCode(String partCode) {
        this.partCode = partCode;
    }

    public String getAutoStylingName() {
        return autoStylingName;
    }

    public void setAutoStylingName(String autoStylingName) {
        this.autoStylingName = autoStylingName;
    }

    public int getJhCKAmount() {
        return jhCKAmount;
    }

    public void setJhCKAmount(int jhCKAmount) {
        this.jhCKAmount = jhCKAmount;
    }

    public String getOutboundContainerName() {
        return outboundContainerName;
    }

    public void setOutboundContainerName(String outboundContainerName) {
        this.outboundContainerName = outboundContainerName;
    }

    public int getOutboundPackageAmount() {
        return outboundPackageAmount;
    }

    public void setOutboundPackageAmount(int outboundPackageAmount) {
        this.outboundPackageAmount = outboundPackageAmount;
    }

    public int getContainerAmount() {
        return containerAmount;
    }

    public void setContainerAmount(int containerAmount) {
        this.containerAmount = containerAmount;
    }

    public String getJhOutWareHouseListRemark() {
        return jhOutWareHouseListRemark;
    }

    public void setJhOutWareHouseListRemark(String jhOutWareHouseListRemark) {
        this.jhOutWareHouseListRemark = jhOutWareHouseListRemark;
    }

    public String getJhOutWareHouseID() {
        return jhOutWareHouseID;
    }

    public void setJhOutWareHouseID(String jhOutWareHouseID) {
        this.jhOutWareHouseID = jhOutWareHouseID;
    }

    public String getInboundBatch() {
        return inboundBatch;
    }

    public void setInboundBatch(String inboundBatch) {
        this.inboundBatch = inboundBatch;
    }

    public int getBhFinishedAmount() {
        return bhFinishedAmount;
    }

    public void setBhFinishedAmount(int bhFinishedAmount) {
        this.bhFinishedAmount = bhFinishedAmount;
    }

    public String getBhEmployeeName() {
        return bhEmployeeName;
    }

    public void setBhEmployeeName(String bhEmployeeName) {
        this.bhEmployeeName = bhEmployeeName;
    }

    public String getZdCustomerID() {
        return zdCustomerID;
    }

    public void setZdCustomerID(String zdCustomerID) {
        this.zdCustomerID = zdCustomerID;
    }

    public int getJhStatus() {
        return jhStatus;
    }

    public void setJhStatus(int jhStatus) {
        this.jhStatus = jhStatus;
    }

    public String getJhStatusName() {
        return jhStatusName;
    }

    public void setJhStatusName(String jhStatusName) {
        this.jhStatusName = jhStatusName;
    }

    public String getAssemblingStation() {
        return assemblingStation;
    }

    public void setAssemblingStation(String assemblingStation) {
        this.assemblingStation = assemblingStation;
    }
}
