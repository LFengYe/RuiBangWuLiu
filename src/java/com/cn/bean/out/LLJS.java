/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.bean.out;

import com.cn.bean.ClassDescription;
import com.cn.bean.FieldDescription;

/**
 *
 * @author LFeng
 */
@ClassDescription(classDesc = "领料结算")
public class LLJS {

    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    @FieldDescription(description = "领料结算单据号")
    private String llJSID;
    @FieldDescription(description = "领料结算日期")
    private String llJSDate;
    @FieldDescription(description = "终端客户ID")
    private String zdCustomerID;
    @FieldDescription(description = "终端客户姓名", operate = "display")
    private String zdcustomerName;
    @FieldDescription(description = "领料结算制单员")
    private String llJSProducerName;
    @FieldDescription(description = "领料结算制单时间", type = "date")
    private String llJSProduceTime;
    @FieldDescription(description = "领料结算审核员")
    private String llJSAuditStaffName;
    @FieldDescription(description = "领料结算审核时间")
    private String llJSAuditTime;
    @FieldDescription(description = "领料结算备注")
    private String llJSRemark;

    public String getLlJSID() {
        return llJSID;
    }

    public void setLlJSID(String llJSID) {
        this.llJSID = llJSID;
    }

    public String getLlJSDate() {
        return llJSDate;
    }

    public void setLlJSDate(String llJSDate) {
        this.llJSDate = llJSDate;
    }

    public String getZdCustomerID() {
        return zdCustomerID;
    }

    public void setZdCustomerID(String zdCustomerID) {
        this.zdCustomerID = zdCustomerID;
    }

    public String getZdcustomerName() {
        return zdcustomerName;
    }

    public void setZdcustomerName(String zdcustomerName) {
        this.zdcustomerName = zdcustomerName;
    }

    public String getLlJSProducerName() {
        return llJSProducerName;
    }

    public void setLlJSProducerName(String llJSProducerName) {
        this.llJSProducerName = llJSProducerName;
    }

    public String getLlJSProduceTime() {
        return llJSProduceTime;
    }

    public void setLlJSProduceTime(String llJSProduceTime) {
        this.llJSProduceTime = llJSProduceTime;
    }

    public String getLlJSAuditStaffName() {
        return llJSAuditStaffName;
    }

    public void setLlJSAuditStaffName(String llJSAuditStaffName) {
        this.llJSAuditStaffName = llJSAuditStaffName;
    }

    public String getLlJSAuditTime() {
        return llJSAuditTime;
    }

    public void setLlJSAuditTime(String llJSAuditTime) {
        this.llJSAuditTime = llJSAuditTime;
    }

    public String getLlJSRemark() {
        return llJSRemark;
    }

    public void setLlJSRemark(String llJSRemark) {
        this.llJSRemark = llJSRemark;
    }
}
