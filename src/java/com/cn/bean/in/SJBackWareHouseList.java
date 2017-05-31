/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean.in;

import com.cn.bean.ClassDescription;
import com.cn.bean.FieldDescription;

/**
 *
 * @author LFeng
 */
@ClassDescription(classDesc = "送检返回明细")
public class SJBackWareHouseList {

    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
//    @FieldDescription(description = "单内显示序号")
    private int listNumber;
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
//    @FieldDescription(description = "送检返回最大数量")
    private int sjCKAmount;
//    @FieldDescription(description = "部品单位")
    private String partUnit;
    @FieldDescription(description = "送检退库数量")
    private int sjTKAmount;
    @FieldDescription(description = "备注")
    private String sjBackWareHouseListRemark;
    @FieldDescription(description = "送检出库退库单号")
    private String sjBackWareHouseID;
    @FieldDescription(description = "库管员")
    private String wareHouseManagerName;

    public int getListNumber() {
        return listNumber;
    }

    public void setListNumber(int listNumber) {
        this.listNumber = listNumber;
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

    public int getSjTKAmount() {
        return sjTKAmount;
    }

    public void setSjTKAmount(int sjTKAmount) {
        this.sjTKAmount = sjTKAmount;
    }

    public String getSjBackWareHouseListRemark() {
        return sjBackWareHouseListRemark;
    }

    public void setSjBackWareHouseListRemark(String sjBackWareHouseListRemark) {
        this.sjBackWareHouseListRemark = sjBackWareHouseListRemark;
    }

    public String getSjBackWareHouseID() {
        return sjBackWareHouseID;
    }

    public void setSjBackWareHouseID(String sjBackWareHouseID) {
        this.sjBackWareHouseID = sjBackWareHouseID;
    }

    public String getWareHouseManagerName() {
        return wareHouseManagerName;
    }

    public void setWareHouseManagerName(String wareHouseManagerName) {
        this.wareHouseManagerName = wareHouseManagerName;
    }

    public int getSjCKAmount() {
        return sjCKAmount;
    }

    public void setSjCKAmount(int sjCKAmount) {
        this.sjCKAmount = sjCKAmount;
    }
}
