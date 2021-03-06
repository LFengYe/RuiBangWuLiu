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
public class JHOutWareHouse {
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }

    @FieldDescription(description = "计划出库单号")
    private String jhOutWareHouseID;
    @FieldDescription(description = "终端客户代码")
    private String zdCustomerID;
    @FieldDescription(description = "终端客户名称", operate = "display")
    private String zdCustomerName;
    @FieldDescription(description = "计划需求时间")
    private String jhDemandTime;
    @FieldDescription(description = "计划类型")
    private String jhType;
    @FieldDescription(description = "计划方式")
    private String jhMethod;
    @FieldDescription(description = "备货人员姓名")
    private String bhStaffName;
    @FieldDescription(description = "计划班次")
    private String jhShift;
    @FieldDescription(description = "计划出库制单人员姓名")
    private String jhCKProducerName;
    @FieldDescription(description = "计划出库制单时间", type = "date")
    private String jhCKProduceTime;
    @FieldDescription(description = "打印标志")
    private String printFlag;
    @FieldDescription(description = "备注")
    private String jhOutWareHouseRemark;
    @FieldDescription(description = "计划是否确认")
    private String jhConfirm;

    public String getJhOutWareHouseID() {
        return jhOutWareHouseID;
    }

    public void setJhOutWareHouseID(String jhOutWareHouseID) {
        this.jhOutWareHouseID = jhOutWareHouseID;
    }

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

    public String getJhDemandTime() {
        return jhDemandTime;
    }

    public void setJhDemandTime(String jhDemandTime) {
        this.jhDemandTime = jhDemandTime;
    }

    public String getJhType() {
        return jhType;
    }

    public void setJhType(String jhType) {
        this.jhType = jhType;
    }

    public String getBhStaffName() {
        return bhStaffName;
    }

    public void setBhStaffName(String bhStaffName) {
        this.bhStaffName = bhStaffName;
    }

    public String getJhShift() {
        return jhShift;
    }

    public void setJhShift(String jhShift) {
        this.jhShift = jhShift;
    }

    public String getJhCKProducerName() {
        return jhCKProducerName;
    }

    public void setJhCKProducerName(String jhCKProducerName) {
        this.jhCKProducerName = jhCKProducerName;
    }

    public String getJhCKProduceTime() {
        return jhCKProduceTime;
    }

    public void setJhCKProduceTime(String jhCKProduceTime) {
        this.jhCKProduceTime = jhCKProduceTime;
    }

    public String getPrintFlag() {
        return printFlag;
    }

    public void setPrintFlag(String printFlag) {
        this.printFlag = printFlag;
    }

    public String getJhOutWareHouseRemark() {
        return jhOutWareHouseRemark;
    }

    public void setJhOutWareHouseRemark(String jhOutWareHouseRemark) {
        this.jhOutWareHouseRemark = jhOutWareHouseRemark;
    }

    public String getJhMethod() {
        return jhMethod;
    }

    public void setJhMethod(String jhMethod) {
        this.jhMethod = jhMethod;
    }

    public String getJhConfirm() {
        return jhConfirm;
    }

    public void setJhConfirm(String jhConfirm) {
        this.jhConfirm = jhConfirm;
    }
}
