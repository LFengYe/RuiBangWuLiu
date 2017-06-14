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
public class FZInWareHouse {
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    @FieldDescription(description = "分装入库单号")
    private String fzInWareHouseID;
    @FieldDescription(description = "制单人员姓名")
    private String fzRKProducerName;
    @FieldDescription(description = "制单时间")
    private String fzRKProduceTime;
    @FieldDescription(description = "审核人员姓名")
    private String fzRKAuditStaffName;
    @FieldDescription(description = "审核时间")
    private String fzRKAuditTime;
    @FieldDescription(description = "备注")
    private String fzInWareHouseRemark;

    public String getFzInWareHouseID() {
        return fzInWareHouseID;
    }

    public void setFzInWareHouseID(String fzInWareHouseID) {
        this.fzInWareHouseID = fzInWareHouseID;
    }

    public String getFzRKProducerName() {
        return fzRKProducerName;
    }

    public void setFzRKProducerName(String fzRKProducerName) {
        this.fzRKProducerName = fzRKProducerName;
    }

    public String getFzRKProduceTime() {
        return fzRKProduceTime;
    }

    public void setFzRKProduceTime(String fzRKProduceTime) {
        this.fzRKProduceTime = fzRKProduceTime;
    }

    public String getFzRKAuditStaffName() {
        return fzRKAuditStaffName;
    }

    public void setFzRKAuditStaffName(String fzRKAuditStaffName) {
        this.fzRKAuditStaffName = fzRKAuditStaffName;
    }

    public String getFzRKAuditTime() {
        return fzRKAuditTime;
    }

    public void setFzRKAuditTime(String fzRKAuditTime) {
        this.fzRKAuditTime = fzRKAuditTime;
    }

    public String getFzInWareHouseRemark() {
        return fzInWareHouseRemark;
    }

    public void setFzInWareHouseRemark(String fzInWareHouseRemark) {
        this.fzInWareHouseRemark = fzInWareHouseRemark;
    }
}
