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
public class FJHOutWareHouse {
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    @FieldDescription(description = "非计划出库单号")
    private String fjhOutWareHouseID;
    @FieldDescription(description = "非终端客户")
    private String fzdCustomerID;
    @FieldDescription(description = "非终端客户", operate = "display")
    private String fzdCustomerName;
    @FieldDescription(description = "发货人员名称")
    private String fhStaffName;
    @FieldDescription(description = "制单人员姓名")
    private String fjhCKProducerName;
    @FieldDescription(description = "制单时间")
    private String fjhCKProduceTime;
    @FieldDescription(description = "打印标志")
    private String printFlag;
    @FieldDescription(description = "备注")
    private String fjhOutWareHouseRemark;

    public String getFjhOutWareHouseID() {
        return fjhOutWareHouseID;
    }

    public void setFjhOutWareHouseID(String fjhOutWareHouseID) {
        this.fjhOutWareHouseID = fjhOutWareHouseID;
    }

    public String getFzdCustomerID() {
        return fzdCustomerID;
    }

    public void setFzdCustomerID(String fzdCustomerID) {
        this.fzdCustomerID = fzdCustomerID;
    }

    public String getFzdCustomerName() {
        return fzdCustomerName;
    }

    public void setFzdCustomerName(String fzdCustomerName) {
        this.fzdCustomerName = fzdCustomerName;
    }

    public String getFhStaffName() {
        return fhStaffName;
    }

    public void setFhStaffName(String fhStaffName) {
        this.fhStaffName = fhStaffName;
    }

    public String getFjhCKProducerName() {
        return fjhCKProducerName;
    }

    public void setFjhCKProducerName(String fjhCKProducerName) {
        this.fjhCKProducerName = fjhCKProducerName;
    }

    public String getFjhCKProduceTime() {
        return fjhCKProduceTime;
    }

    public void setFjhCKProduceTime(String fjhCKProduceTime) {
        this.fjhCKProduceTime = fjhCKProduceTime;
    }

    public String getPrintFlag() {
        return printFlag;
    }

    public void setPrintFlag(String printFlag) {
        this.printFlag = printFlag;
    }

    public String getFjhOutWareHouseRemark() {
        return fjhOutWareHouseRemark;
    }

    public void setFjhOutWareHouseRemark(String fjhOutWareHouseRemark) {
        this.fjhOutWareHouseRemark = fjhOutWareHouseRemark;
    }
}
