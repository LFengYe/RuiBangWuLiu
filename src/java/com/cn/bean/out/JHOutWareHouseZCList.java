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
public class JHOutWareHouseZCList{
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    
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
    @FieldDescription(description = "总成数量", operate = "import")
    private int zcAmount;
    @FieldDescription(description = "计划出库单号")
    private String jhOutWareHouseID;
    @FieldDescription(description = "计划需求时间", operate = "display")
    private String jhDemandTime;
    @FieldDescription(description = "库房存放地址", operate = "display")
    private String kfCFAddress;
    @FieldDescription(description = "集配区存放地址", operate = "display")
    private String jpqCFAddress;
    @FieldDescription(description = "出库盛具", operate = "display")
    private String outboundContainerName;
    @FieldDescription(description = "包装数量", operate = "display")
    private int outboundPackageAmount;

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

    public String getJhOutWareHouseID() {
        return jhOutWareHouseID;
    }

    public void setJhOutWareHouseID(String jhOutWareHouseID) {
        this.jhOutWareHouseID = jhOutWareHouseID;
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

    public int getZcAmount() {
        return zcAmount;
    }

    public void setZcAmount(int zcAmount) {
        this.zcAmount = zcAmount;
    }

    public String getJhDemandTime() {
        return jhDemandTime;
    }

    public void setJhDemandTime(String jhDemandTime) {
        this.jhDemandTime = jhDemandTime;
    }

    public String getKfCFAddress() {
        return kfCFAddress;
    }

    public void setKfCFAddress(String kfCFAddress) {
        this.kfCFAddress = kfCFAddress;
    }

    public String getJpqCFAddress() {
        return jpqCFAddress;
    }

    public void setJpqCFAddress(String jpqCFAddress) {
        this.jpqCFAddress = jpqCFAddress;
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
}
