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
    @FieldDescription(description = "部品件号")
    private String partCode;
    @FieldDescription(description = "部品名称", operate = "display")
    private String partName;
    @FieldDescription(description = "入库数量")
    private int inboundAmount;
    @FieldDescription(description = "入库箱数")
    private int inboundBoxAmount;
    @FieldDescription(description = "制单人员姓名")
    private String fzInProducerName;
    @FieldDescription(description = "制单时间", type = "date")
    private String fzInProduceTime;
    @FieldDescription(description = "备注")
    private String fzInRemark;

    public String getFzInWareHouseID() {
        return fzInWareHouseID;
    }

    public void setFzInWareHouseID(String fzInWareHouseID) {
        this.fzInWareHouseID = fzInWareHouseID;
    }

    public int getInboundAmount() {
        return inboundAmount;
    }

    public void setInboundAmount(int inboundAmount) {
        this.inboundAmount = inboundAmount;
    }

    public int getInboundBoxAmount() {
        return inboundBoxAmount;
    }

    public void setInboundBoxAmount(int inboundBoxAmount) {
        this.inboundBoxAmount = inboundBoxAmount;
    }

    public String getFzInProducerName() {
        return fzInProducerName;
    }

    public void setFzInProducerName(String fzInProducerName) {
        this.fzInProducerName = fzInProducerName;
    }

    public String getFzInProduceTime() {
        return fzInProduceTime;
    }

    public void setFzInProduceTime(String fzInProduceTime) {
        this.fzInProduceTime = fzInProduceTime;
    }

    public String getFzInRemark() {
        return fzInRemark;
    }

    public void setFzInRemark(String fzInRemark) {
        this.fzInRemark = fzInRemark;
    }

    public String getPartCode() {
        return partCode;
    }

    public void setPartCode(String partCode) {
        this.partCode = partCode;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }
}
