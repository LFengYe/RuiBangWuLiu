/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean.out;

import com.cn.bean.FieldDescription;

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
    @FieldDescription(description = "单内显示序号", operate = "display")
    private int listNumber;
    @FieldDescription(description = "失败原因", operate = "display")
    private String failedReason;
    @FieldDescription(description = "供应商代码", operate = "import")
    private String supplierID;
    @FieldDescription(description = "供应商名称", operate = "display")
    private String supplierName;
    @FieldDescription(description = "部品编号", operate = "display")
    private String partID;
    @FieldDescription(description = "部品名称", operate = "display")
    private String partName;
    @FieldDescription(description = "部品件号", operate = "import")
    private String partCode;
    @FieldDescription(description = "入库批次")
    private String inboundBatch;
    @FieldDescription(description = "车型", operate = "display")
    private String autoStylingName;
    @FieldDescription(description = "数量", operate = "import")
    private int jhCKAmount;
    @FieldDescription(description = "缺少数量", operate = "display")
    private int shortAmount;
    @FieldDescription(description = "库存数量", operate = "display")
    private int kcCount;
    @FieldDescription(description = "出库盛具", operate = "display")
    private String outboundContainerName;
    @FieldDescription(description = "包装数量", operate = "display")
    private int outboundPackageAmount;
    @FieldDescription(description = "盛具数量")
    private int containerAmount;
    @FieldDescription(description = "备注", operate = "import")
    private String jhOutWareHouseListRemark;
    @FieldDescription(description = "计划出库单号")
    private String jhOutWareHouseID;
    @FieldDescription(description = "状态", operate = "display")
    private int jhStatus;
    @FieldDescription(description = "制单时间", operate = "display")
    private String jhCKProduceTime;
    @FieldDescription(description = "计划需求时间", operate = "display")
    private String jhDemandTime;
    @FieldDescription(description = "库房存放地址", operate = "display")
    private String kfCFAddress;
    
    public String getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(String supplierID) {
        this.supplierID = supplierID;
    }

    public String getPartCode() {
        return partCode;
    }

    public void setPartCode(String partCode) {
        this.partCode = partCode;
    }

    public String getInboundBatch() {
        return inboundBatch;
    }

    public void setInboundBatch(String inboundBatch) {
        this.inboundBatch = inboundBatch;
    }
    
    public int getJhCKAmount() {
        return jhCKAmount;
    }

    public void setJhCKAmount(int jhCKAmount) {
        this.jhCKAmount = jhCKAmount;
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

    public int getListNumber() {
        return listNumber;
    }

    public void setListNumber(int listNumber) {
        this.listNumber = listNumber;
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

    public String getAutoStylingName() {
        return autoStylingName;
    }

    public void setAutoStylingName(String autoStylingName) {
        this.autoStylingName = autoStylingName;
    }

    public String getOutboundContainerName() {
        return outboundContainerName;
    }

    public void setOutboundContainerName(String outboundContainerName) {
        this.outboundContainerName = outboundContainerName;
    }

    public String getFailedReason() {
        return failedReason;
    }

    public void setFailedReason(String failedReason) {
        this.failedReason = failedReason;
    }

    @Override
    public int compareTo(Object o) {
        JHOutWareHouseList list = (JHOutWareHouseList) o;
        return this.inboundBatch.compareTo(list.getInboundBatch());
    }

    public int getOutboundPackageAmount() {
        return outboundPackageAmount;
    }

    public void setOutboundPackageAmount(int outboundPackageAmount) {
        this.outboundPackageAmount = outboundPackageAmount;
    }

    public int getJhStatus() {
        return jhStatus;
    }

    public void setJhStatus(int jhStatus) {
        this.jhStatus = jhStatus;
    }

    public String getJhCKProduceTime() {
        return jhCKProduceTime;
    }

    public void setJhCKProduceTime(String jhCKProduceTime) {
        this.jhCKProduceTime = jhCKProduceTime;
    }

    public String getJhDemandTime() {
        return jhDemandTime;
    }

    public void setJhDemandTime(String jhDemandTime) {
        this.jhDemandTime = jhDemandTime;
    }

    public int getShortAmount() {
        return shortAmount;
    }

    public void setShortAmount(int shortAmount) {
        this.shortAmount = shortAmount;
    }

    public int getKcCount() {
        return kcCount;
    }

    public void setKcCount(int kcCount) {
        this.kcCount = kcCount;
    }

    public String getKfCFAddress() {
        return kfCFAddress;
    }

    public void setKfCFAddress(String kfCFAddress) {
        this.kfCFAddress = kfCFAddress;
    }
}
