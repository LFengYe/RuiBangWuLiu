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
@ClassDescription(classDesc = "送检返回")
public class SJBackWareHouse {
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    
    @FieldDescription(description = "送检退库单号")
    private String sjBackWareHouseID;
    @FieldDescription(description = "供应商代码")
    private String supplierID;
//    @FieldDescription(description = "供应商名称")
    private String supplierName;
    @FieldDescription(description = "制单人员姓名")
    private String sjTKProducerName;
    @FieldDescription(description = "制单时间")
    private String sjTKProduceTime;
    @FieldDescription(description = "审核人员姓名")
    private String sjTKAuditStaffName;
    @FieldDescription(description = "审核时间")
    private String sjTKAuditTime;
    @FieldDescription(description = "打印标志")
    private String printFlag;
    @FieldDescription(description = "备注")
    private String sjBackWareHouseRemark;

    public String getSjBackWareHouseID() {
        return sjBackWareHouseID;
    }

    public void setSjBackWareHouseID(String sjBackWareHouseID) {
        this.sjBackWareHouseID = sjBackWareHouseID;
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

    public String getSjTKProducerName() {
        return sjTKProducerName;
    }

    public void setSjTKProducerName(String sjTKProducerName) {
        this.sjTKProducerName = sjTKProducerName;
    }

    public String getSjTKProduceTime() {
        return sjTKProduceTime;
    }

    public void setSjTKProduceTime(String sjTKProduceTime) {
        this.sjTKProduceTime = sjTKProduceTime;
    }

    public String getSjTKAuditStaffName() {
        return sjTKAuditStaffName;
    }

    public void setSjTKAuditStaffName(String sjTKAuditStaffName) {
        this.sjTKAuditStaffName = sjTKAuditStaffName;
    }

    public String getSjTKAuditTime() {
        return sjTKAuditTime;
    }

    public void setSjTKAuditTime(String sjTKAuditTime) {
        this.sjTKAuditTime = sjTKAuditTime;
    }

    public String getPrintFlag() {
        return printFlag;
    }

    public void setPrintFlag(String printFlag) {
        this.printFlag = printFlag;
    }

    public String getSjBackWareHouseRemark() {
        return sjBackWareHouseRemark;
    }

    public void setSjBackWareHouseRemark(String sjBackWareHouseRemark) {
        this.sjBackWareHouseRemark = sjBackWareHouseRemark;
    }
}
