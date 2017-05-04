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
public class ZDBackWareHouse {

    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    @FieldDescription(description = "终端退库单据号")
    private String zdBackWareHouseID;
    @FieldDescription(description = "终端客户代码")
    private String zdCustomerID;
    @FieldDescription(description = "终端客户名称")
    private String zdCustomerName;
    @FieldDescription(description = "原存放区域")
    private String ycFArea;
    @FieldDescription(description = "退库类型")
    private String zdTKType;
    @FieldDescription(description = "终端退库制单员姓名")
    private String zdTKProducerName;
    @FieldDescription(description = "终端退库制单时间")
    private String zdTKProduceTime;
    @FieldDescription(description = "终端退库审核员姓名")
    private String zdTKAuditStaffName;
    @FieldDescription(description = "终端退库审核时间")
    private String zdTKAuditTime;
    @FieldDescription(description = "打印标志")
    private String printFlag;
    @FieldDescription(description = "终端退库备注")
    private String zdTKRemark;

    public String getZdBackWareHouseID() {
        return zdBackWareHouseID;
    }

    public void setZdBackWareHouseID(String zdBackWareHouseID) {
        this.zdBackWareHouseID = zdBackWareHouseID;
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

    public String getYcFArea() {
        return ycFArea;
    }

    public void setYcFArea(String ycFArea) {
        this.ycFArea = ycFArea;
    }

    public String getZdTKType() {
        return zdTKType;
    }

    public void setZdTKType(String zdTKType) {
        this.zdTKType = zdTKType;
    }

    public String getZdTKProducerName() {
        return zdTKProducerName;
    }

    public void setZdTKProducerName(String zdTKProducerName) {
        this.zdTKProducerName = zdTKProducerName;
    }

    public String getZdTKProduceTime() {
        return zdTKProduceTime;
    }

    public void setZdTKProduceTime(String zdTKProduceTime) {
        this.zdTKProduceTime = zdTKProduceTime;
    }

    public String getZdTKAuditStaffName() {
        return zdTKAuditStaffName;
    }

    public void setZdTKAuditStaffName(String zdTKAuditStaffName) {
        this.zdTKAuditStaffName = zdTKAuditStaffName;
    }

    public String getZdTKAuditTime() {
        return zdTKAuditTime;
    }

    public void setZdTKAuditTime(String zdTKAuditTime) {
        this.zdTKAuditTime = zdTKAuditTime;
    }

    public String getPrintFlag() {
        return printFlag;
    }

    public void setPrintFlag(String printFlag) {
        this.printFlag = printFlag;
    }

    public String getZdTKRemark() {
        return zdTKRemark;
    }

    public void setZdTKRemark(String zdTKRemark) {
        this.zdTKRemark = zdTKRemark;
    }
}
