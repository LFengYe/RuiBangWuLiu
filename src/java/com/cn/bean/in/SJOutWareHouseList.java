/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean.in;

import com.cn.bean.FieldDescription;

/**
 *
 * @author LFeng
 */
public class SJOutWareHouseList {

    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
//    @FieldDescription(description = "单内显示序号")
//    private String listNumber;
    @FieldDescription(description = "供应商代码")
    private String supplierID;
//    @FieldDescription(description = "供应商名称")
    private String supplierName;
//    @FieldDescription(description = "部品编号")
    private String partID;
//    @FieldDescription(description = "部品名称")
    private String partName;
    @FieldDescription(description = "部品件号")
    private String partCode;
    @FieldDescription(description = "入库批次")
    private String inboundBatch;
    @FieldDescription(description = "入库数量(最大送检出库数)", operate = "display")
    private int inboundAmount;
//    @FieldDescription(description = "部品单位")
    private String partUnit;
    @FieldDescription(description = "送检出库数量")
    private String sjCKAmount;
    @FieldDescription(description = "备注")
    private String sjOutWareHouseListRemark;
    @FieldDescription(description = "送检出库退库单号")
    private String sjOutWareHouseID;
    @FieldDescription(description = "库管员")
    private String wareHouseManagerName;

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

    public String getInboundBatch() {
        return inboundBatch;
    }

    public void setInboundBatch(String inboundBatch) {
        this.inboundBatch = inboundBatch;
    }

    public String getPartUnit() {
        return partUnit;
    }

    public void setPartUnit(String partUnit) {
        this.partUnit = partUnit;
    }

    public String getSjCKAmount() {
        return sjCKAmount;
    }

    public void setSjCKAmount(String sjCKAmount) {
        this.sjCKAmount = sjCKAmount;
    }

    public String getSjOutWareHouseListRemark() {
        return sjOutWareHouseListRemark;
    }

    public void setSjOutWareHouseListRemark(String sjOutWareHouseListRemark) {
        this.sjOutWareHouseListRemark = sjOutWareHouseListRemark;
    }

    public String getSjOutWareHouseID() {
        return sjOutWareHouseID;
    }

    public void setSjOutWareHouseID(String sjOutWareHouseID) {
        this.sjOutWareHouseID = sjOutWareHouseID;
    }

    public String getWareHouseManagerName() {
        return wareHouseManagerName;
    }

    public void setWareHouseManagerName(String wareHouseManagerName) {
        this.wareHouseManagerName = wareHouseManagerName;
    }

    public int getInboundAmount() {
        return inboundAmount;
    }

    public void setInboundAmount(int inboundAmount) {
        this.inboundAmount = inboundAmount;
    }
}
