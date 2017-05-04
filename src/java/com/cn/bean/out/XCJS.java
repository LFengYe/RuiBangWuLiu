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
@ClassDescription(classDesc = "现场结算")
public class XCJS {

    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    @FieldDescription(description = "现场结算单据号")
    private String xcJSID;
    @FieldDescription(description = "现场结算日期")
    private String xcJSDate;
    @FieldDescription(description = "终端客户ID")
    private String zdCustomerID;
    @FieldDescription(description = "终端客户姓名")
    private String zdcustomerName;
    @FieldDescription(description = "现场结算制单员")
    private String xcJSProducerName;
    @FieldDescription(description = "现场结算制单时间")
    private String xcJSProduceTime;
    @FieldDescription(description = "现场结算审核员")
    private String xcJSAuditStaffName;
    @FieldDescription(description = "现场结算审核时间")
    private String xcJSAuditTime;
    @FieldDescription(description = "现场结算备注")
    private String xcJSRemark;

    public String getXcJSID() {
        return xcJSID;
    }

    public void setXcJSID(String xcJSID) {
        this.xcJSID = xcJSID;
    }

    public String getXcJSDate() {
        return xcJSDate;
    }

    public void setXcJSDate(String xcJSDate) {
        this.xcJSDate = xcJSDate;
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

    public String getXcJSProducerName() {
        return xcJSProducerName;
    }

    public void setXcJSProducerName(String xcJSProducerName) {
        this.xcJSProducerName = xcJSProducerName;
    }

    public String getXcJSProduceTime() {
        return xcJSProduceTime;
    }

    public void setXcJSProduceTime(String xcJSProduceTime) {
        this.xcJSProduceTime = xcJSProduceTime;
    }

    public String getXcJSAuditStaffName() {
        return xcJSAuditStaffName;
    }

    public void setXcJSAuditStaffName(String xcJSAuditStaffName) {
        this.xcJSAuditStaffName = xcJSAuditStaffName;
    }

    public String getXcJSAuditTime() {
        return xcJSAuditTime;
    }

    public void setXcJSAuditTime(String xcJSAuditTime) {
        this.xcJSAuditTime = xcJSAuditTime;
    }

    public String getXcJSRemark() {
        return xcJSRemark;
    }

    public void setXcJSRemark(String xcJSRemark) {
        this.xcJSRemark = xcJSRemark;
    }
}
