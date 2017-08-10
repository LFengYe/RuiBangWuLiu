/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean.report;

import com.cn.bean.FieldDescription;

/**
 *
 * @author LFeng
 */
public class JHCKCompletionAllInfo {

    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    @FieldDescription(description = "终端客户编号")
    private String zdCustomerID;
    @FieldDescription(description = "终端客户")
    private String zdCustomerName;
    @FieldDescription(description = "计划类型")
    private String jhType;
    @FieldDescription(description = "制单时间")
    private String jhProduceTime;
    @FieldDescription(description = "计划需求时间")
    private String jhDemandTime;
    @FieldDescription(description = "备货员")
    private String bhStaffName;
    @FieldDescription(description = "供应商编号")
    private String supplierID;
    @FieldDescription(description = "供应商")
    private String supplierName;
    @FieldDescription(description = "部品名称")
    private String partName;
    @FieldDescription(description = "部品件号")
    private String partCode;
    @FieldDescription(description = "部品单位")
    private String partUnit;
    @FieldDescription(description = "计划总数")
    private String jhTotal;
    @FieldDescription(description = "备注")
    private String jhOutWareHouseListRemark;
    @FieldDescription(description = "已备货")
    private String bhTotal;
    @FieldDescription(description = "未备货")
    private String wbHTotal;
    @FieldDescription(description = "备货完成时间")
    private String bhTime;
    @FieldDescription(description = "已领货")
    private String lhTotal;
    @FieldDescription(description = "未领货")
    private String wlHTotal;
    @FieldDescription(description = "领货完成时间")
    private String lhTime;
    @FieldDescription(description = "已上线")
    private String sxTotal;
    @FieldDescription(description = "未上线")
    private String wsXTotal;
    @FieldDescription(description = "上线完成时间")
    private String sxTime;

    public String getZdCustomerID() {
        return zdCustomerID;
    }

    public void setZdCustomerID(String zdCustomerID) {
        this.zdCustomerID = zdCustomerID;
    }

    public String getZdCustomerName() {
        return zdCustomerName;
    }

    public void setZdCustomerName(String zdCustomerName) {
        this.zdCustomerName = zdCustomerName;
    }

    public String getJhType() {
        return jhType;
    }

    public void setJhType(String jhType) {
        this.jhType = jhType;
    }

    public String getJhProduceTime() {
        return jhProduceTime;
    }

    public void setJhProduceTime(String jhProduceTime) {
        this.jhProduceTime = jhProduceTime;
    }

    public String getJhDemandTime() {
        return jhDemandTime;
    }

    public void setJhDemandTime(String jhDemandTime) {
        this.jhDemandTime = jhDemandTime;
    }

    public String getBhStaffName() {
        return bhStaffName;
    }

    public void setBhStaffName(String bhStaffName) {
        this.bhStaffName = bhStaffName;
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

    public String getPartUnit() {
        return partUnit;
    }

    public void setPartUnit(String partUnit) {
        this.partUnit = partUnit;
    }

    public String getJhTotal() {
        return jhTotal;
    }

    public void setJhTotal(String jhTotal) {
        this.jhTotal = jhTotal;
    }

    public String getJhOutWareHouseListRemark() {
        return jhOutWareHouseListRemark;
    }

    public void setJhOutWareHouseListRemark(String jhOutWareHouseListRemark) {
        this.jhOutWareHouseListRemark = jhOutWareHouseListRemark;
    }

    public String getBhTotal() {
        return bhTotal;
    }

    public void setBhTotal(String bhTotal) {
        this.bhTotal = bhTotal;
    }

    public String getWbHTotal() {
        return wbHTotal;
    }

    public void setWbHTotal(String wbHTotal) {
        this.wbHTotal = wbHTotal;
    }

    public String getBhTime() {
        return bhTime;
    }

    public void setBhTime(String bhTime) {
        this.bhTime = bhTime;
    }

    public String getLhTotal() {
        return lhTotal;
    }

    public void setLhTotal(String lhTotal) {
        this.lhTotal = lhTotal;
    }

    public String getWlHTotal() {
        return wlHTotal;
    }

    public void setWlHTotal(String wlHTotal) {
        this.wlHTotal = wlHTotal;
    }

    public String getLhTime() {
        return lhTime;
    }

    public void setLhTime(String lhTime) {
        this.lhTime = lhTime;
    }

    public String getSxTotal() {
        return sxTotal;
    }

    public void setSxTotal(String sxTotal) {
        this.sxTotal = sxTotal;
    }

    public String getWsXTotal() {
        return wsXTotal;
    }

    public void setWsXTotal(String wsXTotal) {
        this.wsXTotal = wsXTotal;
    }

    public String getSxTime() {
        return sxTime;
    }

    public void setSxTime(String sxTime) {
        this.sxTime = sxTime;
    }
}
