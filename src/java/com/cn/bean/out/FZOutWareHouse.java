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
public class FZOutWareHouse {
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    @FieldDescription(description = "分装入库单号")
    private String fzOutWareHouseID;
    @FieldDescription(description = "制单人员姓名")
    private String fzCKProducerName;
    @FieldDescription(description = "制单时间")
    private String fzCKProduceTime;
    @FieldDescription(description = "审核人员姓名")
    private String fzCKAuditStaffName;
    @FieldDescription(description = "审核时间")
    private String fzCKAuditTime;
    @FieldDescription(description = "备注")
    private String fzOutWareHouseRemark;

    public String getFzOutWareHouseID() {
        return fzOutWareHouseID;
    }

    public void setFzOutWareHouseID(String fzOutWareHouseID) {
        this.fzOutWareHouseID = fzOutWareHouseID;
    }

    public String getFzCKProducerName() {
        return fzCKProducerName;
    }

    public void setFzCKProducerName(String fzCKProducerName) {
        this.fzCKProducerName = fzCKProducerName;
    }

    public String getFzCKProduceTime() {
        return fzCKProduceTime;
    }

    public void setFzCKProduceTime(String fzCKProduceTime) {
        this.fzCKProduceTime = fzCKProduceTime;
    }

    public String getFzCKAuditStaffName() {
        return fzCKAuditStaffName;
    }

    public void setFzCKAuditStaffName(String fzCKAuditStaffName) {
        this.fzCKAuditStaffName = fzCKAuditStaffName;
    }

    public String getFzCKAuditTime() {
        return fzCKAuditTime;
    }

    public void setFzCKAuditTime(String fzCKAuditTime) {
        this.fzCKAuditTime = fzCKAuditTime;
    }

    public String getFzOutWareHouseRemark() {
        return fzOutWareHouseRemark;
    }

    public void setFzOutWareHouseRemark(String fzOutWareHouseRemark) {
        this.fzOutWareHouseRemark = fzOutWareHouseRemark;
    }
}
